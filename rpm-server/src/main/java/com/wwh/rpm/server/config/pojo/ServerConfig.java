package com.wwh.rpm.server.config.pojo;

import java.util.List;

import com.wwh.rpm.common.config.pojo.Arguments;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;

public class ServerConfig extends CommunicationConfig {

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
     * 转发配置
     */
    private List<ForwardOverClient> forwardOverClient;

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

    /**
     * 获取通讯配置
     * 
     * @return
     */
    public CommunicationConfig getCommConfig() {
        CommunicationConfig config = new CommunicationConfig();
        config.setEncryptType(getEncryptType());
        config.setEnableCompression(getEnableCompression());
        config.setCompressionLevel(getCompressionLevel());
        return config;
    }

    @Override
    public String toString() {
        return "ServerConfig [sid=" + sid + ", host=" + host + ", port=" + port + ", ctrlPort=" + ctrlPort
                + ", forwardOverClient=" + forwardOverClient + ", arguments=" + arguments + ", getEncryptType()="
                + getEncryptType() + ", getEnableCompression()=" + getEnableCompression() + ", getCompressionLevel()="
                + getCompressionLevel() + "]";
    }

    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();

        sbuf.append("\n##############################################\n");
        sbuf.append("服务器id   sid = ").append(sid).append("\n");
        sbuf.append("监听地址   host = ").append(host).append("\n");
        sbuf.append("监听端口   port = ").append(port).append("\n");
        sbuf.append("控制端口   ctrlPort = ").append(ctrlPort).append("\n");
        sbuf.append("加密方式   encryptType = ").append(getEncryptType()).append("\n");
        sbuf.append("是否压缩   enableCompression = ").append(getEnableCompression()).append("\n");
        sbuf.append("压缩级别   compressionLevel = ").append(getCompressionLevel()).append("\n");

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
            sbuf.append("\n# 其他配置：\n");
            sbuf.append(arguments.toPrettyString());
        }

        sbuf.append("\n##############################################\n");

        return sbuf.toString();
    }

}
