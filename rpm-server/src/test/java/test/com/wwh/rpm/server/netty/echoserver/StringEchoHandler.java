package test.com.wwh.rpm.server.netty.echoserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

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
            // 返回
            ctx.writeAndFlush("\n## you message is :" + message);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.warn("到达指定时间间隔没有收到心跳，关闭连接：{}", ctx.channel().remoteAddress());
                ctx.fireUserEventTriggered(evt);
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
