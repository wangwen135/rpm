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

}
