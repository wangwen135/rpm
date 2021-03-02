package com.wwh.rpm.server.config.pojo;

public class ForwardOverClient {

    private String listenHost;
    private int listenPort;

    private String clientId;

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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
        return "ForwardOverClient [listenHost=" + listenHost + ", listenPort=" + listenPort + ", clientId=" + clientId
                + ", forwardHost=" + forwardHost + ", forwardPort=" + forwardPort + "]";
    }

    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(" *  本地监听  listenHost:listenPort = ").append(listenHost).append(":").append(listenPort)
                .append("\n");
        sbuf.append(" -> 转发的客户端ID  clientId = ").append(clientId).append("\n");
        sbuf.append(" -> 经由客户端转发至  forwardHost:forwardPort = ").append(forwardHost).append(":").append(forwardPort).append("\n");

        return sbuf.toString();
    }

}
