package com.wwh.rpm.client.subconnection.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.pool.BufferManager;
import com.wwh.rpm.client.pool.RpmConnection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 数据传输处理
 * 
 * @author wangwh
 * @date 2022-2-9
 */
public class DataTransmissionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DataTransmissionHandler.class);

    /**
     * 连接的唯一标识
     */
    private Long id;

    // 要写入到一个固定的连接中
    private RpmConnection rpmConnection;

    private BufferManager bufferManager;

    public DataTransmissionHandler() {
        //连接池 和 缓冲管理器 应该在？ClientManager
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 向缓冲管理器注册
        bufferManager.registerSubChannel(id, ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;

        // 写入到连接中
        rpmConnection.writeData2Server(id, data);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // 发送关闭的包
        rpmConnection.closeRemoteSubChannel(id);

        // 向缓冲管理器反注册
        bufferManager.unregister(id);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("连接【{}】数据传输处理异常", id, cause);
        ctx.close();

    }

}
