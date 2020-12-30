package com.wwh.rpm.server.master.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.server.config.pojo.ServerConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * 认证
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    private ServerConfig config;

    public AuthenticationHandler(ServerConfig config) {
        this.config = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 这里直接判断对象

        ChannelPipeline channelPipeline = ctx.pipeline();

        channelPipeline.remove(this);

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("发生异常，关闭连接", cause);
        ctx.close();
    }
}
