package com.wwh.rpm.protocol.packet.control;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

public class ShutdownPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_SHUTDOWN;
    }

}
