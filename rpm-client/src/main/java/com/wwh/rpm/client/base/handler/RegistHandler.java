package com.wwh.rpm.client.base.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.AuthPacket;
import com.wwh.rpm.protocol.packet.auth.RegistPacket;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.ResultPacket;
import com.wwh.rpm.protocol.security.RandomNumberCodec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 客户端注册
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class RegistHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RegistHandler.class);
    private BaseClient baseClient;

    public RegistHandler(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthPacket) {
            AuthPacket auth = (AuthPacket) msg;
            handleAuthPacket(ctx, auth);
        } else if (msg instanceof TokenPacket) {
            TokenPacket token = (TokenPacket) msg;
            handleTokenPacket(ctx, token);
        } else if (msg instanceof ResultPacket) {
            ResultPacket result = (ResultPacket) msg;
            throw new RPMException("客户端注册失败！" + result.getMsg());
        } else {
            throw new RPMException("客户端注册失败！");
        }
    }

    private void handleAuthPacket(ChannelHandlerContext ctx, AuthPacket authPacket) {
        ClientConfig clientConfig = baseClient.getConfig();
        String sid = clientConfig.getServerConf().getSid();

        int rand = RandomNumberCodec.decrypt(authPacket, sid);
        logger.debug("收到的随机数为：{}", rand);
        rand++;
        AuthPacket reply = RandomNumberCodec.encrypt(rand, sid);
        ctx.writeAndFlush(reply);

    }

    private void handleTokenPacket(ChannelHandlerContext ctx, TokenPacket tokenPacket) {
        logger.info("注册成功！服务端返回的token：{}", tokenPacket.getToken());

        baseClient.setToken(tokenPacket.getToken());
        // 移除自己
        ctx.pipeline().remove(this);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 注册客户端
        ClientConfig clientConfig = baseClient.getConfig();

        RegistPacket registPacket = new RegistPacket();
        registPacket.setCid(clientConfig.getCid());

        ctx.writeAndFlush(registPacket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("客户端注册异常！", cause);
        ctx.close();
    }
}
