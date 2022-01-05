package com.wwh.rpm.client.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.config.pojo.ServerConfig;
import com.wwh.rpm.client.connection.handler.ToServerHandlerInitializer;
import com.wwh.rpm.client.connection.handler.ToTargetHandlerInitializer;
import com.wwh.rpm.protocol.packet.AbstractPacket;

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

    /**
     * 同步获取一个到目标地址的Channel
     * 
     * @param host
     * @param port
     * @return Channel
     * @throws Exception
     */
    public Channel getConnection2TargetSync(String host, int port) throws Exception {
        EventLoopGroup workerGroup = clientManager.getWorkerGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        // 不自动读取数据
        b.option(ChannelOption.AUTO_READ, false);
        // 打印日志
        b.handler(new ToTargetHandlerInitializer(clientManager));
        ChannelFuture f = b.connect(host, port).sync();
        Channel channel = f.channel();
        if (logger.isDebugEnabled()) {
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.debug("到目标地址 {}:{} 的连接 {} 被关闭！", host, port, future.channel());
                }
            });
        }
        return channel;
    }

    /**
     * <pre>
     * 获取一个到目标地址的Channel包装
     * 不自动读取数据
     * 使用者需要添加Handler
     * </pre>
     * 
     * @param host
     * @param port
     * @return Channel的包装对象
     */
    public FetchChannelWarp getConnection2Target(String host, int port) {
        final FetchChannelWarp warp = new FetchChannelWarp();
        EventLoopGroup workerGroup = clientManager.getWorkerGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        // 不自动读取数据
        b.option(ChannelOption.AUTO_READ, false);
        // 打印日志
        b.handler(new ToTargetHandlerInitializer(clientManager));
        ChannelFuture f = b.connect(host, port);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("链接建立成功！");
                    warp.setSuccess(future.channel());
                } else {
                    logger.info("链接建立失败！错误消息：{}", future.cause().getMessage());
                    warp.setError(future.cause());
                }
            }
        });
        return warp;
    }

    /**
     * <pre>
     * 获取到服务端的连接（不自动读取数据）
     * 发送指令并等待返回成功
     * 移除编码解码器
     * </pre>
     * 
     * @param packet 待发送的指令
     * @return Channel的包装对象
     */
    public FetchChannelWarp getCleanConnection2Server(AbstractPacket packet) {
        final FetchChannelWarp warp = new FetchChannelWarp();

        ServerConfig serverConf = clientManager.getConfig().getServerConf();
        EventLoopGroup workerGroup = clientManager.getWorkerGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        // 不自动读取数据
        b.option(ChannelOption.AUTO_READ, false);

        b.handler(new ToServerHandlerInitializer(clientManager, warp, packet));

        ChannelFuture f = b.connect(serverConf.getHost(), serverConf.getPort());

        Channel channel = f.channel();

        if (logger.isDebugEnabled()) {
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.debug("到服务器的连接 {} 被关闭！", future.channel());
                }
            });
        }
        return warp;
    }

    /**
     * <pre>
     * 【同步】获取到服务端的连接（不自动读取数据）
     * 发送指令并等待返回成功
     * 移除编码解码器
     * </pre>
     * 
     * @param packet 待发送的指令
     * @return Channel
     * @throws Exception
     */
    public Channel getCleanConnection2ServerSync(AbstractPacket packet) throws Exception {
        FetchChannelWarp warp = getCleanConnection2Server(packet);
        return warp.getChannelOnce();
    }

    /**
     * <pre>
     * 获取到服务端的连接
     * 只完成注册动作
     * 包含编码/解码处理器
     * </pre>
     * 
     * @return
     */
    public FetchChannelWarp getConnection2Server() {
        return getCleanConnection2Server(null);
    }

}
