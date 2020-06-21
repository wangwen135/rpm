package com.wwh.rpm.config.client;

import java.util.List;

public class ClientConfig {

	private String cid;

	private ServerConf serverConf;

	private List<ForwardOverServer> forwardOverServer;

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public ServerConf getServerConf() {
		return serverConf;
	}

	public void setServerConf(ServerConf serverConf) {
		this.serverConf = serverConf;
	}

	public List<ForwardOverServer> getForwardOverServer() {
		return forwardOverServer;
	}

	public void setForwardOverServer(List<ForwardOverServer> forwardOverServer) {
		this.forwardOverServer = forwardOverServer;
	}

	@Override
	public String toString() {
		return "ClientConfig [cid=" + cid + ", serverConf=" + serverConf + ", forwardOverServer=" + forwardOverServer
				+ "]";
	}

}
