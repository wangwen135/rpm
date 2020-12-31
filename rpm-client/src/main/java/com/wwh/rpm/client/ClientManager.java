package com.wwh.rpm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.config.pojo.ClientConfig;

public class ClientManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private ClientConfig config;
    private BaseClient baseClient;
    // 还有一堆子服务

    public ClientManager(ClientConfig config) {
        this.config = config;
        baseClient = new BaseClient(this);
    }

    public void startClient() throws Exception {
        logger.info("启动客户端...");
        baseClient.start();

        logger.info("开始启动子服务...");
    }

    public void shutdownClient() {
        baseClient.shutdown();
    }

    public String getToken() {
        return baseClient.getToken();
    }

    public ClientConfig getConfig() {
        return config;
    }

}
