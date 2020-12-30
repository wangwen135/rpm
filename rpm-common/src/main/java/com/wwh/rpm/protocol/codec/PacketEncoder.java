package com.wwh.rpm.protocol.codec;

import com.wwh.rpm.common.serialize.Serialization;
import com.wwh.rpm.common.serialize.SerializerFactory;
import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf out) throws Exception {

        Serialization serialization = SerializerFactory.getSerializer();

        byte[] data = serialization.serialize(msg);
        int dataLength = data.length;
        byte type = msg.getType();

        out.writeByte(ProtocolConstants.MAGIC_NUMBER);
        out.writeByte(type);
        out.writeInt(dataLength);
        out.writeBytes(data);
    }

}
