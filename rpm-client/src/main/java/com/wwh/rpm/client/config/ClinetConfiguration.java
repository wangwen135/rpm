package com.wwh.rpm.client.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ForwardOverServer;
import com.wwh.rpm.client.config.pojo.ServerConf;
import com.wwh.rpm.common.config.YamlConfigReader;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;

public class ClinetConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ClinetConfiguration.class);

    public static final String CLIENT_CONFIG_FILE = "client.yaml";

    public static ClientConfig getClientConfig() throws ConfigException {
        return getClientConfig(CLIENT_CONFIG_FILE);
    }

    public static ClientConfig getClientConfig(String file) throws ConfigException {
        try {
            ClientConfig clientConfig = YamlConfigReader.readConfiguration(file, ClientConfig.class);

            // 默认值处理

            printClientConfig(clientConfig);

            check(clientConfig);

            return clientConfig;
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            logger.error("读取配置异常", e);
            throw new ConfigException("读取配置异常：" + e.getMessage(), e);
        }
    }

    public static void printClientConfig(ClientConfig config) {
        RpmMsgPrinter.printMsg(config.toPrettyString());
    }

    public static void check(ClientConfig clientConfig) throws ConfigException {
        if (clientConfig == null) {
            throw new ConfigException("配置文件不能为空");
        }
        if (StringUtils.isBlank(clientConfig.getCid())) {
            throw new ConfigException("cid不能空");
        }
        // 服务端配置
        checkServerConf(clientConfig.getServerConf());

        // 转发配置
        checkForwardOverServer(clientConfig.getForwardOverServer());
    }

    private static void checkServerConf(ServerConf serverConf) throws ConfigException {
        if (serverConf == null) {
            throw new ConfigException("服务端配置【serverConf】不能为空");
        }

        if (StringUtils.isBlank(serverConf.getSid())) {
            throw new ConfigException("服务端配置【serverConf:sid】不能空");
        }

        if (StringUtils.isBlank(serverConf.getHost())) {
            throw new ConfigException("服务端配置【serverConf:host】不能空");
        }

        if (serverConf.getPort() < 1 || serverConf.getPort() > 65535) {
            throw new ConfigException("服务端配置【serverConf:port】错误");
        }
    }

    private static void checkForwardOverServer(List<ForwardOverServer> forwardOverServer) throws ConfigException {

    }

}