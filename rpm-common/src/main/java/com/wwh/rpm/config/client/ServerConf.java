package com.wwh.rpm.config.client;

public class ServerConf {

    private String sid;

    private String host;
    private int port;

    private int encryption = 1;

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

    @Override
    public String toString() {
	return "ServerConf [sid=" + sid + ", host=" + host + ", port=" + port + ", encryption=" + encryption + "]";
    }

    public String toPrettyString() {
	StringBuffer sbuf = new StringBuffer();
	sbuf.append(" 服务器地址   host = ").append(host).append("\n");
	sbuf.append(" 服务器端口   port = ").append(port).append("\n");
	sbuf.append(" 服务器id   sid = ").append(sid).append("\n");
	sbuf.append(" 加密方式   encryption = ").append(encryption).append("\n");
	return sbuf.toString();
    }
}
