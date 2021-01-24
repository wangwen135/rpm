package com.wwh.rpm.client;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.subserver.SubserverManager;
import com.wwh.rpm.ctrl.Closeer;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ClientManager implements Closeer {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private ClientConfig config;
    private BaseClient baseClient;
    private SubserverManager subserverManager;

    // 一次性
    private AtomicBoolean isStartup = new AtomicBoolean(false);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public ClientManager(ClientConfig config) {
        this.config = config;
        baseClient = new BaseClient(this);
        subserverManager = new SubserverManager(this);

        // 创建线程池
        int bossPoolSize = config.getForwardOverServer() == null ? 1 : config.getForwardOverServer().size();
        bossGroup = new NioEventLoopGroup(bossPoolSize);
        workerGroup = new NioEventLoopGroup();
    }

    public void startClient() throws Exception {
        if (!isStartup.compareAndSet(false, true)) {
            logger.error("客户端已是启动状态 ！");
            return;
        }

        logger.info("启动客户端...");
        baseClient.start(workerGroup);

        logger.debug("等待客户端注册完成...");
        baseClient.waitToken();

        logger.info("开始启动子服务...");
        subserverManager.startAll(bossGroup, workerGroup);
    }

    public void shutdownClient() {
        logger.info("关闭客户端...");
        baseClient.shutdown();

        logger.info("关闭子服务...");
        subserverManager.stopAll();

        logger.info("关闭线程池...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public String getToken() {
        return baseClient.getToken();
    }

    public ClientConfig getConfig() {
        return config;
    }

    @Override
    public void close() {
        ClientStarter.shutdownNotify();
    }

}
