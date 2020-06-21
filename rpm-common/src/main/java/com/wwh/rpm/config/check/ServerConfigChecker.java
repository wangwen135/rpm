package com.wwh.rpm.config.check;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.config.server.ServerConfig;

public class ServerConfigChecker {

	public static void check(ServerConfig serverConfig) {

		if (serverConfig == null) {
			throw new ConfigException("配置文件不能空");
		}

		if (StringUtils.isBlank(serverConfig.getSid())) {
			throw new ConfigException("SID不能空");
		}

		if (StringUtils.isBlank(serverConfig.getHost())) {
			throw new ConfigException("监听地址Host不能空");
		}

		if (serverConfig.getPort() < 1 || serverConfig.getPort() > 65535) {
			throw new ConfigException("监听端口Port配置错误");
		}
	}
}
