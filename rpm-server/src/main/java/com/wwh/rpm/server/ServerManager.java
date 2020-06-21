package com.wwh.rpm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.config.server.ServerConfig;

public class ServerManager {

	private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

	private ServerConfig config;

	public ServerManager(ServerConfig config) {
		this.config = config;
	}

	public void startServer() {

		startMainServer();

		startSubServer();
	}

	private void startMainServer() {

		logger.info("启动主服务...");
	}

	private void startSubServer() {

		logger.info("启动子服务...");
	}

	public void shutdownServer() {
		logger.warn("关闭服务器...");

	}
}
