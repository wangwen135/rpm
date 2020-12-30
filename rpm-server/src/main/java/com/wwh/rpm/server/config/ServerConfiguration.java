package com.wwh.rpm.server.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.config.YamlConfigReader;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.server.config.pojo.ServerConfig;

public class ServerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);

    public static final String SERVER_CONFIG_FILE = "server.yaml";

    public static ServerConfig getServerConfig() throws ConfigException {
        return getServerConfig(SERVER_CONFIG_FILE);
    }

    public static ServerConfig getServerConfig(String file) throws ConfigException {
        try {
            ServerConfig serverConfig = YamlConfigReader.readConfiguration(file, ServerConfig.class);

            // 默认值处理

            printServerConfig(serverConfig);

            check(serverConfig);

            return serverConfig;
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            logger.error("读取配置异常", e);
            throw new ConfigException("读取配置异常：" + e.getMessage(), e);
        }
    }

    public static void printServerConfig(ServerConfig config) {
        RpmMsgPrinter.printMsg(config.toPrettyString());
    }

    public static void check(ServerConfig serverConfig) throws ConfigException {

        if (serverConfig == null) {
            throw new ConfigException("配置文件不能空");
        }

        if (StringUtils.isBlank(serverConfig.getSid())) {
            throw new ConfigException("sid不能空");
        }

        if (StringUtils.isBlank(serverConfig.getHost())) {
            throw new ConfigException("监听地址host不能空");
        }

        if (serverConfig.getPort() < 1 || serverConfig.getPort() > 65535) {
            throw new ConfigException("监听端口port配置错误");
        }

    }

}
