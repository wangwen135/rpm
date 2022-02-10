package com.wwh.rpm.client.subserver.handler;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.subserver.Subserver;
import com.wwh.rpm.common.handler.HandlerInitHelper;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 子服务的handler初始化
 * 
 * @author wangwh
 * @date 2021-1-4
 */
public class SubserverHandlerInitializer extends ChannelInitializer<SocketChannel> {

    public Subserver subserver;

    public SubserverHandlerInitializer(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        ClientConfig config = subserver.getConfig();

        // 日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        // 转发处理
        pipeline.addLast(new SubserverChannelHandler(subserver));
    }

}
