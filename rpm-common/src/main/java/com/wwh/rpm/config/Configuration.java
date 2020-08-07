package com.wwh.rpm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.config.check.ClientConfigChecker;
import com.wwh.rpm.config.check.ServerConfigChecker;
import com.wwh.rpm.config.client.ClientConfig;
import com.wwh.rpm.config.server.ServerConfig;
import com.wwh.rpm.config.yaml.YamlReader;

public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static final String SERVER_CONFIG_FILE = "server.yaml";

    public static final String CLIENT_CONFIG_FILE = "client.yaml";

    public static ClientConfig getClientConfig() {
	return getClientConfig(CLIENT_CONFIG_FILE);
    }

    public static ClientConfig getClientConfig(String file) {
	try {
	    ClientConfig clientConfig = YamlReader.readConfiguration(file, ClientConfig.class);

	    // 默认值处理

	    printClientConfig(clientConfig);

	    ClientConfigChecker.check(clientConfig);

	    return clientConfig;
	} catch (ConfigException e) {
	    throw e;
	} catch (Exception e) {
	    throw new ConfigException("配置文件错误", e);
	}
    }

    public static void printClientConfig(ClientConfig config) {
	logger.warn(config.toPrettyString());
    }

    public static ServerConfig getServerConfig() {
	return getServerConfig(SERVER_CONFIG_FILE);
    }

    public static ServerConfig getServerConfig(String file) {
	try {
	    ServerConfig serverConfig = YamlReader.readConfiguration(file, ServerConfig.class);

	    // 默认值处理

	    printServerConfig(serverConfig);

	    ServerConfigChecker.check(serverConfig);

	    return serverConfig;
	} catch (ConfigException e) {
	    throw e;
	} catch (Exception e) {
	    throw new ConfigException("配置文件错误", e);
	}
    }

    public static void printServerConfig(ServerConfig config) {
	logger.warn(config.toPrettyString());
    }

}
