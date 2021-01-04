package com.wwh.rpm.client.subserver;

import com.wwh.rpm.client.base.handler.RegistHandler;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.subserver.handler.SubserverProxyHandler;
import com.wwh.rpm.client.subserver.handler.SubserverRegistHandler;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;

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
        pipeline.addLast(new SubserverProxyHandler(subserver));
    }

}
