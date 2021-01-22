package com.wwh.rpm.ctrl.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.protocol.packet.AbstractPacket;
import com.wwh.rpm.protocol.packet.control.ShutdownPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 控制客户端
 * 
 * @author wangwh
 * @date 2021-1-22
 */
public class CtrlClient extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CtrlClient.class);

    private Boolean result;

    /**
     * 向端口发送关闭指令并等待响应
     * 
     * @param ctrlPort
     * @return
     * @throws Exception
     */
    public Boolean sendShutdownCommand(int ctrlPort) throws Exception {
        return sendCommandWaitResult(ctrlPort, new ShutdownPacket());
    }

    /**
     * 发送指令并等待结果
     * 
     * @param ctrlPort 目标控制端口
     * @param packet   需要发送的数据包
     * @return
     * @throws Exception
     */
    public Boolean sendCommandWaitResult(int ctrlPort, AbstractPacket packet) throws Exception {
        logger.debug("向控制端口：{} 发送指令包：{}", ctrlPort, packet);

        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new PacketDecoder());
                    p.addLast(new PacketEncoder());
                    // 处理器
                    p.addLast(CtrlClient.this);
                }
            });

            Channel channel = b.connect("127.0.0.1", ctrlPort).sync().channel();
            // 发送数据包
            channel.writeAndFlush(packet);
            // 等待关闭
            channel.closeFuture().sync();

            return result;
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("接收到响应指令包：{}", msg);

        if (msg instanceof SuccessPacket) {
            result = true;
        } else {
            logger.warn("暂不支持！msg : {}", msg);
            result = false;
        }

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("发送指令异常", cause);
        ctx.close();
    }
}
