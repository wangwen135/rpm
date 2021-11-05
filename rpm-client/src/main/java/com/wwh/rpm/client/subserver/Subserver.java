package com.wwh.rpm.client.subserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ForwardOverServer;
import com.wwh.rpm.client.connection.ConnectionProvider;
import com.wwh.rpm.client.subserver.handler.SubserverHandlerInitializer;
import com.wwh.rpm.common.utils.LogUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 客户端子服务
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class Subserver {

    private static final Logger logger = LoggerFactory.getLogger(Subserver.class);

    private SubserverManager subserverManager;
    private ForwardOverServer forwardConfig;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public Subserver(SubserverManager subserverManager, ForwardOverServer forwardConfig) {
        this.subserverManager = subserverManager;
        this.forwardConfig = forwardConfig;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
    }

    /**
     * 启动
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);

        // 不会自动读取数据
        b.childOption(ChannelOption.AUTO_READ, false);
        b.childOption(ChannelOption.TCP_NODELAY, true);

        b.childHandler(new SubserverHandlerInitializer(this));

        channel = b.bind(forwardConfig.getListenHost(), forwardConfig.getListenPort()).sync().channel();

        LogUtil.msgLog.info("子主服务启动在 {}:{}", forwardConfig.getListenHost(), forwardConfig.getListenPort());

    }

    /**
     * 关闭
     */
    public void shutdown() {
        try {
            logger.debug("子服务关闭线程池...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            if (channel != null && channel.isActive()) {
                channel.close().sync();
            }

        } catch (InterruptedException e) {
            logger.error("关闭子服务异常", e);
        }
    }

    @Override
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("【子服务】本地监听：\n");
        sbuf.append(forwardConfig.getListenHost());
        sbuf.append(":");
        sbuf.append(forwardConfig.getListenPort());
        sbuf.append("\n经由服务端转发至：\n");
        sbuf.append(forwardConfig.getForwardHost());
        sbuf.append(":");
        sbuf.append(forwardConfig.getForwardHost());
        return sbuf.toString();
    }

    public ForwardOverServer getForwardConfig() {
        return forwardConfig;
    }

    public ClientConfig getConfig() {
        return subserverManager.getConfig();
    }

    public String getToken() {
        return subserverManager.getToken();
    }

    public ConnectionProvider getConnectionProvider() {
        return subserverManager.getConnectionProvider();
    }
}
