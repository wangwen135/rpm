package com.wwh.rpm.server.subserver.handler;


import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.subserver.Subserver;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


public class SubserverHandlerInitializer extends ChannelInitializer<SocketChannel> {

    public Subserver subserver;

    public SubserverHandlerInitializer(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        Server
        Config config = subserver.getConfig();

        // 日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        // 转发处理
        pipeline.addLast(new SubserverProxyHandler(subserver));
    }

}
