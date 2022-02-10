package com.wwh.rpm.client.pool;

import java.util.Date;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.subconnection.SubconnectionProvider;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.protocol.packet.transport.ClosePacket;
import com.wwh.rpm.protocol.packet.transport.TransportPacket;

import io.netty.channel.Channel;

/**
 * 连接对象接口
 * 
 * @author wangwh
 * @date 2022-1-5
 */
public interface RpmConnection {

    /**
     * 启动连接
     * 
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 关闭连接
     */
    void shutdown();

    /**
     * 获取连接池
     * 
     * @return
     */
    ConnectionPool getConnectionPool();

    /**
     * 获取连接的ID
     * 
     * @return
     */
    Integer getConnectionId();

    /**
     * 获取token
     * 
     * @return
     */
    String getToken();

    /**
     * 获取客户端配置
     * 
     * @return
     */
    ClientConfig getClientConfig();

    /**
     * 获取netty Channel
     * 
     * @return
     */
    Channel getChannel();

    /**
     * 连接已经建立
     * 
     * @return
     */
    default boolean alreadyEstablished() {
        Channel channel = getChannel();
        if (channel != null) {
            return channel.isActive();
        }
        return false;
    }

    /**
     * 获取连接建立时间
     * 
     * @return
     */
    Date getEstablishTime();

    /**
     * 检查连接
     * 
     * @return
     */
    default boolean isOk() {

        Channel channel = getChannel();
        if (channel != null) {
            return channel.isActive();
        }
        return false;

    }

    /**
     * 将转发指令包发送到服务端
     * 
     * @param id
     * @param host
     * @param port
     */
    default void writeForwardPacket(Long id, String host, int port) {
        ForwardCommandPacket forwardPacket = new ForwardCommandPacket(id, host, port);
        getChannel().writeAndFlush(forwardPacket);
    }

    /**
     * 写数据到服务端
     * 
     * @param id
     * @param data
     */
    default void writeData2Server(Long id, byte[] data) {
        TransportPacket tPacket = new TransportPacket(id, data);
        getChannel().writeAndFlush(tPacket);
    }

    /**
     * 关闭远程子连接
     * 
     * @param id
     */
    default void closeRemoteSubChannel(long id) {
        ClosePacket cPacket = new ClosePacket(id);
        getChannel().writeAndFlush(cPacket);
    }

    /**
     * 获取缓冲管理器
     * 
     * @return
     */
    default BufferManager getBufferManager() {
        return getConnectionPool().getBufferManager();
    }

    default SubconnectionProvider getSubconnectionProvider() {
        return getConnectionPool().getSubconnectionProvider();
    }
}
