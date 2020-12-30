package com.wwh.rpm.server.master.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.protocol.packet.heartbeat.HearbeatPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳处理
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果是心跳包就不再往后处理了
        if (msg instanceof HearbeatPacket) {
            logger.info("收到心跳包");
            // 回复一个心跳包
            ctx.writeAndFlush(new HearbeatPacket());
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.warn("到达指定时间间隔没有收到心跳，关闭连接：{}", ctx.channel().remoteAddress());
                ctx.fireUserEventTriggered(evt);
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
