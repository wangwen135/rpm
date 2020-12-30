package com.wwh.rpm.client.base.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.protocol.packet.heartbeat.HearbeatPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <pre>
 * 客户端心跳处理
 * 
 * 60秒没有写数据就发一个心跳包，次数服务端会回一个心跳包
 * 如果200秒没有收到服务端的回复则关闭
 * </pre>
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果是心跳包就不再往后处理了
        if (msg instanceof HearbeatPacket) {
            logger.debug("收到服务端响应的心跳包");
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.error("到达指定时间间隔没有收到服务端的任何数据，关闭客户端！");
                ctx.fireUserEventTriggered(evt);
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                logger.debug("发送心跳包");
                ctx.writeAndFlush(new HearbeatPacket());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
