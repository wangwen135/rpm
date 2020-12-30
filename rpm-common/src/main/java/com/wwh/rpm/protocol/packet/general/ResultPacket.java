package com.wwh.rpm.protocol.packet.general;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 通用结果
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class ResultPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private Boolean success;

    private String msg;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_RESULT;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
