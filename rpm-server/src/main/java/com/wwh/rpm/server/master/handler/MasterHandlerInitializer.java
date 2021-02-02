package com.wwh.rpm.server.master.handler;

import com.wwh.rpm.common.Constants;
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

        // 认证超时处理
        p.addLast(new AuthTimeoutHandler());

        // 先添加编码器
        p.addLast(Constants.DECODE_HANDLER_NAME, new PacketDecoder());
        p.addLast(Constants.ENCODE_HANDLER_NAME, new PacketEncoder());

        // 认证
        p.addLast("auth", new AuthenticationHandler(masterServer));

        // 指令处理
        p.addLast(Constants.COMMAND_HANDLER_NAME, new CommandHandler(masterServer));
    }

}
