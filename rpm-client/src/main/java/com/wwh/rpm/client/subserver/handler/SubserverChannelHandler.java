package com.wwh.rpm.client.subserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ForwardOverServer;
import com.wwh.rpm.client.pool.RpmConnection;
import com.wwh.rpm.client.subserver.Subserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 子服务连接处理
 * 
 * @author wangwh
 * @date 2022-2-10
 */
public class SubserverChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SubserverChannelHandler.class);

    private Subserver subserver;

    private Long id;
    private RpmConnection rpmConnection;

    public SubserverChannelHandler(Subserver subserver) {
        this.subserver = subserver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        id = subserver.getNextId();
        Channel inboundChannel = ctx.channel();
        logger.debug("子服务建立新连接：{} ID为：{}", inboundChannel, id);
        rpmConnection = subserver.getConnectionPool().getConnection();
        logger.debug("上传通道使用rpm连接为：{}", rpmConnection.getConnectionId());
        String host = subserver.getForwardConfig().getForwardHost();
        int port = subserver.getForwardConfig().getForwardPort();
        rpmConnection.writeForwardPacket(id, host, port);

        subserver.getConnectionPool().getBufferManager().registerSubChannel(id, inboundChannel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ReferenceCountUtil.release(buf);
        // byte[] bytes = ByteBufUtil.getBytes(in);

        rpmConnection.writeData2Server(id, bytes);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("子服务连接【[{}】断开", id);
        // 远程关闭
        rpmConnection.closeRemoteSubChannel(id);

        // 清理缓冲区
        subserver.getConnectionPool().getBufferManager().unregister(id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ForwardOverServer forwardConfig = subserver.getForwardConfig();
        logger.error("子服务【listen {}:{}】转发异常", forwardConfig.getListenHost(), forwardConfig.getListenPort(), cause);
        ctx.close();
    }
}
