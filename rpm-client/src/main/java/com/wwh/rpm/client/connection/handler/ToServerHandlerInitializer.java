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

/**
 * 到服务端的channel的handler初始化
 * 
 * @author wangwh
 * @date 2021-1-29
 */
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

        if (packet != null) {
            // 客户端注册
            pipeline.addLast("regist", new RegistClientHandler(token, fetchChannelWarp));
            // 发送指令等待结果
            pipeline.addLast("sendCommandWaitResult", new SendCommandWaitResultHandler(packet, fetchChannelWarp));
        } else {
            // 客户端注册 如果不发指令，则需要通知出来
            pipeline.addLast("regist", new RegistClientHandler(token, fetchChannelWarp, true));
        }
    }

}
