package com.wwh.rpm.protocol.packet.auth;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 注册包
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class RegistPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private String cid;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_REGIST;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

}
