package com.wwh.rpm.client.subserver;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.subserver.handler.SubserverRegistHandler;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.common.handler.HexDumpProxyBackendHandler;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 子服务到服务端的handler初始化
 * 
 * @author wangwh
 * @date 2021-1-4
 */
public class Sub2ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    public Subserver subserver;

    public Sub2ServerHandlerInitializer(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        ClientConfig config = subserver.getConfig();

        // 日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        // 编码器
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("decoder", new PacketDecoder());

        // 客户端注册
        pipeline.addLast("regist", new SubserverRegistHandler(subserver));

        // TODO 连接服务器之前都是需要进行认证的

        // 转发
        pipeline.addLast(new HexDumpProxyBackendHandler(ch));
    }

}
