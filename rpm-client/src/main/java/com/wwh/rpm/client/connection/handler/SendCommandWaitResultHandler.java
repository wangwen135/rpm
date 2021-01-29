package com.wwh.rpm.client.connection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.connection.FetchChannelWarp;
import com.wwh.rpm.client.connection.event.RegistSuccessEvent;
import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.AbstractPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * <pre>
 * 等待命令返回结果
 * 同时移除编码器
 * </pre>
 * 
 * @author wangwh
 * @date 2021-1-29
 */
public class SendCommandWaitResultHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SendCommandWaitResultHandler.class);

    private AbstractPacket packet;
    private FetchChannelWarp fetchChannelWarp;

    public SendCommandWaitResultHandler(AbstractPacket packet, FetchChannelWarp fetchChannelWarp) {
        this.packet = packet;
        this.fetchChannelWarp = fetchChannelWarp;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof RegistSuccessEvent) {
            logger.debug("向服务端发送指令:{}", packet);
            ctx.writeAndFlush(packet);
            // 读取数据
            ctx.read();
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SuccessPacket) {
            logger.debug("服务端返回成功！指令已经响应！");

            ChannelPipeline pipeline = ctx.pipeline();
            // 移除自己，移除编码解码器
            pipeline.remove(this);
            pipeline.remove(Constants.ENCODE_HANDLER_NAME);
            pipeline.remove(Constants.DECODE_HANDLER_NAME);

            fetchChannelWarp.setSuccess(ctx.channel());
        } else {
            RPMException e = new RPMException("指令响应失败");
            ctx.close();
            fetchChannelWarp.setError(e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("发送指令异常", cause);
        ctx.close();

        // 通知失败
        fetchChannelWarp.setError(new RPMException("发送指令异常", cause));
    }
}
