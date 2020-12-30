package com.wwh.rpm.server.master.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 认证
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class TestHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(TestHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("读取消息：");
        logger.debug(msg.toString());
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.debug("写消息：");
        logger.debug(msg.toString());

        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("发生异常：", cause);

        super.exceptionCaught(ctx, cause);
    }
}
