package com.wwh.rpm.protocol.packet.general;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class SuccessPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private String msg;

    public SuccessPacket() {

    }

    public SuccessPacket(Integer nonce) {
        setNonce(nonce);
    }

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_SUCCESS;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
