package com.wwh.rpm.client;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.pool.ConnectionPool;
import com.wwh.rpm.client.subconnection.SubconnectionProvider;
import com.wwh.rpm.client.subserver.SubserverManager;
import com.wwh.rpm.ctrl.Closeer;

/**
 * 客户端管理器
 * 
 * @author wangwh
 */
public class ClientManager implements Closeer {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private ClientConfig config;

    private ConnectionPool connectionPool;

    private SubserverManager subserverManager;

    private SubconnectionProvider subconnectionProvider;

    private static Object lock = new Object();

    // 一次性
    private AtomicBoolean isStartup = new AtomicBoolean(false);

    /**
     * 全局ID生成器<br>
     * 负责全部子连接的ID生成
     */
    private AtomicLong idGenerator = new AtomicLong(0);

    /**
     * 获取ID
     * 
     * @return
     */
    public long getNextId() {
        return idGenerator.incrementAndGet();
    }

    // 改成启动连接池

    // 再启动子服务

    public ClientManager(ClientConfig config) {
        this.config = config;

        connectionPool = new ConnectionPool(this);

        subserverManager = new SubserverManager(this);

        subconnectionProvider = new SubconnectionProvider(this);

    }

    public void startClient() throws Exception {
        if (!isStartup.compareAndSet(false, true)) {
            logger.error("客户端已是启动状态 ！");
            return;
        }

        synchronized (lock) {
            logger.info("启动连接池...");
            connectionPool.startPool();

            logger.info("开始启动子服务...");
            subserverManager.startAll();
        }
    }

    public void shutdownClient() {
        synchronized (lock) {

            logger.info("关闭子连接...");
            subconnectionProvider.close();

            logger.info("关闭子服务...");
            subserverManager.stopAll();

            logger.info("关闭连接池...");
            connectionPool.shutdownPool();

        }
    }

    public String getToken() {
        return connectionPool.getToken();
    }

    public ClientConfig getConfig() {
        return config;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public SubconnectionProvider getSubconnectionProvider() {
        return subconnectionProvider;
    }

    @Override
    public void close() {
        ClientStarter.shutdownNotify();
    }

}
