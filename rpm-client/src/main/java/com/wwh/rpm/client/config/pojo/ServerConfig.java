package com.wwh.rpm.client.config.pojo;

import org.apache.commons.lang3.StringUtils;

import com.wwh.rpm.common.config.pojo.AbstractConfig;
import com.wwh.rpm.common.exception.ConfigException;

/**
 * 服务端配置
 * 
 * @author wangwh
 */
public class ServerConfig extends AbstractConfig {

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

    @Override
    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("\n#服务端配置：\n");
        sbuf.append("  服务器地址 host = ").append(host).append("\n");
        sbuf.append("  服务器端口 port = ").append(port).append("\n");
        sbuf.append("  服务器sid  sid = ").append(sid).append("\n");
        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {

        if (StringUtils.isBlank(getSid())) {
            throw new ConfigException("服务端ID【serverConf:sid】不能空");
        }

        if (StringUtils.isBlank(getHost())) {
            throw new ConfigException("服务端监听地址【serverConf:host】不能空");
        }

        if (getPort() < 1 || getPort() > 65535) {
            throw new ConfigException("服务端监听端口【serverConf:port】错误");
        }

    }
}
