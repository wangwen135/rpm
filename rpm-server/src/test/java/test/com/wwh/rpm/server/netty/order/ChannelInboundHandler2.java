package test.com.wwh.rpm.server.netty.order;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class ChannelInboundHandler2 implements ChannelInboundHandler {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - handlerRemoved");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - channelRegistered");
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - channelUnregistered");
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - channelActive");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - channelInactive");
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("【2】in - channelRead");
        ByteBuf in = (ByteBuf) msg;
        try {
            // while (in.isReadable()) {
            // System.out.print((char) in.readByte());
            // System.out.flush();
            // }

            System.out.println(in.toString(io.netty.util.CharsetUtil.UTF_8));
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - channelReadComplete");
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("【2】in - userEventTriggered");
        System.out.println(evt);
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("【2】in - channelWritabilityChanged");
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("【2】in - exceptionCaught");
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }

}
