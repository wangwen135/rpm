package com.wwh.rpm.client.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.base.handler.BaseHandlerInitializer;
import com.wwh.rpm.common.handler.TransmissionHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 连接提供者
 * 
 * @author wangwh
 * @date 2021-1-28
 */
public class ConnectionProvider {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionProvider.class);

    private ClientManager clientManager;

    public ConnectionProvider(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public Channel getConnection2Target(String host, int port) throws Exception {
        EventLoopGroup workerGroup = clientManager.getWorkerGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);

        // 不自动读取数据
        b.option(ChannelOption.AUTO_READ, false);

        // 转发
        b.handler(new TransmissionHandler());

        ChannelFuture f = b.connect(host, port).sync();

        Channel channel = f.channel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.debug("到目标地址 {}:{} 的连接被关闭！", host, port);
            }
        });
        return channel;
    }

    // 获取到目标的连接
    // 是否自动读取

    // 获取到服务器的连接
    // 是否发送指令

    // 是否清空handler

}
