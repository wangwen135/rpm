package com.wwh.rpm.server.master;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;

import io.netty.channel.Channel;

/**
 * 转发管理器
 * 
 * @author wangwh
 * @date 2021-2-1
 */
@Deprecated
public class ForwardManagerSynch {

    private static final Logger logger = LoggerFactory.getLogger(ForwardManagerSynch.class);

    private AtomicLong idGenerator = new AtomicLong(0);

    private MasterServer masterServer;

    /**
     * 保存转发通道
     */
    private Map<Long, Composition> forwardChannelMap = new ConcurrentHashMap<>();

    public ForwardManagerSynch(MasterServer masterServer) {
        this.masterServer = masterServer;
    }

    private Long nextId() {
        return idGenerator.incrementAndGet();
    }

    /**
     * 判断ID是否还存在，超时之后会被移除
     * 
     * @param id
     * @return
     */
    public boolean idExist(long id) {
        return forwardChannelMap.containsKey(id);
    }

    /**
     * 收到一个客户端的转发通道
     * 
     * @param id
     * @param forwardChannel
     */
    public void receiveClientChannel(long id, Channel forwardChannel) {
        Composition c = forwardChannelMap.get(id);
        if (c == null) {
            throw new RPMException("Id对应的等待对象不存在，可能超时了");
        }
        c.setChannel(forwardChannel);
        synchronized (c) {
            c.notify();
        }
    }

    /**
     * 接收客户端转发通道异常
     * 
     * @param id
     * @param cause
     */
    public void receiveClientChannelError(long id, Throwable cause) {
        Composition c = forwardChannelMap.get(id);
        if (c == null) {
            throw new RPMException("Id对应的等待对象不存在，可能超时了");
        }
        c.setError(cause);
        synchronized (c) {
            c.notify();
        }
    }

    /**
     * 获取一个客户端的转发通道，阻塞
     * 
     * @param forwardConfig
     * @return 成功时返回通道，失败或超时抛出异常
     */
    public Channel acquireClientForwardChannel(ForwardOverClient forwardConfig) {
        String clientId = forwardConfig.getClientId();
        // 先找客户端是否存在
        Channel clientMasterChannel = masterServer.getChannelByCid(clientId);
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

        Composition c = new Composition();
        forwardChannelMap.put(id, c);

        synchronized (c) {
            try {
                c.wait(Constants.ACQUIRE_CLIENT_FORWARD_CHANNEL_TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("等待客户端转发通道时被中断");
            }
        }

        forwardChannelMap.remove(id);
        Channel transferChannel = c.get();
        if (transferChannel == null) {
            throw new RPMException("没有获取到客户端的转发通道");
        }
        return transferChannel;
    }

    class Composition {
        private volatile Channel channel;
        private volatile Throwable cause;

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public void setError(Throwable cause) {
            this.cause = cause;
        }

        public Channel get() {
            if (cause != null) {
                if (cause instanceof RPMException) {
                    throw (RPMException) cause;
                } else {
                    throw new RPMException(cause);
                }
            }
            return channel;
        }
    }
}
