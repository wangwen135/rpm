package com.wwh.rpm.client.subserver.handler;

import com.wwh.rpm.client.config.pojo.ForwardOverServer;
import com.wwh.rpm.client.subserver.Sub2ServerHandlerInitializer;
import com.wwh.rpm.client.subserver.Subserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;

/**
 * 转发
 * 
 * @author wangwh
 * @date 2021-1-4
 */
public class SubserverProxyHandler extends ChannelInboundHandlerAdapter {
    private Subserver subserver;
    
    private Channel outboundChannel;
    
    
    public SubserverProxyHandler(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop()).channel(ctx.channel().getClass());
        b.handler(new Sub2ServerHandlerInitializer(subserver));
        // b.option(ChannelOption.AUTO_READ, false);

        String forwardHost = subserver.getForwardConfig().getForwardHost();
        int forwardPort = subserver.getForwardConfig().getForwardPort();
        ChannelFuture f = b.connect(forwardHost, forwardPort);

        outboundChannel = f.channel();
        
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // 连接完成，开始读取第一个数据
                    inboundChannel.read();
                } else {
                    // 如果连接尝试失败，关闭连接
                    inboundChannel.close();
                }
            }
        });

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        // 清除数据，开始读取下一个块
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * 在所有排队写请求刷新后关闭指定的通道。
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
