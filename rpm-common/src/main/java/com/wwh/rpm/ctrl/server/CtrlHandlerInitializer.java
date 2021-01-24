package com.wwh.rpm.ctrl.server;

import com.wwh.rpm.ctrl.Closeer;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class CtrlHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private Closeer closeer;

    public CtrlHandlerInitializer(Closeer closeer) {
        this.closeer = closeer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        // 先添加编码器
        p.addLast("decoder", new PacketDecoder());
        p.addLast("encoder", new PacketEncoder());

        // 控制命令处理器
        p.addLast(new CtrlHandler(closeer));

    }

}
