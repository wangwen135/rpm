package com.wwh.rpm.server.master.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.handler.TransmissionHandler;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.protocol.packet.general.FailPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;

public class CommandHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof ForwardCommandPacket) {
            ForwardCommandPacket fcp = (ForwardCommandPacket) msg;
            forwardCommandHandler(ctx, fcp);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private void forwardCommandHandler(ChannelHandlerContext ctx, ForwardCommandPacket forwardCommand) {
        logger.debug("处理转发指令，to {}:{}", forwardCommand.getHost(), forwardCommand.getPort());
        Channel inboundChannel = ctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop()).channel(inboundChannel.getClass());
        b.option(ChannelOption.AUTO_READ, false);

        // 新连接的数据直接转发
        b.handler(new TransmissionHandler(inboundChannel));

        ChannelFuture f = b.connect(forwardCommand.getHost(), forwardCommand.getPort());

        Channel outboundChannel = f.channel();

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("到目标服务器的连接建立成功 {}", outboundChannel.toString());
                    inboundChannel.writeAndFlush(new SuccessPacket());

                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.remove("decoder");
                    pipeline.remove("encoder");
                    pipeline.remove("command");

                    // 添加转发handler
                    pipeline.addLast(new TransmissionHandler(outboundChannel));

                    inboundChannel.read();
                    outboundChannel.read();
                } else {
                    inboundChannel.writeAndFlush(new FailPacket());
                    inboundChannel.close();
                }

            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("指令处理异常", cause);
        ctx.close();
    }

}
