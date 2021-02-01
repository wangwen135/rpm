package com.wwh.rpm.protocol.packet.command;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 转发指令包执行结果
 * 
 * @author wangwh
 * @date 2021-2-1
 */
public class ForwardResultPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private Long id;

    private boolean result = false;

    public ForwardResultPacket() {
    }

    public ForwardResultPacket(long id) {
        this(id, false);
    }

    public ForwardResultPacket(long id, boolean result) {
        this.id = id;
        this.result = result;
    }

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_FORWARD_COMMAND_RESULT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ForwardResultPacket [id=" + id + ", result=" + result + "]";
    }

}
