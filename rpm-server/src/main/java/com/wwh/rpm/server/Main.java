package com.wwh.rpm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.config.Configuration;
import com.wwh.rpm.config.server.ServerConfig;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static Object lock = new Object();

	public static void main(String[] args) {
		logger.warn("启动 RPM 服务端...");
		try {
			Launch(args);
		} catch (Exception e) {
			logger.error("程序异常！", e);
		}
		logger.warn("RPM 停止工作！");
	}

	public static void Launch(String[] args) {
		// 读取配置文件
		ServerConfig config;
		try {
			config = Configuration.getServerConfig();
		} catch (ConfigException e) {
			logger.error("配置文件错误：\n{}", e.getMessage());
			return;
		}

		ServerManager sm = new ServerManager(config);

		try {
			// 启动服务
			sm.startServer();

			// 注册关闭钩子
			addShutdownHook(sm);

			// 测试一下
			new Thread(() -> {
				try {
					Thread.sleep(1000000);
					System.exit(9);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();

			// 等待关闭
			synchronized (lock) {
				try {
					logger.debug("等待关闭程序的信号...");
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e1) {
			logger.error("启动服务异常", e1);
		} finally {
			// 关闭
			sm.shutdownServer();
		}

	}

	private static void addShutdownHook(ServerManager sm) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.warn("shutdown hook exec");
			synchronized (lock) {
				lock.notifyAll();
			}
		}));

	}
}
