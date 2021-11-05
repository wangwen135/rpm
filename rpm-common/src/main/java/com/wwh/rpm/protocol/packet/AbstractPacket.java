package com.wwh.rpm.protocol.packet;

import java.io.Serializable;

/**
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public abstract class AbstractPacket implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long time;

    /**
     * 特征混淆用
     */
    private String nonce;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public abstract byte getType();

}
