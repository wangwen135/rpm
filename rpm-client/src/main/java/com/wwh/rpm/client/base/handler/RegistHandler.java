package com.wwh.rpm.client.base.handler;

import static com.wwh.rpm.protocol.ProtocolConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.config.pojo.CommConfig;
import com.wwh.rpm.common.enums.EncryptTypeEnum;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.AuthPacket;
import com.wwh.rpm.protocol.packet.auth.RegistPacket;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.ResultPacket;
import com.wwh.rpm.protocol.security.RandomNumberCodec;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;

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
        rand += AUTH_RANDOM_NUMBER_INCREMENT;
        AuthPacket reply = RandomNumberCodec.encrypt(rand, sid);
        ctx.writeAndFlush(reply);

    }

    private void handleTokenPacket(ChannelHandlerContext ctx, TokenPacket tokenPacket) {
        logger.info("注册成功！服务端返回的token：{}", tokenPacket.getToken());

        baseClient.setToken(tokenPacket.getToken());
        baseClient.setCommConfig(tokenPacket.getCommConfig());
        // 移除自己
        ctx.pipeline().remove(this);
        configCommunication(ctx);
    }

    /**
     * 配置压缩和加密方式
     *
     * @param ctx
     */
    private void configCommunication(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        CommConfig commConfig = baseClient.getCommConfig();

        logger.info("通信配置：{}", commConfig.toPrettyString());

        boolean compression = commConfig.getEnableCompression();
        // 是否需要进行压缩
        if (compression) {
            int level = commConfig.getCompressionLevel();
            // 压缩
            pipeline.addFirst(new JdkZlibEncoder(level));
            pipeline.addFirst(new JdkZlibDecoder());
        }

        EncryptTypeEnum encryptType = commConfig.getEncryptType();
        if (EncryptTypeEnum.NONE == encryptType) {
            logger.warn("注意：与服务端的通信未加密！");
        } else if (EncryptTypeEnum.SIMPLE == encryptType) {
            String sid = baseClient.getConfig().getServerConf().getSid();
            // 加密
            pipeline.addFirst(new SimpleEncryptionEncoder(sid));
            pipeline.addFirst(new SimpleEncryptionDecoder(sid));
        } else {
            logger.warn("暂时不支持的加密方式：{}", encryptType);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("注册客户端...");
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
