package com.wwh.rpm.server.subserver;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.connection.SimpleChannelWarp;
import com.wwh.rpm.common.utils.LogUtil;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.subserver.handler.SubserverHandlerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端的子服务
 * 
 * @author wangwh
 * @date 2021-1-26
 */
public class Subserver {

    private static final Logger logger = LoggerFactory.getLogger(Subserver.class);

    private SubserverManager subserverManager;
    private ForwardOverClient forwardConfig;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public Subserver(SubserverManager subserverManager, ForwardOverClient forwardOverClient) {
        this.subserverManager = subserverManager;
        this.forwardConfig = forwardOverClient;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
    }

    public SubserverManager getSubserverManager() {
        return subserverManager;
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
        sbuf.append("\n经由客户端【").append(forwardConfig.getClientId()).append("】转发至：\n");
        sbuf.append(forwardConfig.getForwardHost());
        sbuf.append(":");
        sbuf.append(forwardConfig.getForwardHost());
        return sbuf.toString();
    }

    public ServerConfig getConfig() {
        return subserverManager.getConfig();
    }

    public ForwardOverClient getForwardConfig() {
        return forwardConfig;
    }

    /**
     * 异步获取客户端连接
     * 
     * @param callback 回调方法（成功、失败、超时）
     */
    public void acquireClientForwardChannel(Consumer<SimpleChannelWarp> callback) {
        subserverManager.acquireClientForwardChannel(this, callback);
    }
}
