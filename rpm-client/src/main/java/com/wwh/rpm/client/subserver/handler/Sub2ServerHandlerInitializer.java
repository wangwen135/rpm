package com.wwh.rpm.client.subserver.handler;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.subserver.Subserver;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.common.handler.TransmissionHandler;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;

/**
 * 子服务到服务端的handler初始化
 * 
 * @author wangwh
 * @date 2021-1-4
 */
public class Sub2ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private Subserver subserver;
    private Channel inboundChannel;

    public Sub2ServerHandlerInitializer(Subserver subserver, Channel inboundChannel) {
        this.subserver = subserver;
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        ClientConfig config = subserver.getConfig();

        // 日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        // 加密
        pipeline.addLast(new SimpleEncryptionEncoder(config.getServerConf().getSid()));
        pipeline.addLast(new SimpleEncryptionDecoder(config.getServerConf().getSid()));

        // 压缩
        pipeline.addLast(new JdkZlibEncoder());
        pipeline.addLast(new JdkZlibDecoder());

        // 编码器
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("decoder", new PacketDecoder());

        // 客户端注册
        pipeline.addLast("regist", new SubserverRegistHandler(subserver, inboundChannel));

        // 转发
        pipeline.addLast(new TransmissionHandler(inboundChannel));
    }

}
