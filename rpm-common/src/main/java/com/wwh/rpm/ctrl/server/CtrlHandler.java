package com.wwh.rpm.ctrl.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.ctrl.Closeer;
import com.wwh.rpm.protocol.packet.control.ShutdownPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;

public class CtrlHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CtrlHandler.class);

    private Closeer closeer;

    public CtrlHandler(Closeer closeer) {
        this.closeer = closeer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("处理控制指令包：{}", msg.getClass());

        if (msg instanceof ShutdownPacket) {
            ShutdownPacket sp = (ShutdownPacket) msg;

            closeHandle(sp, ctx.channel());
        } else {
            // 其他指令
            throw new RPMException("暂不支持！msg class : " + msg.getClass());
        }
    }

    /**
     * 处理关闭指令
     * 
     * @param sp
     */
    private void closeHandle(ShutdownPacket sp, Channel channel) {
        logger.warn("接收到关闭指令！结束程序...");
        // 响应成功包
        channel.writeAndFlush(new SuccessPacket());
        // 端口连接
        channel.close();
        // 结束关联程序
        closeer.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CorruptedFrameException) {
            logger.warn("{}", cause.getMessage());
        } else {
            logger.warn("指令处理异常", cause);
        }
        ctx.close();
    }
}
