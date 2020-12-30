package com.wwh.rpm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.MasterServer;
import com.wwh.rpm.server.subserver.SubserverManager;

/**
 * 服务管理器
 * @author wwh
 *
 */
public class ServerManager {

	private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

	private ServerConfig config;
	private MasterServer masterServer;
	private SubserverManager subserverManager;

	public ServerManager(ServerConfig config) {
		this.config = config;
		masterServer = new MasterServer(config);
		
		subserverManager = new SubserverManager(config);
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
		//多个子服务复用一个bossGroup和workGroup线程池
		
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
