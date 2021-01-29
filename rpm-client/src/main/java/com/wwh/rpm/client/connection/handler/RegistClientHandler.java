package com.wwh.rpm.client.connection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.connection.FetchChannelWarp;
import com.wwh.rpm.client.connection.event.RegistSuccessEvent;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 注册客户端
 * 
 * @author wangwh
 * @date 2021-1-29
 */
public class RegistClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RegistClientHandler.class);

    private String token;
    private FetchChannelWarp fetchChannelWarp;

    public RegistClientHandler(String token, FetchChannelWarp fetchChannelWarp) {
        this.token = token;
        this.fetchChannelWarp = fetchChannelWarp;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SuccessPacket) {
            logger.debug("服务端返回 注册成功！");
            // 注册成功移除处理器
            ctx.pipeline().remove(this);
            // 通知下一个处理器
            ctx.fireUserEventTriggered(new RegistSuccessEvent());
        } else {
            RPMException e = new RPMException("注册失败");
            // 关闭链路
            ctx.close();
            // 通知失败
            fetchChannelWarp.setError(e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TokenPacket tokenPacket = new TokenPacket();
        tokenPacket.setToken(token);
        logger.debug("向服务器发送认证信息，token：{}", token);
        ctx.writeAndFlush(tokenPacket);
        ctx.read();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("注册客户端异常", cause);
        ctx.close();
        // 通知失败
        fetchChannelWarp.setError(new RPMException("注册客户端异常", cause));
    }
}
