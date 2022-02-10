package com.wwh.rpm.client.pool.connection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.pool.RpmConnection;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.protocol.packet.transport.ClosePacket;
import com.wwh.rpm.protocol.packet.transport.TransportPacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CommandHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private RpmConnection rpmConnection;

    public CommandHandler(RpmConnection rpmConnection) {
        this.rpmConnection = rpmConnection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("处理指令包：{}", msg);

        // 测试指令

        // 结果指令等

        if (msg instanceof TransportPacket) {
            // 处理传输指令
            TransportPacket tp = (TransportPacket) msg;
            transportPacketHandler(ctx, tp);
        } else if (msg instanceof ClosePacket) {
            // 关闭指令
            ClosePacket closePacket = (ClosePacket) msg;
            closePacketHandler(ctx, closePacket);
        } else if (msg instanceof ForwardCommandPacket) {
            // 转发指令
            ForwardCommandPacket fcp = (ForwardCommandPacket) msg;
            forwardCommandHandler(ctx, fcp);
        } else {
            throw new RPMException("暂不支持！msg class : " + msg.getClass());
        }
    }

    private void closePacketHandler(ChannelHandlerContext ctx, ClosePacket closePacket) {
        logger.debug("处理关闭指令：{}", closePacket);
        Long id = closePacket.getId();
        Channel channel = rpmConnection.getBufferManager().unregister(id);
        if (channel != null) {
            channel.close();
        }
    }

    private void transportPacketHandler(ChannelHandlerContext ctx, TransportPacket transportPacket) {
        logger.debug("读取数据 id={} dataLength={}", transportPacket.getId(), transportPacket.getData().length);
        rpmConnection.getBufferManager().putBuffer(transportPacket.getId(), transportPacket.getData());
    }

    private void forwardCommandHandler(ChannelHandlerContext ctx, ForwardCommandPacket forwardCommand) {
        logger.debug("处理转发指令:{}", forwardCommand);
        rpmConnection.getSubconnectionProvider().connect2Target(forwardCommand);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("指令处理异常", cause);
        ctx.close();
    }
}
