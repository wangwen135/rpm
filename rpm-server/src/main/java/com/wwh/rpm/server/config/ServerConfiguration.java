package com.wwh.rpm.server.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.config.YamlConfigReader;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.LogUtil;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;

/**
 * @author wwh
 */
public class ServerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);

    /**
     * 服务端配置文件名称
     */
    public static final String SERVER_CONFIG_FILE = "server.yaml";

    /**
     * 缺省的客户端控制端口
     */
    public static final int DEFAULT_SERVER_CTRL_PORT = 56791;

    /**
     * 获取、打印、检查 配置文件
     * 
     * @return
     * @throws ConfigException
     */
    public static ServerConfig getEffectiveConfig() throws ConfigException {
        ServerConfig config = getServerConfig();
        printServerConfig(config);
        check(config);
        return config;
    }

    public static ServerConfig getServerConfig() throws ConfigException {
        return getServerConfig(SERVER_CONFIG_FILE);
    }

    public static ServerConfig getServerConfig(String file) throws ConfigException {
        try {
            ServerConfig serverConfig = YamlConfigReader.readConfiguration(file, ServerConfig.class);
            setDefaultValue(serverConfig);
            return serverConfig;
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            logger.error("读取配置异常", e);
            throw new ConfigException("读取配置异常：" + e.getMessage(), e);
        }
    }

    /**
     * 设置默认值
     * 
     * @param config
     */
    private static void setDefaultValue(ServerConfig config) {
        Integer ctrlPort = config.getCtrlPort();
        if (ctrlPort == null || ctrlPort < 1) {
            config.setCtrlPort(DEFAULT_SERVER_CTRL_PORT);
        }
    }

    public static void printServerConfig(ServerConfig config) {
        LogUtil.msgLog.info(config.toPrettyString());
    }

    public static void check(ServerConfig serverConfig) throws ConfigException {

        if (serverConfig == null) {
            throw new ConfigException("配置文件不能空");
        }

        if (StringUtils.isBlank(serverConfig.getSid())) {
            throw new ConfigException("服务端ID【sid】不能空");
        }

        if (StringUtils.isBlank(serverConfig.getHost())) {
            throw new ConfigException("监听地址【host】不能空");
        }

        if (serverConfig.getPort() < 1 || serverConfig.getPort() > 65535) {
            throw new ConfigException("监听端口【port】配置错误");
        }

        Integer compressionLevel = serverConfig.getCompressionLevel();
        if (compressionLevel < Constants.COMPRESSION_LEVEL_MIN || compressionLevel > Constants.COMPRESSION_LEVEL_MAX) {
            throw new ConfigException("压缩级别【compressionLevel】配置错误，只支持 0-9");
        }

        // 转发配置
        checkForwardOverClient(serverConfig.getForwardOverClient());
    }

    private static void checkForwardOverClient(List<ForwardOverClient> forwardList) throws ConfigException {
        if (forwardList == null || forwardList.isEmpty()) {
            return;
        }
        for (int i = 1; i <= forwardList.size(); i++) {
            ForwardOverClient f = forwardList.get(i - 1);

            if (StringUtils.isBlank(f.getListenHost())) {
                throw new ConfigException("转发配置【forwardOverClient[" + i + "]:listenHost】不能空");
            }

            if (f.getListenPort() < 1 || f.getListenPort() > 65535) {
                throw new ConfigException("转发配置【forwardOverClient[" + i + "]:listenPort】错误");
            }

            if (StringUtils.isBlank(f.getClientId())) {
                throw new ConfigException("转发配置【forwardOverClient[" + i + "]:clientId】不能空");
            }

            if (StringUtils.isBlank(f.getForwardHost())) {
                throw new ConfigException("转发配置【forwardOverClient[" + i + "]:forwardHost】不能空");
            }

            if (f.getForwardPort() < 1 || f.getForwardPort() > 65535) {
                throw new ConfigException("转发配置【forwardOverClient[" + i + "]:forwardPort】错误");
            }
        }
    }

}
