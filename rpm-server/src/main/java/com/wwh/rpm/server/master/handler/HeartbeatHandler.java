package com.wwh.rpm.server.master.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.protocol.packet.heartbeat.HearbeatPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳处理
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public class HeartbeatHandler extends SimpleChannelInboundHandler<HearbeatPacket> {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HearbeatPacket msg) throws Exception {
        logger.info("收到来自：{} 的心跳包", ctx.channel().remoteAddress().toString());
        // 回复一个心跳包
        ctx.writeAndFlush(new HearbeatPacket());
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
