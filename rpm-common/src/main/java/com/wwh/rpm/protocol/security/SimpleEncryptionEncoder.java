package com.wwh.rpm.protocol.security;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class SimpleEncryptionEncoder extends ChannelOutboundHandlerAdapter {

    /**
     * 秘钥
     */
    private String key;
    private byte[] enKey;
    private int enKeyLength;
    private int enKeyIndex = 0;

    public SimpleEncryptionEncoder(String key) {
        this.key = key;
        enKey = SimpleEncryptionUtil.getEncryptionKeyBySid(key);
        enKeyLength = enKey.length;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
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
        super.write(ctx, msg, promise);
    }

    public String getKey() {
        return key;
    }
}
