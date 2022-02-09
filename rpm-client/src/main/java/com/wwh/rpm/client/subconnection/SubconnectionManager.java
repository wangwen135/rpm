package com.wwh.rpm.client.subconnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SubconnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(SubconnectionManager.class);

    private EventLoopGroup workerGroup;

//    private AtomicLong counter = new AtomicLong(0);

    private Map<Long, Channel> channels = new ConcurrentHashMap<>();

    public SubconnectionManager() {
        workerGroup = new NioEventLoopGroup();
    }

    // 建立连接
    public void connect2Target(ForwardCommandPacket fcPacket) {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
    }

    public void close() {

        logger.info("关闭全部子连接");
        for (Channel channel : channels.values()) {
            channel.close();
        }

        logger.info("关闭线程池...");
        workerGroup.shutdownGracefully();
    }
}
