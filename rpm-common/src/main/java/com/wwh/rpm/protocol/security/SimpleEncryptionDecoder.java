package com.wwh.rpm.protocol.security;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 简单加密解码器
 * 
 * @author wangwh
 * @date 2021-2-26
 */
public class SimpleEncryptionDecoder extends ChannelInboundHandlerAdapter {

    /**
     * 秘钥
     */
    private String key;
    private byte[] enKey;
    private int enKeyLength;
    private int enKeyIndex = 0;

    public SimpleEncryptionDecoder(String key) {
        this.key = key;
        enKey = SimpleEncryptionUtil.getEncryptionKeyBySid(key);
        enKeyLength = enKey.length;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        m.markReaderIndex();
        // 可以读取的字节
        int readableBytes = m.readableBytes();
        // 读取的指针
        int readerIndex = m.readerIndex();

        for (int i = 0; i < readableBytes; i++) {
            byte b = m.readByte();
            m.setByte(readerIndex++, b ^ enKey[enKeyIndex++]);
            if (enKeyIndex >= enKeyLength) {
                enKeyIndex = 0;
            }
        }
        m.resetReaderIndex();
        super.channelRead(ctx, m);
    }

    public String getKey() {
        return key;
    }

}
