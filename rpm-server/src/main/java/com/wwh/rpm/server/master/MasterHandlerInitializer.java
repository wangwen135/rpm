package com.wwh.rpm.server.master;

import static com.wwh.rpm.common.Constants.DEFAULT_IDLE_TIMEOUT;

import java.util.concurrent.TimeUnit;

import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.handler.AuthenticationHandler;
import com.wwh.rpm.server.master.handler.HeartbeatHandler;
import com.wwh.rpm.server.master.handler.TestHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
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

        // 先添加编码器
        p.addLast(new PacketDecoder());
        p.addLast(new PacketEncoder());

        // 认证
        p.addLast(new AuthenticationHandler(masterServer));

        // 心跳处理
        p.addLast(new IdleStateHandler(DEFAULT_IDLE_TIMEOUT, 0, 0, TimeUnit.SECONDS));
        p.addLast(new HeartbeatHandler());

        // 指令处理

        p.addLast(new TestHandler());
    }

}
