package test.com.wwh.rpm.client.netty.echoclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

public class HeartbeatHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    /** 客户端请求的心跳 */
    private static final ByteBuf HEARTBEAT_BYTEBUF = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("heartbeat!", CharsetUtil.UTF_8));

    private long readCount = 0;
    private long writeCount = 0;

    private long heartbeatCount = 0;

    // private static long MAX_HEARTBEAT_COUNT = 10;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        // 心跳请求处理，每间隔一段时间发送一次心跳请求;

        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                // 如果写通道处于空闲状态就发送心跳命令
                heartbeatCount++;

                // ctx.channel().writeAndFlush(HEARTBEAT_BYTEBUF.duplicate());
                ctx.channel().writeAndFlush("heartbeat!");

                logger.info("\n写数据处于空闲状态，发送心跳！\n" + "心跳次数：{} 读数据次数：{}  写数据次数：{}", heartbeatCount, readCount,
                        writeCount);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("连接被关闭");
        System.exit(1);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("read Complete");
        readCount++;
        super.channelReadComplete(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("write");
        writeCount++;
        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {

        System.out.println("flush");
        super.flush(ctx);
    }
}
