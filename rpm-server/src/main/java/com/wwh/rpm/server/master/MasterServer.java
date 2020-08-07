package com.wwh.rpm.server.master;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.config.server.ServerConfig;
import com.wwh.rpm.server.master.handler.AuthorizationHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class MasterServer {

	private static final Logger logger = LoggerFactory.getLogger(MasterServer.class);

	private ServerConfig config;

	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel channel;
	// 客户端列表

	public MasterServer(ServerConfig config) {
		this.config = config;
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public void shutdown() throws Exception {
		if (!isRunning()) {
			logger.warn("主服务没有启动");
			return;
		}
		if (channel != null) {
			channel.close();
		}
	}

	public void start() throws Exception {
		if (!isRunning.compareAndSet(false, true)) {
			logger.warn("主服务正在运行");
			return;
		}
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childOption(ChannelOption.AUTO_READ, false);

			b.handler(new LoggingHandler(LogLevel.INFO));

			b.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();

					p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));

					// 授权
					p.addLast(new AuthorizationHandler());

					// 编码解码器
					p.addLast(new StringDecoder());
					p.addLast(new StringEncoder());

				}
			});

			channel = b.bind(config.getHost(), config.getPort()).sync().channel();

			logger.info("主服务启动在 " + config.getHost() + ":" + config.getPort());

		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			throw e;
		}

		channel.closeFuture().addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				// 这里阻塞一下试试

				logger.warn(future.channel().toString() + " 链路关闭");
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
			}
		});

	}

}
