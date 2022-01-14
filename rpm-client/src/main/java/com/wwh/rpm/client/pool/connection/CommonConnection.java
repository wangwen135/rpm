package com.wwh.rpm.client.pool.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConfig;
import com.wwh.rpm.client.pool.ConnectionPool;
import com.wwh.rpm.client.pool.RpmConnection;
import com.wwh.rpm.client.pool.connection.handler.CommonHandlerInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 通信连接
 * 
 * @author wangwh
 * @date 2022-1-5
 */
public class CommonConnection implements RpmConnection {

    private static final Logger logger = LoggerFactory.getLogger(CommonConnection.class);

    private ConnectionPool connectionPool;

    private Integer id;

    private EventLoopGroup workerGroup;
    private Channel channel;

    private volatile boolean shutdown = false;

    public CommonConnection(ConnectionPool connectionPool, Integer id) {
        this.connectionPool = connectionPool;
        this.id = id;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    /**
     * 启动连接，异步
     */
    @Override
    public void start() throws Exception {
        logger.debug("启动通信连接【{}】...", id);

        ClientConfig clientConfig = connectionPool.getClientConfig();
        ServerConfig serverConf = clientConfig.getServerConf();

        workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.SO_KEEPALIVE, true);

        // 注册handler
        b.handler(new CommonHandlerInitializer(this));

        ChannelFuture f = b.connect(serverConf.getHost(), serverConf.getPort());
        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("通信连接【{}】已建立", id);
                    channel = f.channel();

                    connectionPool.registerChannel(id, channel);

                    channel.closeFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            logger.error("通信连接【{}】被关闭！", id);
                            workerGroup.shutdownGracefully();
                            connectionPool.unregisterChannel(id);
                        }
                    });
                    if (shutdown) {
                        logger.warn("链接已经标记为关闭，结束刚刚建立的连接【{}】", id);
                        channel.close();
                    }
                } else {
                    logger.error("通信连接【{}】建立失败", id, future.cause());
                    workerGroup.shutdownGracefully();
                    // 这里也需要取消注册
                    connectionPool.unregisterChannel(id);
                }
            }
        });
    }

    /**
     * 关闭
     */
    @Override
    public void shutdown() {
        logger.info("关闭客户端通信连接【{}】", id);
        shutdown = true;
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public Integer getConnectionId() {
        return id;
    }

    @Override
    public String getToken() {
        return connectionPool.getToken();
    }

    /**
     * 获取客户端配置
     * 
     * @return
     */
    @Override
    public ClientConfig getClientConfig() {
        return connectionPool.getClientConfig();
    }

}
