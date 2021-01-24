package com.wwh.rpm.client.base.handler;

import com.wwh.rpm.client.base.BaseClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private BaseClient baseClient;

    public CommandHandler(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);
        //解析服务端的指令，
        //服务端通过客户端转发的情况
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("指令处理异常", cause);
        ctx.close();
    }
}
