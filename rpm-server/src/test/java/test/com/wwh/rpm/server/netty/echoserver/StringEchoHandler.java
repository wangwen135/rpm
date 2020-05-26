package test.com.wwh.rpm.server.netty.echoserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class StringEchoHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(StringEchoHandler.class);

    public StringEchoHandler(){
        // 增加@Sharable 注解则 只会实例化一次，则不能使用有状态的局部
        // ？？ 这个还是有问题啊，每次都会触发
        System.out.println("调用 StringEchoHandler 构造方法");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        logger.info("收到客户端的消息：{}", message);

        if ("quit".equalsIgnoreCase(message)) {
            System.out.println("关闭客户端！");
            ctx.close();
        } else {
            // 模拟同步的耗时任务
            Thread.sleep(5000);
            // 返回
            ctx.writeAndFlush("#！ 你发送的消息是：" + message);
        }

    }
}
