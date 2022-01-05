package com.wwh.rpm.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.config.YamlConfigReader;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.LogUtil;

/**
 * 客户端配置工具类
 * 
 * @author wwh
 */
public class ClinetConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ClinetConfiguration.class);

    /**
     * 客户端配置文件名称
     */
    public static final String CLIENT_CONFIG_FILE = "client.yaml";

    /**
     * 获取、打印、检查 配置文件
     * 
     * @return
     * @throws ConfigException
     */
    public static ClientConfig getEffectiveConfig() throws ConfigException {
        ClientConfig config = getClientConfig();
        printClientConfig(config);
        check(config);
        return config;
    }

    /**
     * 获取配置文件
     * 
     * @return
     * @throws ConfigException
     */
    public static ClientConfig getClientConfig() throws ConfigException {
        return getClientConfig(CLIENT_CONFIG_FILE);
    }

    public static ClientConfig getClientConfig(String file) throws ConfigException {
        try {
            ClientConfig clientConfig = YamlConfigReader.readConfiguration(file, ClientConfig.class);
            return clientConfig;
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            logger.error("读取配置异常", e);
            throw new ConfigException("读取配置异常：" + e.getMessage(), e);
        }
    }

    public static void printClientConfig(ClientConfig config) {
        LogUtil.msgLog.info(config.toPrettyString());
    }

    public static void check(ClientConfig clientConfig) throws ConfigException {
        if (clientConfig == null) {
            throw new ConfigException("配置文件不能为空");
        }
        clientConfig.check();
    }

}
