package com.wwh.rpm.client.subserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.subserver.Subserver;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.protocol.packet.general.ResultPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 子服务认证
 * 
 * @author wangwh
 * @date 2021-1-4
 */
public class SubserverRegistHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SubserverRegistHandler.class);

    private Subserver subserver;
    private int stage = 0;

    public SubserverRegistHandler(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SuccessPacket) {
            if (stage == 1) {// 注册成功

                sendForwardInfo(ctx);
                stage = 2;
            } else if (stage == 2) {// 转发配置成功
                // 移除自己，移除编码解码器
                ctx.pipeline().remove(this);
                ctx.pipeline().remove("encoder");
                ctx.pipeline().remove("decoder");
                // 通知CHANEL开始读取数据
                // TODO

                // 连接可用
            } else {
                throw new RPMException("错误状态：" + stage);

            }

        } else if (msg instanceof ResultPacket) {
            ResultPacket result = (ResultPacket) msg;
            throw new RPMException(result.getMsg());
        } else {
            String errMsg = "子服务注册失败";
            if (stage == 1) {
                errMsg = "子服务客户端认证失败，请检查token";
            }
            throw new RPMException(errMsg);
        }
    }

    /**
     * 发送转发地址端口信息
     * 
     * @param ctx
     */
    private void sendForwardInfo(ChannelHandlerContext ctx) {

        String host = subserver.getForwardConfig().getForwardHost();
        int port = subserver.getForwardConfig().getForwardPort();
        ForwardCommandPacket fcp = new ForwardCommandPacket();
        fcp.setHost(host);
        fcp.setPort(port);

        ctx.writeAndFlush(fcp);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        // 发送认证信息
        String token = subserver.getToken();
        TokenPacket tokenPacket = new TokenPacket();
        tokenPacket.setToken(token);

        ctx.writeAndFlush(tokenPacket);
        stage = 1;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("子服务链接异常！", cause);
        ctx.close();
    }
}
