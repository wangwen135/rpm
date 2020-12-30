package com.wwh.rpm.client.base;

import static com.wwh.rpm.common.Constants.DEFAULT_HEARTBEAT;
import static com.wwh.rpm.common.Constants.DEFAULT_IDLE_TIMEOUT;

import java.util.concurrent.TimeUnit;

import com.wwh.rpm.client.base.handler.ClientHeartbeatHandler;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author wangwh
 * @date 2020-12-30
 */
public class BaseHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private ClientConfig clientConfig;

    public BaseHandlerInitializer(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        HandlerInitHelper.initNettyLoggingHandler(pipeline, clientConfig.getArguments());

        // 先添加编码器
        pipeline.addLast(new PacketDecoder());
        pipeline.addLast(new PacketEncoder());

        // 心跳处理
        pipeline.addLast(new IdleStateHandler(DEFAULT_IDLE_TIMEOUT, DEFAULT_HEARTBEAT, 0, TimeUnit.SECONDS));
        pipeline.addLast(new ClientHeartbeatHandler());
    }

}
