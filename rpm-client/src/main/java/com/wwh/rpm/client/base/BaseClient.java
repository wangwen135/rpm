package com.wwh.rpm.client.base;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.ClientStarter;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BaseClient {

    private static final Logger logger = LoggerFactory.getLogger(BaseClient.class);

    private ClientManager clientManager;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private EventLoopGroup workerGroup;

    private Channel channel;

    /**
     * 服务端返回的token
     */
    private String token;

    public BaseClient(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void shutdown() {
        if (!isRunning()) {
            logger.warn("主服务没有启动");
            return;
        }
        if (channel != null) {
            channel.close();
        }
        // 考虑一下这个workerGroup 是否需要通用
        // 如通用则需要由上层传下来，由上层关闭
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public void start() throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("客户端正在运行！");
            return;
        }
        ServerConf serverConf = clientManager.getConfig().getServerConf();

        workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.handler(new BaseHandlerInitializer(this));

        ChannelFuture f = b.connect(serverConf.getHost(), serverConf.getPort()).sync();
        channel = f.channel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.warn("客户端主连接被关闭");
                ClientStarter.shutdownNotify();
            }
        });
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ClientConfig getConfig() {
        return clientManager.getConfig();
    }
}
