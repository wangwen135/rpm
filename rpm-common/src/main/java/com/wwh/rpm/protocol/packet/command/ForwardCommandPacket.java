package com.wwh.rpm.protocol.packet.command;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

public class ForwardCommandPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private String host;

    private int port;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_FORWARD_COMMAND;
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

}
