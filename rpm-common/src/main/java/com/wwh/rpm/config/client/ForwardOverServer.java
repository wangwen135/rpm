package com.wwh.rpm.config.client;

public class ForwardOverServer {

    private String listenHost;
    private int listenPort;

    private String forwardHost;
    private int forwardPort;

    public String getListenHost() {
	return listenHost;
    }

    public void setListenHost(String listenHost) {
	this.listenHost = listenHost;
    }

    public int getListenPort() {
	return listenPort;
    }

    public void setListenPort(int listenPort) {
	this.listenPort = listenPort;
    }

    public String getForwardHost() {
	return forwardHost;
    }

    public void setForwardHost(String forwardHost) {
	this.forwardHost = forwardHost;
    }

    public int getForwardPort() {
	return forwardPort;
    }

    public void setForwardPort(int forwardPort) {
	this.forwardPort = forwardPort;
    }

    @Override
    public String toString() {
	return "ForwardOverServer [listenHost=" + listenHost + ", listenPort=" + listenPort + ", forwardHost="
		+ forwardHost + ", forwardPort=" + forwardPort + "]";
    }

    public String toPrettyString() {
	StringBuffer sbuf = new StringBuffer();
	sbuf.append(" * 本地监听地址  listenHost = ").append(listenHost).append("\n");
	sbuf.append(" * 本地监听端口  listenPort = ").append(listenPort).append("\n");
	sbuf.append(" -> 经由服务端转发的目标地址  forwardHost = ").append(forwardHost).append("\n");
	sbuf.append(" -> 经由服务端转发的目标端口  forwardPort = ").append(forwardPort).append("\n");

	return sbuf.toString();
    }

}
