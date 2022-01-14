package com.wwh.rpm.client.pool.connection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConfig;
import com.wwh.rpm.client.pool.ConnectionPool;
import com.wwh.rpm.client.pool.RpmConnection;
import com.wwh.rpm.client.pool.connection.handler.RegisterHandlerInitializer;
import com.wwh.rpm.common.exception.RPMException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 主连接（注册连接）
 * 
 * @author wangwh
 * @date 2022-1-5
 */
public class RegisterConnection implements RpmConnection {

    private static final Logger logger = LoggerFactory.getLogger(RegisterConnection.class);

    private ConnectionPool connectionPool;

    private Integer id;

    private Object lock = new Object();

    private EventLoopGroup workerGroup;
    private Channel channel;
    /**
     * 服务端返回的token
     */
    private String token;

    public RegisterConnection(ConnectionPool connectionPool, Integer id) {
        this.connectionPool = connectionPool;
        this.id = id;
    }

    /**
     * 启动连接，同步
     * 
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        logger.debug("启动主连接...");

        ClientConfig clientConfig = connectionPool.getClientConfig();
        ServerConfig serverConf = clientConfig.getServerConf();

        workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.SO_KEEPALIVE, true);

        // 注册handler
        b.handler(new RegisterHandlerInitializer(this));
        try {
            ChannelFuture f = b.connect(serverConf.getHost(), serverConf.getPort()).sync();
            channel = f.channel();
            connectionPool.registerChannel(id, channel);

            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.error("客户端【主连接】被关闭！");
                    workerGroup.shutdownGracefully();

                    // connectionPool.unregisterChannel(id);

                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    // ClientStarter.shutdownNotify();
                    connectionPool.communicationInterrupt();
                }
            });
        } catch (InterruptedException e) {
            logger.error("连接到服务器异常", e);
            workerGroup.shutdownGracefully();
            throw e;
        }

    }

    @Override
    public void shutdown() {
        logger.info("关闭客户端主连接：{}", channel);
        synchronized (lock) {
            lock.notifyAll();
        }
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public String getToken() {
        return token;
    }

    /**
     * 等待获取到token
     * 
     * @return
     */
    public String waitToken() {
        if (token != null) {
            return token;
        }
        // 最多等30秒
        for (int i = 1; i < 30; i++) {
            synchronized (lock) {
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.warn("等待获取token【{}】", i);
        }
        if (StringUtils.isBlank(token)) {
            throw new RPMException("token 为空！");
        } else {
            return token;
        }
    }

    public void setToken(String token) {
        this.token = token;
        // 通知
        synchronized (lock) {
            lock.notifyAll();
        }
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

    @Override
    public Integer getConnectionId() {
        return id;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

}
