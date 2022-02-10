package com.wwh.rpm.server.config.pojo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.config.pojo.AbstractConfig;
import com.wwh.rpm.common.config.pojo.Arguments;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;
import com.wwh.rpm.common.exception.ConfigException;

/**
 * 服务端配置
 * 
 * @author WWH
 * @date 2022-2-10
 */
public class ServerConfig extends AbstractConfig {

    /**
     * 服务ID
     */
    private String sid;

    /**
     * 服务器监听地址
     */
    private String host;

    /**
     * 服务器监听端口
     */
    private int port;

    /**
     * 服务端控制端口
     */
    private Integer ctrlPort;

    /**
     * 通信配置
     */
    private CommunicationConfig communication;

    /**
     * 转发配置
     */
    private List<ForwardOverClient> forwardOverClient;

    /**
     * 其他参数
     */
    private Arguments arguments;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<ForwardOverClient> getForwardOverClient() {
        return forwardOverClient;
    }

    public void setForwardOverClient(List<ForwardOverClient> forwardOverClient) {
        this.forwardOverClient = forwardOverClient;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    public Integer getCtrlPort() {
        return ctrlPort;
    }

    public void setCtrlPort(Integer ctrlPort) {
        this.ctrlPort = ctrlPort;
    }

    public CommunicationConfig getCommunication() {
        return communication;
    }

    public void setCommunication(CommunicationConfig communication) {
        this.communication = communication;
    }

    @Override
    public String toString() {
        return "ServerConfig [sid=" + sid + ", host=" + host + ", port=" + port + ", ctrlPort=" + ctrlPort
                + ", communication=" + communication + ", forwardOverClient=" + forwardOverClient + ", arguments="
                + arguments + "]";
    }

    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();

        sbuf.append("\n##############################################\n");
        sbuf.append("服务器id   sid = ").append(sid).append("\n");
        sbuf.append("监听地址   host = ").append(host).append("\n");
        sbuf.append("监听端口   port = ").append(port).append("\n");
        sbuf.append("控制端口   ctrlPort = ").append(ctrlPort).append("\n");
        // 通信配置
        sbuf.append(communication.toPrettyString());

        if (forwardOverClient != null && !forwardOverClient.isEmpty()) {
            sbuf.append("\n# 服务端经由客户端转发的列表：\n");
            for (int i = 0; i < forwardOverClient.size(); i++) {
                sbuf.append("### 配置【").append(i + 1).append("】\n");
                sbuf.append(forwardOverClient.get(i).toPrettyString());
            }

        } else {
            sbuf.append("\n# 服务端经由客户端转发的列表为空！\n");
        }

        // 其他配置
        if (arguments != null) {
            sbuf.append(arguments.toPrettyString());
        }

        sbuf.append("\n##############################################\n");

        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {
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
        checkForwardOverClient(forwardOverClient);
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
