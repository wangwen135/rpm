package com.wwh.rpm.client.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConfig;
import com.wwh.rpm.client.pool.connection.CommonConnection;
import com.wwh.rpm.client.pool.connection.RegisterConnection;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;

import io.netty.channel.Channel;

/**
 * 连接池<br>
 * 注意线程安全问题
 * 
 * @author wangwh
 * @date 2021-12-28
 */
public class ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private ClientConfig clientConfig;

    /**
     * 获取连接计数器
     */
    private AtomicLong counter = new AtomicLong(0);

    /**
     * ID 生成器
     */
    private AtomicInteger idCreater = new AtomicInteger(0);

    /**
     * 主连接
     */
    private RegisterConnection regConnection;

    /**
     * 通信连接
     */
    private Map<Integer, RpmConnection> commConnectionMap = new ConcurrentHashMap<>();

    private Map<Integer, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 服务端返回的token
     */
    private String token;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public ConnectionPool(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;

    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public String getToken() {
        return token;
    }

    /**
     * 启动连接池<br>
     * 阻塞，直到获取到token
     * 
     * @throws Exception
     */
    public void startPool() throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("连接池已经启动！");
            return;
        }

        logger.debug("启动连接池...");
        // --- 先启动注册连接，一个客户端只能注册一次，避免有多个客户端ID相同时注册的对不上
        regConnection = new RegisterConnection(this, 0);
        regConnection.start();
        // 主的也保存一下
        commConnectionMap.put(0, regConnection);

        // --- 获取到token，剩下的用token注册
        token = regConnection.waitToken();
        logger.debug("获取到token：{}", token);

        // --- 再启动剩余的
        initCommonConnection();
    }

    public void shutdownPool() {
        if (!isRunning()) {
            logger.warn("连接池没有启动");
            return;
        }
    }

    public int getPoolSize() {
        return clientConfig.getPool().getPoolSize();
    }

    /**
     * 启动普通连接
     */
    private void initCommonConnection() {
        for (int i = 0; i < getPoolSize() - 1; i++) {
            int id = idCreater.incrementAndGet();
            CommonConnection connection = new CommonConnection(this, id);
            commConnectionMap.put(id, connection);
            try {
                connection.start();
            } catch (Exception e) {
                logger.error("启动通信连接异常", e);
                connection.shutdown();
                commConnectionMap.remove(id);
            }
        }
    }

    /**
     * 注册通道
     * 
     * @param channel
     */
    public void registerChannel(Integer id, Channel channel) {
        channelMap.put(id, channel);
    }

    /**
     * 取消注册
     * 
     * @param id
     */
    public void unregisterChannel(Integer id) {
        commConnectionMap.remove(id);
        channelMap.remove(id);
    }

    /**
     * 获取连接
     * 
     * @return
     */
    public Channel getChannel() {
        long index = counter.incrementAndGet();

        return null;
    }

    // 连接池管理线程

    // 关闭连接池

    public CommunicationConfig getCommunicationConfig() {
        return clientConfig.getCommunication();
    }

    public ServerConfig getServerConf() {
        return clientConfig.getServerConf();
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    /**
     * 通信中断
     */
    public void communicationInterrupt() {

    }

}
