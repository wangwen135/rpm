package com.wwh.rpm.server.master.handler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.AuthPacket;
import com.wwh.rpm.protocol.packet.auth.RegistPacket;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;
import com.wwh.rpm.protocol.security.RandomNumberCodec;
import com.wwh.rpm.server.master.MasterServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.concurrent.ScheduledFuture;

/**
 * 认证
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    private MasterServer masterServer;

    private int random;

    private String cid;
    private String token;

    private ScheduledFuture<?> timeOutFuture;

    public AuthenticationHandler(MasterServer masterServer) {
        this.masterServer = masterServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RegistPacket) {
            RegistPacket regist = (RegistPacket) msg;
            handleRegistPacket(ctx, regist);
        } else if (msg instanceof AuthPacket) {
            AuthPacket auth = (AuthPacket) msg;
            handleAuthPacket(ctx, auth);
        } else if (msg instanceof TokenPacket) {
            TokenPacket token = (TokenPacket) msg;
            handleTokenPacket(ctx, token);
        } else {
            throw new RPMException("客户端未注册！");
        }
    }

    private void handleRegistPacket(ChannelHandlerContext ctx, RegistPacket registPacket) {
        cid = registPacket.getCid();
        if (StringUtils.isBlank(cid)) {
            throw new RPMException("客户端cid不能为空");
        }

        random = RandomUtils.nextInt(1111111, 9999999);
        String sid = masterServer.getConfig().getSid();

        logger.debug("向客户端发送随机数：{}", random);

        AuthPacket reply = RandomNumberCodec.encrypt(random, sid);
        ctx.writeAndFlush(reply);
    }

    private void handleAuthPacket(ChannelHandlerContext ctx, AuthPacket authPacket) {
        if (StringUtils.isBlank(cid)) {
            throw new RPMException("客户端未注册直接进行认证");
        }

        String sid = masterServer.getConfig().getSid();
        int rand = RandomNumberCodec.decrypt(authPacket, sid);

        // 比对随机数
        if ((rand - 1) == random) {
            // 返回token
            token = UUID.randomUUID().toString();
            TokenPacket tokenPacket = new TokenPacket();
            tokenPacket.setToken(token);
            ctx.writeAndFlush(tokenPacket);
            authSuccess(ctx);
        } else {
            throw new RPMException("随机数不正确，客户端认证失败！");
        }
    }

    private void handleTokenPacket(ChannelHandlerContext ctx, TokenPacket tokenPacket) {
        token = tokenPacket.getToken();

        // 验证token，失败直接关闭连接
        cid = masterServer.validateToken(token);

        // 通知客户端
        ctx.writeAndFlush(new SuccessPacket());
        authSuccess(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("客户端认证异常，关闭连接", cause);
        ctx.close();
    }

    private void authSuccess(ChannelHandlerContext ctx) {
        // 深圳channel属性
        Attribute<String> attribute = ctx.channel().attr(Constants.ATTR_KEY_CID);
        attribute.set(cid);

        // 注册
        masterServer.registClient(cid, token);
        // 关闭定时器
        timeOutFuture.cancel(true);

        // 移除handler
        ctx.pipeline().remove(this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("新连接，启动定时器，用于关闭连接");

        timeOutFuture = ctx.executor().schedule(new RegistTimeOutTask(ctx), Constants.DEFAULT_REGIST_TIMEOUT,
                TimeUnit.SECONDS);
    }
}

class RegistTimeOutTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RegistTimeOutTask.class);

    private final ChannelHandlerContext ctx;

    public RegistTimeOutTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        if (!ctx.channel().isOpen()) {
            return;
        }
        logger.warn("注册超时，关闭连接！");
        ctx.close();
    }

}
