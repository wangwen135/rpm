package com.wwh.rpm.tools.proxy;

import com.wwh.rpm.common.utils.RpmMsgPrinter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.nio.NioHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 从netty example 中复制的
 * 
 * @author wangwh
 * @date 2021-2-24
 */
public final class SocksServer {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "1080"));
    static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("debug", "false"));

    public static void main(String[] args) throws Exception {

        RpmMsgPrinter.printMsg("启动 SocksServer...");
        RpmMsgPrinter.printMsg("监听地址：{}:{}", HOST, PORT);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            if (DEBUG) {
                b.handler(new LoggingHandler(LogLevel.INFO));
            }
            b.childHandler(new SocksServerInitializer());

            b.bind(HOST, PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        RpmMsgPrinter.printMsg("SocksServer 结束！");
    }
}