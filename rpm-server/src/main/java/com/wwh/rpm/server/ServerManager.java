package com.wwh.rpm.server;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.ctrl.Closeer;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.MasterServer;
import com.wwh.rpm.server.subserver.SubserverManager;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 服务管理器
 *
 * @author wwh
 */
public class ServerManager implements Closeer {

    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private ServerConfig config;
    private MasterServer masterServer;
    private SubserverManager subserverManager;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private AtomicBoolean isStartup = new AtomicBoolean(false);

    public ServerManager(ServerConfig config) {
        this.config = config;
        masterServer = new MasterServer(this);
        subserverManager = new SubserverManager(this);

        // 创建线程池
        int bossPoolSize = config.getForwardOverClient() == null ? 1 : config.getForwardOverClient().size();
        bossGroup = new NioEventLoopGroup(bossPoolSize);
        workerGroup = new NioEventLoopGroup();
    }

    public void startServer() throws Exception {

        if (!isStartup.compareAndSet(false, true)) {
            logger.error("服务端已是启动状态 ！");
            return;
        }

        logger.info("启动主服务...");
        masterServer.start(bossGroup, workerGroup);

        startSubServer();
    }

    private void startSubServer() throws Exception {

        logger.info("启动子服务...");
        // 多个子服务复用一个bossGroup和workGroup线程池
        subserverManager.startAll(bossGroup, workerGroup);

    }

    public void shutdownServer() {
        logger.info("关闭主服务...");
        masterServer.shutdown();

        logger.info("关闭子服务...");
        subserverManager.stopAll();

        logger.info("关闭线程池...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public ServerConfig getConfig() {
        return config;
    }

    @Override
    public void close() {
        ServerStarter.shutdownNotify();
    }
}
