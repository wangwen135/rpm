package test.com.wwh.rpm.server.netty.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class TCheckHandler extends ChannelInboundHandlerAdapter {

	//使用局部变量控制状态
	private boolean checked = false;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		
		System.out.println("进行鉴权，参数解析等");
		//读取到消息
		//解析消息
		//从魔法数字开始，后面直接跟上json算了
		
		//鉴权失败，直接关闭这个链接
		
		//json解析完成，验证通过之后
		
		ChannelPipeline pipeline = ctx.channel().pipeline();
	}
}
