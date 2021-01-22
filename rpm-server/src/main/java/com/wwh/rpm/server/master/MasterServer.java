package com.wwh.rpm.server.master;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.server.ServerManager;
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
        clientTokenManager.regist(cid, token, channel);
    }

    public void unregistClient(String cid) {
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
}
