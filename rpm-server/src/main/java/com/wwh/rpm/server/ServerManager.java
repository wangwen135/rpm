package com.wwh.rpm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.config.server.ServerConfig;
import com.wwh.rpm.server.master.MasterServer;

public class ServerManager {

    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private ServerConfig config;
    private MasterServer masterServer;

    public ServerManager(ServerConfig config){
        this.config = config;
        masterServer = new MasterServer(config);
    }

    public void startServer() throws Exception {

        startMasterServer();

        startSubServer();

    }

    private void startMasterServer() throws Exception {
        logger.info("启动主服务...");
        masterServer.start();
    }

    private void startSubServer() throws Exception {

        logger.info("启动子服务...");
    }

    public void shutdownServer() {
        logger.warn("关闭服务器...");

        try {
            masterServer.shutdown();
        } catch (Exception e) {
            logger.error("关闭服务异常", e);
        }

    }
}
