package com.wwh.rpm.server.master.handler;

import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ForwardCommandHandler extends SimpleChannelInboundHandler<ForwardCommandPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ForwardCommandPacket msg) throws Exception {

        msg.getHost();
        msg.getPort();
        
        
    }

}
