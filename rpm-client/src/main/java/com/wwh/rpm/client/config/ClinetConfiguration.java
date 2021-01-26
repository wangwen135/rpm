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

/**
 * @author wwh
 */
public class ClinetConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ClinetConfiguration.class);

    /**
     * 客户端配置文件名称
     */
    public static final String CLIENT_CONFIG_FILE = "client.yaml";

    /**
     * 缺省的客户端控制端口
     */
    public static final int DEFAULT_CLIENT_CTRL_PORT = 56781;

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
            setDefaultValue(clientConfig);
            return clientConfig;
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
    private static void setDefaultValue(ClientConfig config) {
        Integer ctrlPort = config.getCtrlPort();
        if (ctrlPort == null || ctrlPort < 1) {
            config.setCtrlPort(DEFAULT_CLIENT_CTRL_PORT);
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
            throw new ConfigException("客户端ID【cid】不能空");
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
            throw new ConfigException("服务端ID【serverConf:sid】不能空");
        }

        if (StringUtils.isBlank(serverConf.getHost())) {
            throw new ConfigException("服务端监听地址【serverConf:host】不能空");
        }

        if (serverConf.getPort() < 1 || serverConf.getPort() > 65535) {
            throw new ConfigException("服务端监听端口【serverConf:port】错误");
        }
    }

    private static void checkForwardOverServer(List<ForwardOverServer> forwardList) throws ConfigException {
        if (forwardList == null || forwardList.isEmpty()) {
            return;
        }
        for (int i = 1; i <= forwardList.size(); i++) {
            ForwardOverServer f = forwardList.get(i - 1);

            if (StringUtils.isBlank(f.getListenHost())) {
                throw new ConfigException("转发配置【forwardOverServer[" + i + "]:listenHost】不能空");
            }

            if (f.getListenPort() < 1 || f.getListenPort() > 65535) {
                throw new ConfigException("转发配置【forwardOverServer[" + i + "]:listenPort】错误");
            }

            if (StringUtils.isBlank(f.getForwardHost())) {
                throw new ConfigException("转发配置【forwardOverServer[" + i + "]:forwardHost】不能空");
            }

            if (f.getForwardPort() < 1 || f.getForwardPort() > 65535) {
                throw new ConfigException("转发配置【forwardOverServer[" + i + "]:forwardPort】错误");
            }
        }
    }

}
