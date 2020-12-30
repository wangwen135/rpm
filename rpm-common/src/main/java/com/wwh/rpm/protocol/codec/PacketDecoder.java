package com.wwh.rpm.protocol.codec;

import java.util.List;

import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.PacketDeserialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

/**
 * <pre>
 * 数据包解码器
 * 
 * 【魔法数字 1】【类型 1】【长度 2】【对象序列化字节】
 * </pre>
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 等待足够的长度
        if (in.readableBytes() < 6) {
            return;
        }

        /**
         * <pre>
         * 标记此缓冲区中的当前读取器索引。
         * 可以通过调用resetReaderIndex()将当前的readerIndex重新定位为标记的readerIndex。
         * 标记的readerIndex的初始值为0。
         * </pre>
         */
        in.markReaderIndex();

        // 检查魔法数字
        byte magicNumber = in.readByte();
        if (magicNumber != ProtocolConstants.MAGIC_NUMBER) {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }

        // 数据类型
        byte type = in.readByte();

        // 等到所以的数据可用
        int dataLength = in.readInt();

        // 返回可读字节数，该字节数等于 (this.writerIndex - this.readerIndex).
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 将接收到的数据转换成对象
        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);

        out.add(PacketDeserialization.deserialization(type, bytes));

    }

}
