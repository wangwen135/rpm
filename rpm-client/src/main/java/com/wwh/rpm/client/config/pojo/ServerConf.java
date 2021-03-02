package com.wwh.rpm.client.config.pojo;

public class ServerConf {

    private String sid;

    private String host;
    private int port;

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

    @Override
    public String toString() {
        return "ServerConf [sid=" + sid + ", host=" + host + ", port=" + port + "]";
    }

    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(" 服务器地址 host = ").append(host).append("\n");
        sbuf.append(" 服务器端口 port = ").append(port).append("\n");
        sbuf.append(" 服务器sid  sid = ").append(sid).append("\n");
        return sbuf.toString();
    }
}
