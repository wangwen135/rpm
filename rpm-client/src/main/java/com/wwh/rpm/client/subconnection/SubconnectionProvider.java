package com.wwh.rpm.client.subconnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.pool.BufferManager;
import com.wwh.rpm.client.pool.ConnectionPool;
import com.wwh.rpm.client.subconnection.handler.ToTargetHandlerInitializer;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 子链接提供者
 * 
 * @author WWH
 *
 */
public class SubconnectionProvider {
    private static final Logger logger = LoggerFactory.getLogger(SubconnectionProvider.class);

    private EventLoopGroup workerGroup;

    private Map<Long, Channel> channels = new ConcurrentHashMap<>();

    private ClientManager clientManager;

    public SubconnectionProvider(ClientManager clientManager) {
        workerGroup = new NioEventLoopGroup();
        this.clientManager = clientManager;
    }

    /**
     * 建立到目标的连接并注册
     * 
     * @param fcPacket
     */
    public void connect2Target(ForwardCommandPacket fcPacket) {
        final Long id = fcPacket.getId();
        final ConnectionPool pool = clientManager.getConnectionPool();
        final BufferManager bufferManager = pool.getBufferManager();
        final String host = fcPacket.getHost();
        final int port = fcPacket.getPort();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.handler(new ToTargetHandlerInitializer(id, clientManager));

        ChannelFuture future = b.connect(host, port);

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("到目标【{}:{}】的连接（{}）建立成功", host, port, id);
                    Channel channel = future.channel();
                    bufferManager.registerSubChannel(id, channel);

                    channels.put(id, channel);

                    channel.closeFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            logger.debug("到目标【{}:{}】的连接（{}）关闭", host, port, id);
                            pool.getConnection().closeRemoteSubChannel(id);
                            bufferManager.unregister(id);
                            channels.remove(id);
                        }
                    });
                } else {
                    logger.debug("到目标【{}:{}】的连接（{}）建立失败", host, port, id, future.cause());
                    pool.getConnection().closeRemoteSubChannel(id);
                    bufferManager.unregister(id);
                    channels.remove(id);
                }
            }
        });
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