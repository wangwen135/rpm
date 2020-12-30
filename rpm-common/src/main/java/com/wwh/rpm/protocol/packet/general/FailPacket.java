package com.wwh.rpm.protocol.packet.general;

import com.wwh.rpm.protocol.packet.AbstractPacket;

public class FailPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private String msg;

    @Override
    public byte getType() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
