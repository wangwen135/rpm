package com.wwh.rpm.server.subserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.handler.TransmissionHandler;
import com.wwh.rpm.server.subserver.Subserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * 用于建立远程通道
 * 
 * @author wangwh
 * @date 2021-1-26
 */
public class RemoteChannelBuildHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RemoteChannelBuildHandler.class);

    private Subserver subserver;
    private Channel outboundChannel;

    public RemoteChannelBuildHandler(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel inboundChannel = ctx.channel();

        logger.debug("子服务收到新连接：{} 开始建立到客户端的链路", inboundChannel);

        // 请求一个客户端通道
        // 阻塞方法
        outboundChannel = subserver.acquireClientForwardChannel(inboundChannel);
        // 添加转发handler
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast(new TransmissionHandler(outboundChannel));

        pipeline.remove(this);
        inboundChannel.read();
        outboundChannel.read();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("构建客户端通道时异常", cause);
        ctx.close();
        if (outboundChannel != null) {
            outboundChannel.close();
        }
    }
}
