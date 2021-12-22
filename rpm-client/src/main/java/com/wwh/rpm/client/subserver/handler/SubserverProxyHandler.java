package com.wwh.rpm.client.subserver.handler;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ForwardOverServer;
import com.wwh.rpm.client.connection.ConnectionProvider;
import com.wwh.rpm.client.subserver.Subserver;
import com.wwh.rpm.common.handler.TransmissionHandler;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 转发
 * 
 * @author wangwh
 * @date 2021-1-4
 */
public class SubserverProxyHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SubserverProxyHandler.class);

    private Subserver subserver;

    private Channel toServerChannel;

    public SubserverProxyHandler(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel inboundChannel = ctx.channel();
        logger.debug("子服务收到新连接：{} 开始建立到服务器链路", inboundChannel);

        ConnectionProvider cp = subserver.getConnectionProvider();

        String host = subserver.getForwardConfig().getForwardHost();
        int port = subserver.getForwardConfig().getForwardPort();
        ForwardCommandPacket fcp = new ForwardCommandPacket();
        fcp.setHost(host);
        fcp.setPort(port);
        // 增加随机数消除特征
        fcp.setNonce(new Random().nextInt());

        toServerChannel = cp.getCleanConnection2ServerSync(fcp);

        // 将两个通道关联起来
        inboundChannel.pipeline().addLast(new TransmissionHandler(toServerChannel));
        toServerChannel.pipeline().addLast(new TransmissionHandler(inboundChannel));
        inboundChannel.read();
        toServerChannel.read();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ForwardOverServer forwardConfig = subserver.getForwardConfig();
        logger.error("子服务【listen {}:{}】转发异常", forwardConfig.getListenHost(), forwardConfig.getListenPort(), cause);
        ctx.close();
        if (toServerChannel != null) {
            toServerChannel.close();
        }
    }
}
