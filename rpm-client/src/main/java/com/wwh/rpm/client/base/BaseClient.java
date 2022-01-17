package com.wwh.rpm.client.base;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.ClientStarter;
import com.wwh.rpm.client.base.handler.BaseHandlerInitializer;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConf;
import com.wwh.rpm.client.connection.ConnectionProvider;
import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.config.pojo.CommConfig;
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
 * <pre>
 * 主客户端
 * 1. 注册
 * 2. 响应服务端的指令
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

    /**
     * 通讯配置
     */
    private CommConfig commConfig;

    private Object lock = new Object();

    private EventLoopGroup workerGroup;

    public BaseClient(ClientManager clientManager) {
        this.clientManager = clientManager;
        workerGroup = new NioEventLoopGroup();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * 关闭
     */
    public void shutdown() {
        synchronized (lock) {
            lock.notifyAll();
        }
        logger.debug("主服务关闭线程池");
        workerGroup.shutdownGracefully();

        if (!isRunning()) {
            logger.warn("主服务没有启动");
            return;
        }
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * 启动
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("客户端正在运行！");
            return;
        }
        ServerConf serverConf = clientManager.getConfig().getServerConf();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup).channel(NioSocketChannel.class);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);
        
        b.handler(new BaseHandlerInitializer(this));

        ChannelFuture f = b.connect(serverConf.getHost(), serverConf.getPort()).sync();
        channel = f.channel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.warn("客户端【主连接】被关闭！");
                synchronized (lock) {
                    lock.notifyAll();
                }
                ClientStarter.shutdownNotify();
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
                lock.wait(Constants.CLIENT_WAIT_TOKEN_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    public ClientConfig getConfig() {
        return clientManager.getConfig();
    }

    public ConnectionProvider getConnectionProvider() {
        return clientManager.getConnectionProvider();
    }

    public CommConfig getCommConfig() {
        return commConfig;
    }

    public void setCommConfig(CommConfig commConfig) {
        this.commConfig = commConfig;
    }

}
