package com.wwh.rpm.server.master.handler;

import static com.wwh.rpm.common.Constants.DEFAULT_IDLE_TIMEOUT;

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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;

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

    private boolean registered = false;

    public AuthenticationHandler(MasterServer masterServer) {
        this.masterServer = masterServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (registered) {
            ctx.fireChannelRead(msg);
            return;
        }

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

            // 注册
            masterServer.registClient(cid, token, ctx.channel());
            setAttribute(ctx);
            registered = true;

            // 心跳处理 这个只有客户端主连接需要加，普通的转发连接不需要
            ctx.pipeline().addLast(new IdleStateHandler(DEFAULT_IDLE_TIMEOUT, 0, 0, TimeUnit.SECONDS));
            ctx.pipeline().addLast(new HeartbeatHandler());

        } else {
            throw new RPMException("随机数不正确，客户端认证失败！");
        }
    }

    private void handleTokenPacket(ChannelHandlerContext ctx, TokenPacket tokenPacket) {
        token = tokenPacket.getToken();

        // 验证token，失败直接关闭连接
        cid = masterServer.validateToken(token);
        // 通知客户端认证成功
        ctx.writeAndFlush(new SuccessPacket());

        setAttribute(ctx);
        // 移除handler
        ctx.pipeline().remove(this);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (registered) {
            ctx.fireExceptionCaught(cause);
            return;
        }

        logger.error("客户端认证异常，关闭连接", cause);
        ctx.close();
    }

    private void setAttribute(ChannelHandlerContext ctx) {
        Attribute<String> cidAttr = ctx.channel().attr(Constants.ATTR_KEY_CID);
        cidAttr.set(cid);
        Attribute<String> tokenAttr = ctx.channel().attr(Constants.ATTR_KEY_TOKEN);
        tokenAttr.set(token);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        masterServer.unregistClient(cid);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
