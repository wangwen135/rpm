package com.wwh.rpm.client.subconnection.handler;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.handler.HandlerInitHelper;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ToTargetHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private ClientManager clientManager;

    public ToTargetHandlerInitializer(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ClientConfig config = clientManager.getConfig();
        // 日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());
    }

}
