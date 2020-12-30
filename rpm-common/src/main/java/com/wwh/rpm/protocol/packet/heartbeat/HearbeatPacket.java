package com.wwh.rpm.protocol.packet.heartbeat;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

public class HearbeatPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_HEARTBEAT;
    }

}
