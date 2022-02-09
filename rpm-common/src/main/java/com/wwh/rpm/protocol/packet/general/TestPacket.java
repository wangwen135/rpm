package com.wwh.rpm.protocol.packet.general;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 测试包
 */
public class TestPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    /**
     * 无效载荷
     */
    private byte[] invalidLoad;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_TEST;
    }

    public byte[] getInvalidLoad() {
        return invalidLoad;
    }

    public void setInvalidLoad(byte[] invalidLoad) {
        this.invalidLoad = invalidLoad;
    }
}
