package com.wwh.rpm.client.connection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.base.BaseClient;
import com.wwh.rpm.client.connection.FetchChannelWarp;
import com.wwh.rpm.client.connection.event.RegistSuccessEvent;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;
import com.wwh.rpm.common.enums.EncryptTypeEnum;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;

/**
 * 注册客户端
 * 
 * @author wangwh
 * @date 2021-1-29
 */
public class RegistClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RegistClientHandler.class);

    private BaseClient baseClient;
    private FetchChannelWarp fetchChannelWarp;
    private boolean notifyFetchChannelWarp = false;

    public RegistClientHandler(BaseClient baseClient, FetchChannelWarp fetchChannelWarp) {
        this(baseClient, fetchChannelWarp, false);
    }

    public RegistClientHandler(BaseClient baseClient, FetchChannelWarp fetchChannelWarp,
            boolean notifyFetchChannelWarp) {
        this.baseClient = baseClient;
        this.fetchChannelWarp = fetchChannelWarp;
        this.notifyFetchChannelWarp = notifyFetchChannelWarp;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SuccessPacket) {
            logger.debug("服务端返回 注册成功！");
            // 注册成功移除处理器
            ctx.pipeline().remove(this);

            // 通讯参数
            configCommunication(ctx);

            if (notifyFetchChannelWarp) {
                fetchChannelWarp.setSuccess(ctx.channel());
            } else {
                // 通知下一个处理器
                ctx.fireUserEventTriggered(new RegistSuccessEvent());
            }
        } else {
            RPMException e = new RPMException("注册失败");
            // 关闭链路
            ctx.close();
            // 通知失败
            fetchChannelWarp.setError(e);
        }
    }

    private void configCommunication(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        CommunicationConfig commConfig = baseClient.getCommConfig();
        boolean compression = commConfig.getEnableCompression();
        // 是否需要进行压缩
        if (compression) {
            int level = commConfig.getCompressionLevel();
            logger.debug("启用压缩，压缩级别{}", level);
            // 压缩
            pipeline.addFirst(new JdkZlibEncoder(level));
            pipeline.addFirst(new JdkZlibDecoder());
        }

        EncryptTypeEnum encryptType = commConfig.getEncryptType();
        if (EncryptTypeEnum.SIMPLE == encryptType) {
            logger.debug("使用简单加密");
            String sid = baseClient.getConfig().getServerConf().getSid();
            // 加密
            pipeline.addFirst(new SimpleEncryptionEncoder(sid));
            pipeline.addFirst(new SimpleEncryptionDecoder(sid));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TokenPacket tokenPacket = new TokenPacket();
        tokenPacket.setToken(baseClient.getToken());
        logger.debug("向服务器发送认证信息，token：{}", baseClient.getToken());
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
