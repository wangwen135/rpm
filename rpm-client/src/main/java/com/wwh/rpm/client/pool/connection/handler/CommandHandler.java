package com.wwh.rpm.client.pool.connection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.connection.ConnectionProvider;
import com.wwh.rpm.client.connection.FetchChannelWarp;
import com.wwh.rpm.client.connection.event.RegistSuccessEvent;
import com.wwh.rpm.client.connection.handler.SendCommandWaitResultHandler;
import com.wwh.rpm.client.pool.RpmConnection;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.common.handler.TransmissionHandler;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.protocol.packet.command.ForwardResultPacket;
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

        if (msg instanceof TransportPacket) {
            // 处理传输指令

            TransportPacket tp = (TransportPacket) msg;
            transportPacketHandler(ctx, tp);

        } else if (msg instanceof ClosePacket) {
            // 关闭指令

        } else if (msg instanceof ForwardCommandPacket) {
            ForwardCommandPacket fcp = (ForwardCommandPacket) msg;
            try {
                forwardCommandHandler(ctx, fcp);
            } catch (Exception e1) {
                logger.warn("转发指令：{} 处理异常！", fcp, e1);
            }
        } else {
            throw new RPMException("暂不支持！msg class : " + msg.getClass());
        }
    }

    private void closePacketHandler(ChannelHandlerContext ctx, ClosePacket closePacket) {

    }

    /**
     * 处理传输指令
     * 
     * @param ctx
     * @param transportPacket
     */
    private void transportPacketHandler(ChannelHandlerContext ctx, TransportPacket transportPacket) {
        rpmConnection.getBufferManager().putBuffer(transportPacket.getId(), transportPacket.getData());
    }

    private void forwardCommandHandler(ChannelHandlerContext ctx, ForwardCommandPacket forwardCommand) {
        logger.debug("处理转发指令:{}", forwardCommand);

//TODOWWH 考虑阻塞问题，可能要做成异步的

        ForwardResultPacket forwardResultPacket = new ForwardResultPacket(forwardCommand.getId());
        ConnectionProvider connectionProvider = baseClient.getConnectionProvider();

        // 1、分别建立到目标和服务器端的连接
        // 这里异步，为了更快的连接速度
        FetchChannelWarp toTarget = connectionProvider.getConnection2Target(forwardCommand.getHost(),
                forwardCommand.getPort());
        FetchChannelWarp toServer = connectionProvider.getConnection2Server();

        Channel toTargetChannel;
        try {
            System.err.println("线程在等待1  " + Thread.currentThread());
            toTargetChannel = toTarget.getChannelOnce();
            System.err.println("线程等待结束1  " + Thread.currentThread());
        } catch (Exception e) {
            if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
                logger.error("无法连接到目标：{}", forwardCommand, e);
            } else {
                logger.error("无法连接到目标：{} {}", forwardCommand, e.getMessage());
            }

            // 通知服务端
            ctx.writeAndFlush(forwardResultPacket);
            // 关闭新开的连接
            toTarget.close();
            toServer.close();
            return;
        }
        Channel toServerChannel;
        try {
            System.err.println("线程在等待2  " + Thread.currentThread());

            toServerChannel = toServer.getChannelOnce();

            System.err.println("线程等待结束2  " + Thread.currentThread());
        } catch (Exception e) {
            logger.error("建立到服务器的链路异常", e);

            // 通知服务端
            ctx.writeAndFlush(forwardResultPacket);
            // 关闭新开的连接
            toTarget.close();
            toServer.close();
            return;
        }

        // 2、上面第1部中到服务端的连接只是注册
        // 响应指令
        FetchChannelWarp toServer2 = new FetchChannelWarp();
        // 到这里才能确定到目标服务器的连接能够建立
        forwardResultPacket.setResult(true);
        SendCommandWaitResultHandler scwrHandler = new SendCommandWaitResultHandler(forwardResultPacket, toServer2);
        toServerChannel.pipeline().addLast(scwrHandler);
        toServerChannel.pipeline().fireUserEventTriggered(new RegistSuccessEvent());
        try {
            System.err.println("线程在等待3  " + Thread.currentThread());

            toServerChannel = toServer2.getChannelOnce();

            System.err.println("线程等待结束3  " + Thread.currentThread());
        } catch (Exception e) {
            logger.error("响应转发指令异常", e);
            // 通知服务端
            forwardResultPacket.setResult(false);
            ctx.writeAndFlush(forwardResultPacket);
            // 关闭新开的连接
            toTarget.close();
            toServer2.close();
            return;
        }

        // 3、将两个通道关联起来
        toTargetChannel.pipeline().addLast(new TransmissionHandler(toServerChannel));
        toServerChannel.pipeline().addLast(new TransmissionHandler(toTargetChannel));
        toTargetChannel.read();
        toServerChannel.read();

        System.err.println("将两个通道关联起来...");
        toTargetChannel.pipeline().read();
        toTargetChannel.pipeline().read();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("指令处理异常", cause);
        ctx.close();
    }
}
