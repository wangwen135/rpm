package com.wwh.rpm.protocol.packet.transport;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 关闭连接包
 * 
 * @author WWH
 *
 */
public class ClosePacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_CLOSE_COMMAND;
    }

    public ClosePacket() {
    }

    public ClosePacket(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClosePacket [id=" + id + "]";
    }

}
