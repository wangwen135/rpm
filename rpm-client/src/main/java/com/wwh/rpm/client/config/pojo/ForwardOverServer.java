package com.wwh.rpm.client.config.pojo;

import org.apache.commons.lang3.StringUtils;

import com.wwh.rpm.common.config.pojo.AbstractConfig;
import com.wwh.rpm.common.exception.ConfigException;

/**
 * 经由服务端转发的配置
 * 
 * @author wangwh
 */
public class ForwardOverServer extends AbstractConfig {

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

    @Override
    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(" *  本地监听  listenHost:listenPort = ").append(listenHost).append(":").append(listenPort)
                .append("\n");
        sbuf.append(" -> 经由服务端转发至  forwardHost:forwardPort = ").append(forwardHost).append(":").append(forwardPort)
                .append("\n");

        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {
        if (StringUtils.isBlank(getListenHost())) {
            throw new ConfigException("转发配置【forwardOverServer:listenHost】不能空");
        }

        if (getListenPort() < 1 || getListenPort() > 65535) {
            throw new ConfigException("转发配置【forwardOverServer:listenPort】端口错误");
        }

        if (StringUtils.isBlank(getForwardHost())) {
            throw new ConfigException("转发配置【forwardOverServer:forwardHost】不能空");
        }

        if (getForwardPort() < 1 || getForwardPort() > 65535) {
            throw new ConfigException("转发配置【forwardOverServer:forwardPort】端口错误");
        }
    }

}
