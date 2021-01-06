package com.wwh.rpm.server.master.handler;

import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.MasterServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 主服务handler初始化
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public class MasterHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private MasterServer masterServer;

    public MasterHandlerInitializer(MasterServer masterServer) {
        this.masterServer = masterServer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        ServerConfig config = masterServer.getConfig();
        HandlerInitHelper.initNettyLoggingHandler(p, config.getArguments());

        p.addLast(new AuthTimeoutHandler());

        // 先添加编码器
        p.addLast("decoder", new PacketDecoder());
        p.addLast("encoder", new PacketEncoder());

        // 认证
        p.addLast("auth", new AuthenticationHandler(masterServer));

        // 指令处理
        p.addLast("command", new CommandHandler());
    }

}
