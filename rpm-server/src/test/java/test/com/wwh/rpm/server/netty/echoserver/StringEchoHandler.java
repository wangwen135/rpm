package test.com.wwh.rpm.server.netty.echoserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class StringEchoHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger logger = LoggerFactory.getLogger(StringEchoHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
		logger.info("收到客户端的消息：{}", message);
		System.out.println(Thread.currentThread());

		ChannelPipeline p = ctx.pipeline();
		System.out.println(p);
		System.out.println("ChannelPipeline = " + p.hashCode());

		if ("quit".equalsIgnoreCase(message)) {
			System.out.println("关闭客户端！");
			ctx.close();
		} else {
			// 模拟同步的耗时任务
			Thread.sleep(1000);
			// 返回
			ctx.writeAndFlush("#！ 你发送的消息是：" + message);
		}

	}
}
