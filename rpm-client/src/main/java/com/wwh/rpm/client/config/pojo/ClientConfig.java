package com.wwh.rpm.client.config.pojo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wwh.rpm.common.config.pojo.AbstractConfig;
import com.wwh.rpm.common.config.pojo.Arguments;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;
import com.wwh.rpm.common.exception.ConfigException;

/**
 * 客户端配置
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class ClientConfig extends AbstractConfig {

    /**
     * 缺省的客户端控制端口
     */
    public static final int DEFAULT_CLIENT_CTRL_PORT = 56781;

    /**
     * 客户端ID
     */
    private String cid;

    /**
     * 客户端控制端口
     */
    private Integer ctrlPort;

    /**
     * 服务端配置
     */
    private ServerConfig serverConf;

    /**
     * 通信配置
     */
    private CommunicationConfig communication;

    /**
     * 连接池配置
     */
    private PoolConfig pool;

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

    public ServerConfig getServerConf() {
        return serverConf;
    }

    public void setServerConf(ServerConfig serverConf) {
        this.serverConf = serverConf;
    }

    public CommunicationConfig getCommunication() {
        return communication;
    }

    public void setCommunication(CommunicationConfig communication) {
        this.communication = communication;
    }

    public PoolConfig getPool() {
        return pool;
    }

    public void setPool(PoolConfig pool) {
        this.pool = pool;
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

    public Integer getCtrlPort() {
        return ctrlPort == null ? DEFAULT_CLIENT_CTRL_PORT : ctrlPort;
    }

    public void setCtrlPort(Integer ctrlPort) {
        this.ctrlPort = ctrlPort;
    }

    @Override
    public String toString() {
        return "ClientConfig [cid=" + cid + ", ctrlPort=" + ctrlPort + ", serverConf=" + serverConf + ", communication="
                + communication + ", pool=" + pool + ", forwardOverServer=" + forwardOverServer + ", arguments="
                + arguments + "]";
    }

    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();

        sbuf.append("\n##############################################\n");
        sbuf.append("客户端id      cid = ").append(cid).append("\n");
        sbuf.append("控制端口 ctrlPort = ").append(getCtrlPort()).append("\n");

        // 服务配置
        sbuf.append(serverConf.toPrettyString());

        // 通信配置
        sbuf.append(communication.toPrettyString());

        if (forwardOverServer != null && !forwardOverServer.isEmpty()) {
            sbuf.append("\n#客户端经由服务端转发的列表：\n");
            for (int i = 0; i < forwardOverServer.size(); i++) {
                sbuf.append("### 配置【").append(i + 1).append("】\n");
                sbuf.append(forwardOverServer.get(i).toPrettyString());
            }

        } else {
            sbuf.append("\n# 客户端经由服务端转发的列表为空！");
        }

        // 其他配置
        if (arguments != null) {
            sbuf.append(arguments.toPrettyString());
        }

        sbuf.append("\n##############################################\n");

        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {

        if (StringUtils.isBlank(getCid())) {
            throw new ConfigException("客户端ID【cid】不能空");
        }
        if (getCtrlPort() < 1 || getCtrlPort() > 65535) {
            throw new ConfigException("控制端口【ctrlPort】配置错误");
        }
        if (getServerConf() == null) {
            throw new ConfigException("服务端配置【serverConf】不能为空");
        }
        getServerConf().check();

        if (getCommunication() == null) {
            throw new ConfigException("通信配置【communication】不能为空");
        }
        getCommunication().check();

        if (getPool() == null) {
            throw new ConfigException("连接池配置【pool】不能为空");
        }
        getPool().check();

        checkForwardOverServer(forwardOverServer);
    }

    private static void checkForwardOverServer(List<ForwardOverServer> forwardList) throws ConfigException {
        if (forwardList == null || forwardList.isEmpty()) {
            return;
        }
        for (int i = 0; i < forwardList.size(); i++) {
            ForwardOverServer f = forwardList.get(i);
            f.check();
        }
    }
}
