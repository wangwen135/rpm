package com.wwh.rpm.client.base.handler;

import static com.wwh.rpm.common.Constants.DEFAULT_HEARTBEAT;
import static com.wwh.rpm.common.Constants.DEFAULT_IDLE_TIMEOUT;

import java.util.concurrent.TimeUnit;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author wangwh
 * @date 2020-12-30
 */
public class BaseHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private BaseClient baseClient;

    public BaseHandlerInitializer(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        ClientConfig config = baseClient.getConfig();

        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        // 加密
        pipeline.addLast(new SimpleEncryptionEncoder(config.getServerConf().getSid()));
        pipeline.addLast(new SimpleEncryptionDecoder(config.getServerConf().getSid()));

        // 压缩
        pipeline.addLast(new JdkZlibEncoder());
        pipeline.addLast(new JdkZlibDecoder());

        // 先添加编码器
        pipeline.addLast(new PacketDecoder());
        pipeline.addLast(new PacketEncoder());

        // 客户端注册
        pipeline.addLast(new RegistHandler(baseClient));

        // 心跳处理
        pipeline.addLast(new IdleStateHandler(DEFAULT_IDLE_TIMEOUT, DEFAULT_HEARTBEAT, 0, TimeUnit.SECONDS));
        pipeline.addLast(new ClientHeartbeatHandler());

        // 服务端指令处理
        pipeline.addLast(new CommandHandler(baseClient));

    }

}
