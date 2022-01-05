package com.wwh.rpm.client.pool.connection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.pool.connection.CommonConnection;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 客户端注册
 *
 * @author wangwh
 * @date 2020-12-31
 */
public class CommonRegistHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommonRegistHandler.class);

    private CommonConnection commonConnection;

    public CommonRegistHandler(CommonConnection commonConnection) {
        this.commonConnection = commonConnection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SuccessPacket) {
            logger.debug("服务端返回 注册成功！");
            // 注册成功移除处理器
            ctx.pipeline().remove(this);
        } else {
            throw new RPMException("注册失败：" + msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TokenPacket tokenPacket = new TokenPacket();
        tokenPacket.setToken(commonConnection.getToken());
        logger.debug("向服务器发送认证信息，token：{}", commonConnection.getToken());
        ctx.writeAndFlush(tokenPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("客户端注册异常！", cause);
        ctx.close();
    }
}
