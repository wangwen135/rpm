package com.wwh.rpm.client.config.pojo;

import java.util.List;

import com.wwh.rpm.common.config.pojo.Arguments;

/**
 * 客户端配置
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class ClientConfig {

    /**
     * 客户端ID
     */
    private String cid;

    /**
     * 服务端配置
     */
    private ServerConf serverConf;

    /**
     * 经由服务端转发的配置
     */
    private List<ForwardOverServer> forwardOverServer;

    /**
     * 其他参数
     */
    private Arguments arguments;

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

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "ClientConfig [cid=" + cid + ", serverConf=" + serverConf + ", forwardOverServer=" + forwardOverServer
                + ", arguments=" + arguments + "]";
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

        // 其他配置
        if (arguments != null) {
            sbuf.append("# 其他配置：\n");
            sbuf.append(arguments.toPrettyString());
        }

        sbuf.append("\n##############################################\n");

        return sbuf.toString();
    }

}
