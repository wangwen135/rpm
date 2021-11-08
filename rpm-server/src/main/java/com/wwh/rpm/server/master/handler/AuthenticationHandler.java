package com.wwh.rpm.server.master.handler;

import static com.wwh.rpm.common.Constants.ATTR_KEY_CID;
import static com.wwh.rpm.common.Constants.ATTR_KEY_TOKEN;
import static com.wwh.rpm.common.Constants.DEFAULT_IDLE_TIMEOUT;
import static com.wwh.rpm.common.Constants.ENCODE_HANDLER_NAME;
import static com.wwh.rpm.protocol.ProtocolConstants.AUTH_RANDOM_NUMBER_INCREMENT;
import static com.wwh.rpm.protocol.ProtocolConstants.AUTH_RANDOM_NUMBER_MAX;
import static com.wwh.rpm.protocol.ProtocolConstants.AUTH_RANDOM_NUMBER_MIN;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.config.pojo.CommConfig;
import com.wwh.rpm.common.enums.EncryptTypeEnum;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.AuthPacket;
import com.wwh.rpm.protocol.packet.auth.RegistPacket;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;
import com.wwh.rpm.protocol.security.RandomNumberCodec;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;
import com.wwh.rpm.server.master.MasterServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
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
            logger.warn("【主服务】认证处理收到异常包：{}，来自地址：{}", msg, ctx.channel().remoteAddress());
            throw new RPMException("客户端未注册！");
        }
    }

    private void handleRegistPacket(ChannelHandlerContext ctx, RegistPacket registPacket) {

        cid = registPacket.getCid();
        logger.debug("【主服务】处理注册包，cid={}", cid);
        if (StringUtils.isBlank(cid)) {
            throw new RPMException("客户端cid不能为空");
        }

        random = RandomUtils.nextInt(AUTH_RANDOM_NUMBER_MIN, AUTH_RANDOM_NUMBER_MAX);
        String sid = masterServer.getConfig().getSid();

        logger.debug("【主服务】向客户端发送随机数：{}", random);

        AuthPacket reply = RandomNumberCodec.encrypt(random, sid);
        ctx.writeAndFlush(reply);
    }

    private void handleAuthPacket(ChannelHandlerContext ctx, AuthPacket authPacket) {
        logger.debug("【主服务】开始处理认证包...");
        if (StringUtils.isBlank(cid)) {
            throw new RPMException("客户端未注册直接进行认证");
        }

        String sid = masterServer.getConfig().getSid();
        int rand = RandomNumberCodec.decrypt(authPacket, sid);

        // 比对随机数
        if ((rand - AUTH_RANDOM_NUMBER_INCREMENT) == random) {
            token = UUID.randomUUID().toString().replace("-", "");
            logger.debug("【主服务】认证通过，返回token：{}", token);

            TokenPacket tokenPacket = new TokenPacket();
            // 将通讯配置返回给客户端
            CommConfig commConfig = masterServer.getConfig().getCommConfig();
            tokenPacket.setCommConfig(commConfig.code());
            tokenPacket.setToken(token);
            ctx.writeAndFlush(tokenPacket);

            logger.debug("【主服务】注册客户端：{}，token：{} ，channel：{}", cid, token, ctx.channel());

            masterServer.registClient(cid, token, ctx.channel());
            setAttribute(ctx);
            registered = true;

            logger.debug("【主服务】添加心跳处理");
            // 心跳处理 这个只有客户端主连接需要加，普通的转发连接不需要
            ctx.pipeline().addAfter(ENCODE_HANDLER_NAME, "idle",
                    new IdleStateHandler(DEFAULT_IDLE_TIMEOUT, 0, 0, TimeUnit.SECONDS));
            ctx.pipeline().addAfter(ENCODE_HANDLER_NAME, "heartbeat", new HeartbeatHandler());

            configCommunication(ctx);
        } else {
            throw new RPMException("随机数不正确，客户端认证失败！");
        }
    }

    private void handleTokenPacket(ChannelHandlerContext ctx, TokenPacket tokenPacket) {
        token = tokenPacket.getToken();
        logger.debug("【主服务】处理token包，token={}", token);

        // 验证token，失败直接关闭连接
        cid = masterServer.validateToken(token);
        // 通知客户端认证成功
        ctx.writeAndFlush(new SuccessPacket());

        setAttribute(ctx);
        // 移除handler
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.remove(this);

        configCommunication(ctx);
    }

    private void configCommunication(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        boolean compression = masterServer.getConfig().getEnableCompression();
        // 是否需要进行压缩
        if (compression) {
            int level = masterServer.getConfig().getCompressionLevel();
            // 压缩
            pipeline.addFirst(new JdkZlibEncoder(level));
            pipeline.addFirst(new JdkZlibDecoder());
        }

        EncryptTypeEnum encryptType = masterServer.getConfig().getEncryptType();
        if (EncryptTypeEnum.NONE == encryptType) {
            logger.warn("注意：通信未加密！");
        } else if (EncryptTypeEnum.SIMPLE == encryptType) {
            String sid = masterServer.getConfig().getSid();
            // 加密
            pipeline.addFirst(new SimpleEncryptionEncoder(sid));
            pipeline.addFirst(new SimpleEncryptionDecoder(sid));
        } else {
            // TODO 其他加密方式暂不支持
            logger.warn("！！！暂时不支持的加密方式：{}", encryptType);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (registered) {
            masterServer.unregistClientByToken(token);
            ctx.fireExceptionCaught(cause);
            return;
        }

        logger.warn("【主服务】客户端认证异常：{}，关闭连接：{}", cause.getMessage(), ctx.channel().remoteAddress());
        logger.debug("错误信息：", cause);

        ctx.close();
    }

    private void setAttribute(ChannelHandlerContext ctx) {
        Attribute<String> cidAttr = ctx.channel().attr(ATTR_KEY_CID);
        cidAttr.set(cid);
        Attribute<String> tokenAttr = ctx.channel().attr(ATTR_KEY_TOKEN);
        tokenAttr.set(token);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("【主服务】连接:{} 断开了", ctx.channel());
        if (registered) {
            masterServer.unregistClientByToken(token);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("【主服务】有新进入连接：{}", ctx.channel());
    }
}
