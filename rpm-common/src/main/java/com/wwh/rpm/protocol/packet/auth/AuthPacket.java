package com.wwh.rpm.protocol.packet.auth;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 认证包
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class AuthPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    /**
     * 使用sid加密的随机数
     */
    private byte[] chiphers;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_AUTH;
    }

    public byte[] getChiphers() {
        return chiphers;
    }

    public void setChiphers(byte[] chiphers) {
        this.chiphers = chiphers;
    }

}
