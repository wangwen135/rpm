package com.wwh.rpm.client.subserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.subserver.Subserver;
import com.wwh.rpm.common.handler.TransmissionHandler;

import io.netty.bootstrap.Bootstrap;
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
    private static final Logger logger = LoggerFactory.getLogger(SubserverProxyHandler.class);

    private Subserver subserver;

    public SubserverProxyHandler(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel inboundChannel = ctx.channel();

        logger.debug("子服务收到新连接：{} 开始建立到服务器链路", inboundChannel);

        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop()).channel(ctx.channel().getClass());

        b.option(ChannelOption.AUTO_READ, false);

        b.handler(new Sub2ServerHandlerInitializer(subserver, inboundChannel));

        String serverHost = subserver.getConfig().getServerConf().getHost();
        int serverPort = subserver.getConfig().getServerConf().getPort();

        ChannelFuture f = b.connect(serverHost, serverPort);

        // 到服务端的通道
        Channel outboundChannel = f.channel();

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    logger.debug("本地到服务器的链路：{} 建立成功", outboundChannel.toString());
                    // 添加转发handler
                    ctx.pipeline().addLast(new TransmissionHandler(outboundChannel));
                } else {
                    logger.debug("本地到服务器的链路建立失败，关闭连接");
                    inboundChannel.close();
                }
            }
        });
    }
}
