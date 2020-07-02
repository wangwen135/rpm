package com.wwh.rpm.config.server;

import java.util.List;

public class ServerConfig {

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
     * 加密方式
     */
    private int encryption = 1;

    /**
     * 转发配置
     */
    private List<ForwardOverClient> forwardOverClient;

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

    public int getEncryption() {
	return encryption;
    }

    public void setEncryption(int encryption) {
	this.encryption = encryption;
    }

    public List<ForwardOverClient> getForwardOverClient() {
	return forwardOverClient;
    }

    public void setForwardOverClient(List<ForwardOverClient> forwardOverClient) {
	this.forwardOverClient = forwardOverClient;
    }

    @Override
    public String toString() {
	return "ServerConfig [sid=" + sid + ", host=" + host + ", port=" + port + ", encryption=" + encryption
		+ ", forwardOverClient=" + forwardOverClient + "]";
    }

    public String toPrettyString() {
	StringBuffer sbuf = new StringBuffer();

	sbuf.append("\n##############################################\n");
	sbuf.append("服务器id   sid = ").append(sid).append("\n");
	sbuf.append("监听地址   host = ").append(host).append("\n");
	sbuf.append("监听端口   port = ").append(port).append("\n");
	sbuf.append("加密方式   encryption = ").append(encryption).append("\n");

	if (forwardOverClient != null && !forwardOverClient.isEmpty()) {
	    sbuf.append("# 服务端经由客户端转发的列表：\n");
	    for (int i = 0; i < forwardOverClient.size(); i++) {
		sbuf.append("### 配置【").append(i + 1).append("】\n");
		sbuf.append(forwardOverClient.get(i).toPrettyString());
	    }

	} else {
	    sbuf.append("# 服务端经由客户端转发的列表为空！");
	}

	sbuf.append("\n##############################################\n");

	return sbuf.toString();
    }

}
