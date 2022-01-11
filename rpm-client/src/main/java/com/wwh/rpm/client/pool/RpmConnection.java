package com.wwh.rpm.client.pool;

import java.util.Date;

import com.wwh.rpm.client.config.pojo.ClientConfig;

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
    boolean alreadyEstablished();

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
    boolean checkConnection();

    /**
     * 写数据到服务端
     * 
     * @param id
     * @param data
     */
    void writeData2Server(long id, byte[] data);
}
