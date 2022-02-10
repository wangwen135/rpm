package com.wwh.rpm.protocol.packet.command;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 转发指令包
 * 
 * @author wangwh
 */
public class ForwardCommandPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String host;

    private int port;

    public ForwardCommandPacket() {

    }

    public ForwardCommandPacket(Long id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_FORWARD_COMMAND;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "ForwardCommandPacket [id=" + id + ", host=" + host + ", port=" + port + "]";
    }

}
