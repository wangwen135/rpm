package com.wwh.rpm.ctrl.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.ctrl.Closeer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 控制服务
 * 
 * @author wangwh
 * @date 2021-1-21
 */
public class CtrlServer {
    private static final Logger logger = LoggerFactory.getLogger(CtrlServer.class);

    private static final String inetHost = "127.0.0.1";

    private Closeer closeer;

    private Channel channel;
    private EventLoopGroup eventGroup;

    public CtrlServer(Closeer closeer) {
        this.closeer = closeer;
    }

    public void shutdown() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        if (eventGroup != null) {
            eventGroup.shutdownGracefully();
        }
    }

    public void start(int ctrlPort) throws Exception {
        logger.debug("准备在端口 {} 启动控制服务...", ctrlPort);
        eventGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(eventGroup, eventGroup).channel(NioServerSocketChannel.class);
            b.childHandler(new CtrlHandlerInitializer(closeer));
            channel = b.bind(inetHost, ctrlPort).sync().channel();

            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("控制服务关闭！");
                    eventGroup.shutdownGracefully();
                }
            });

            RpmMsgPrinter.printMsg("控制服务启动在端口 {}", ctrlPort);

        } catch (Exception e) {
            eventGroup.shutdownGracefully();
            throw e;
        }
    }

}
