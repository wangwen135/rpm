package com.wwh.rpm.protocol.packet.transport;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

public class TransportPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private byte[] data;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_TRANSPORT;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
