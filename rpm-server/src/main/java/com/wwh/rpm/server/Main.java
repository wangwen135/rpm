package com.wwh.rpm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.config.Configuration;
import com.wwh.rpm.config.server.ServerConfig;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.warn("启动 RPM ...");

		Launch(args);

		logger.warn("RPM 停止！");

	}

	public static void Launch(String[] args) {
		// 读取配置文件
		ServerConfig config;
		try {
			config = Configuration.getServerConfig();

			logger.warn("读取到的配置文件是：\n{}", config.toPrettyString());

		} catch (ConfigException e) {
			logger.error("配置文件错误：\n{}", e.getMessage());
			return;
		}

		ServerManager sm = new ServerManager(config);

		addShutdownHook(sm);
		// 启动服务

		sm.startServer();
	}

	private static void addShutdownHook(ServerManager sm) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// 关闭
			sm.shutdownServer();
		}));

	}
}
