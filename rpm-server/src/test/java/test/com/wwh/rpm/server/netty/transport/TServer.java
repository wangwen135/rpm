package test.com.wwh.rpm.server.netty.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import test.com.wwh.rpm.server.netty.echoserver.StringEchoHandler;

public class TServer {

	public static final int PORT = 18899;

	public static void main(String[] args) throws InterruptedException {

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)

					.channel(NioServerSocketChannel.class)

					.option(ChannelOption.SO_BACKLOG, 100)

					.handler(new LoggingHandler(LogLevel.INFO))

					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();

							// 第一步应该是鉴权，配置转发参数

							p.addLast(new StringEchoHandler());

						}
					});

			// Start the server.
			ChannelFuture f = b.bind(PORT).sync();

			System.out.println("服务已经启动！");
			System.out.println("绑定端口：" + PORT);

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
