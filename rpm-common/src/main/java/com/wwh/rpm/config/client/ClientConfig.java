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

    public String toPrettyString() {
	StringBuffer sbuf = new StringBuffer();

	sbuf.append("\n##############################################\n");
	sbuf.append("客户端id   cid = ").append(cid).append("\n");

	sbuf.append("# 服务器配置：\n");
	sbuf.append(serverConf.toPrettyString());

	if (forwardOverServer != null && !forwardOverServer.isEmpty()) {
	    sbuf.append("# 客户端经由服务端转发的列表：\n");
	    for (int i = 0; i < forwardOverServer.size(); i++) {
		sbuf.append("### 配置【").append(i + 1).append("】\n");
		sbuf.append(forwardOverServer.get(i).toPrettyString());
	    }

	} else {
	    sbuf.append("# 客户端经由服务端转发的列表为空！");
	}

	sbuf.append("\n##############################################\n");

	return sbuf.toString();
    }

}
