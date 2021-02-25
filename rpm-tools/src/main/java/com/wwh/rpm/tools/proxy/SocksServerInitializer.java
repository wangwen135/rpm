package com.wwh.rpm.tools.proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {
    static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("debug", "false"));

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (DEBUG) {
            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        }
        pipeline.addLast(new SocksPortUnificationServerHandler(), SocksServerHandler.INSTANCE);
    }
}