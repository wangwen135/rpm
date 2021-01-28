
package com.wwh.rpm.common.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.RPMException;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 转发处理
 * 
 * @author wangwh
 * @date 2021-1-5
 */
public class TransmissionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TransmissionHandler.class);

    private Channel outboundChannel;

    /**
     * 之后需要设置outboundChannel<br>
     * 否则无法转发数据
     */
    public TransmissionHandler() {
    }

    public void setOutboundChannel(Channel outboundChannel) {
        this.outboundChannel = outboundChannel;
        if (!outboundChannel.isActive()) {
            throw new RPMException("输出通道不是活动状态");
        }
    }

    public TransmissionHandler(Channel outboundChannel) {
        setOutboundChannel(outboundChannel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("【转发】目标连接已经建立");
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel == null) {
            throw new RPMException("输出通道不能为空");
        }
        if (!outboundChannel.isActive()) {
            logger.debug("【转发】输出通道不是活跃状态，关闭连接");
            ctx.close();
        } else {
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("【转发】连接{}已关闭", ctx.channel());
        closeOnFlush(outboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("转发处理异常", cause);
        closeOnFlush(ctx.channel());
    }

    /**
     * 在所有排队写请求刷新后关闭指定的通道。
     */
    private void closeOnFlush(Channel ch) {
        if (ch != null && ch.isActive()) {
            logger.debug("【转发】清空并关闭通道：{}", ch);
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}