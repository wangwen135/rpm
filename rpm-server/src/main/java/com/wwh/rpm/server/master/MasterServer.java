package com.wwh.rpm.server.master;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.server.ServerManager;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.handler.MasterHandlerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MasterServer {

    private static final Logger logger = LoggerFactory.getLogger(MasterServer.class);

    private ServerManager serverManager;

    private ClientTokenManager clientTokenManager;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private AtomicLong idGenerator = new AtomicLong(0);

    private Channel channel;
    // 客户端列表

    public MasterServer(ServerManager serverManager) {
        this.serverManager = serverManager;
        this.clientTokenManager = new ClientTokenManager();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void shutdown() {
        if (!isRunning()) {
            logger.warn("主服务没有启动");
            return;
        }
        if (channel != null && channel.isActive()) {
            channel.close();
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("主服务正在运行！");
            return;
        }

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        b.childOption(ChannelOption.TCP_NODELAY, true);

        b.childHandler(new MasterHandlerInitializer(this));

        channel = b.bind(getConfig().getHost(), getConfig().getPort()).sync().channel();

        RpmMsgPrinter.printMsg("主服务启动在 {}:{}", getConfig().getHost(), getConfig().getPort());

        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("主服务关闭！");
                serverManager.close();
            }
        });

    }

    public ServerConfig getConfig() {
        return serverManager.getConfig();
    }

    /**
     * 注册客户端
     * 
     * @param cid
     * @param token
     * @param channel
     */
    public void registClient(String cid, String token, Channel channel) {
        RpmMsgPrinter.printMsg("客户端注册！ cid={}  address={}", cid, channel.remoteAddress());
        clientTokenManager.regist(cid, token, channel);
    }

    public void unregistClient(String cid) {
        RpmMsgPrinter.printMsg("客户端注销！ cid={}", cid);
        clientTokenManager.unregist(cid);
    }

    public String validateToken(String token) {
        String cid = clientTokenManager.getCidByToken(token);
        if (cid == null) {
            throw new RPMException("无效的token");
        } else {
            return cid;
        }
    }

    private Long nextId() {
        return idGenerator.incrementAndGet();
    }

    // --------------------------------------------

    /**
     * 保存转发通道
     */
    private Map<Long, Channel> forwardChannelMap = new ConcurrentHashMap<>();
    /**
     * 保存等待对象
     */
    private Map<Long, Object> waitObjMap = new ConcurrentHashMap<>();

    public void receiveClientChannel(long id, Channel forwardChannel) {
        Object lock = waitObjMap.get(id);
        if (lock == null) {
            throw new RPMException("Id对应的等待对象不存在，可能超时了");
        }
        forwardChannelMap.put(id, forwardChannel);
        synchronized (lock) {
            lock.notify();
        }
    }

    public Channel acquireClientForwardChannel(ForwardOverClient forwardConfig, Channel inboundChannel) {
        String clientId = forwardConfig.getClientId();
        // 先找客户端是否存在
        Channel clientMasterChannel = clientTokenManager.getChannelByCid(clientId);
        if (clientMasterChannel == null) {
            throw new RPMException("客户端：" + clientId + " 未上线");
        }
        if (!clientMasterChannel.isActive()) {
            throw new RPMException("客户端：" + clientId + " 主连接不是活动状态");
        }

        // 像客户端发送转发指令包
        ForwardCommandPacket fcPacket = new ForwardCommandPacket();
        Long id = nextId();
        fcPacket.setId(id);
        fcPacket.setHost(forwardConfig.getForwardHost());
        fcPacket.setPort(forwardConfig.getForwardPort());

        clientMasterChannel.writeAndFlush(fcPacket);

        Object obj = new Object();
        waitObjMap.put(id, obj);

        synchronized (obj) {
            try {
                obj.wait(Constants.ACQUIRE_CLIENT_FORWARD_CHANNEL_TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("等待客户端转发通道时被中断");
            }
        }
        waitObjMap.remove(id);
        Channel transferChannel = forwardChannelMap.remove(id);
        if (transferChannel == null) {
            throw new RPMException("没有获取到客户端的转发通道");
        }

        return transferChannel;
    }
}
