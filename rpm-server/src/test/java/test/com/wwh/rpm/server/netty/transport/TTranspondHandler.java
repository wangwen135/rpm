package test.com.wwh.rpm.server.netty.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class TTranspondHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);

		// ChannelDuplexHandler

		// ProxyHandler 先看一下这个

		ChannelPipeline pipeline = ctx.channel().pipeline();

		// 将收到的消息转发到另外的通道

	}
}
