package com.wwh.rpm.client.pool;

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
     * 获取ID
     * 
     * @return
     */
    Integer getId();

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
}
