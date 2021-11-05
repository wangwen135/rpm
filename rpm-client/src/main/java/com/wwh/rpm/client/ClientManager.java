package com.wwh.rpm.client;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.connection.ConnectionProvider;
import com.wwh.rpm.client.subserver.SubserverManager;
import com.wwh.rpm.ctrl.Closeer;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 客户端管理器
 * 
 * @author wangwh
 */
public class ClientManager implements Closeer {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private ClientConfig config;
    private BaseClient baseClient;
    private SubserverManager subserverManager;

    private ConnectionProvider connectionProvider;

    private static Object lock = new Object();

    // 一次性
    private AtomicBoolean isStartup = new AtomicBoolean(false);

    private EventLoopGroup workerGroup;

    public ClientManager(ClientConfig config) {
        this.config = config;
        baseClient = new BaseClient(this);
        subserverManager = new SubserverManager(this);
        connectionProvider = new ConnectionProvider(this);
        // 创建线程池
        workerGroup = new NioEventLoopGroup();
    }

    public void startClient() throws Exception {
        if (!isStartup.compareAndSet(false, true)) {
            logger.error("客户端已是启动状态 ！");
            return;
        }

        synchronized (lock) {
            logger.info("启动客户端...");
            baseClient.start();

            logger.debug("等待客户端注册完成...");
            baseClient.waitToken();

            logger.info("开始启动子服务...");
            subserverManager.startAll();
        }
    }

    public void shutdownClient() {
        synchronized (lock) {
            logger.info("关闭客户端...");
            baseClient.shutdown();

            logger.info("关闭子服务...");
            subserverManager.stopAll();

            logger.info("关闭线程池...");
            workerGroup.shutdownGracefully();
        }
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public String getToken() {
        return baseClient.getToken();
    }

    public ClientConfig getConfig() {
        return config;
    }

    public BaseClient getBaseClient() {
        return baseClient;
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    @Override
    public void close() {
        ClientStarter.shutdownNotify();
    }

}
