package com.wwh.rpm.server.master.handler;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.MasterServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;

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

        String sid = masterServer.getConfig().getSid();

        // 加密
        p.addLast(new SimpleEncryptionEncoder(sid));
        p.addLast(new SimpleEncryptionDecoder(sid));

        // 压缩
        p.addLast(new JdkZlibEncoder());
        p.addLast(new JdkZlibDecoder());

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
