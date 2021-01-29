package com.wwh.rpm.client.connection.handler;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.connection.FetchChannelWarp;
import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.protocol.packet.AbstractPacket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ToServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private ClientManager clientManager;
    private FetchChannelWarp fetchChannelWarp;
    private AbstractPacket packet;

    public ToServerHandlerInitializer(ClientManager clientManager, FetchChannelWarp fetchChannelWarp,
            AbstractPacket packet) {
        this.clientManager = clientManager;
        this.fetchChannelWarp = fetchChannelWarp;
        this.packet = packet;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ClientConfig config = clientManager.getConfig();
        // 日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        // 编码器
        pipeline.addLast(Constants.ENCODE_HANDLER_NAME, new PacketEncoder());
        pipeline.addLast(Constants.DECODE_HANDLER_NAME, new PacketDecoder());

        String token = clientManager.getToken();

        // TODO 如果不发指令，则需要通知出来
        // 客户端注册
        pipeline.addLast("regist", new RegistClientHandler(token, fetchChannelWarp));

        if (packet != null) {
            // 发送指令等待结果
            pipeline.addLast("sendCommandWaitResult", new SendCommandWaitResultHandler(packet, fetchChannelWarp));
        }
    }

}
