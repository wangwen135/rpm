package com.wwh.rpm.protocol.packet.transport;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * 数据传输包
 * 
 * @author WWH
 *
 */
public class TransportPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private Long id;

    private byte[] data;

    public TransportPacket() {
    }

    public TransportPacket(Long id, byte[] data) {
        this.id = id;
        this.data = data;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TransportPacket [id=" + id + ", data length=" + data.length + "]";
    }

}
