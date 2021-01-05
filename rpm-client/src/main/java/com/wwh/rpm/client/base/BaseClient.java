package com.wwh.rpm.client.base;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.base.handler.BaseHandlerInitializer;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <pre>
 * 客户端
 * 这个可以走ssl，通讯量比较小，主要用于维持长连接。。
 * </pre>
 * 
 * @author wwh
 */
public class BaseClient {

    private static final Logger logger = LoggerFactory.getLogger(BaseClient.class);

    private ClientManager clientManager;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private Channel channel;

    /**
     * 服务端返回的token
     */
    private String token;

    private Object lock = new Object();

    public BaseClient(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void shutdown() {
        synchronized (lock) {
            lock.notifyAll();
        }

        if (!isRunning()) {
            logger.warn("主服务没有启动");
            return;
        }
        if (channel != null) {
            channel.close();
        }
    }

    public void start(EventLoopGroup workerGroup) throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("客户端正在运行！");
            return;
        }
        ServerConf serverConf = clientManager.getConfig().getServerConf();

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
                clientManager.shutdownNotify();
            }
        });
    }

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
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        // 通知
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public ClientConfig getConfig() {
        return clientManager.getConfig();
    }
}
