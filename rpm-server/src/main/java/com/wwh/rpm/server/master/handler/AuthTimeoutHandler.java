package com.wwh.rpm.server.master.handler;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.Constants;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

/**
 * 认证超时处理
 * 
 * @author wangwh
 * @date 2021-1-5
 */
public class AuthTimeoutHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTimeoutHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.debug("新进连接，启动定时器，用于关闭认证超时的连接");
        ctx.executor().schedule(new RegistTimeOutTask(ctx), Constants.DEFAULT_REGIST_TIMEOUT, TimeUnit.SECONDS);
    }
}

class RegistTimeOutTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RegistTimeOutTask.class);

    private final ChannelHandlerContext ctx;

    public RegistTimeOutTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        if (!ctx.channel().isActive()) {
            return;
        }
        // 判断是否认证通过
        Attribute<String> attr = ctx.channel().attr(Constants.ATTR_KEY_TOKEN);
        if (StringUtils.isBlank(attr.get())) {
            logger.warn("客户端注册超时，关闭连接：{}", ctx.channel().remoteAddress());
            ctx.close();
        } else {
            logger.debug("连接：{} 已认证通过，不做任何处理", ctx.channel().toString());
        }
    }

}