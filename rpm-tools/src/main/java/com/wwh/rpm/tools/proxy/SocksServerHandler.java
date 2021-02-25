package com.wwh.rpm.tools.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandType;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;

@ChannelHandler.Sharable
public final class SocksServerHandler extends SimpleChannelInboundHandler<SocksMessage> {

    public static final SocksServerHandler INSTANCE = new SocksServerHandler();

    /**
     * 没有必要做认证
     */
    private static final boolean auth = false;

    private SocksServerHandler() {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SocksMessage socksRequest) throws Exception {
        switch (socksRequest.version()) {
        case SOCKS4a:
            Socks4CommandRequest socksV4CmdRequest = (Socks4CommandRequest) socksRequest;
            if (socksV4CmdRequest.type() == Socks4CommandType.CONNECT) {
                ctx.pipeline().addLast(new SocksServerConnectHandler());
                ctx.fireChannelRead(socksRequest);
                ctx.pipeline().remove(this);
            } else {
                ctx.close();
            }
            break;
        case SOCKS5:
            if (socksRequest instanceof Socks5InitialRequest) {

                if (auth) {
                    // 用户名密码认证
                    ctx.pipeline().addFirst(new Socks5PasswordAuthRequestDecoder());
                    ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.PASSWORD));
                } else {
                    // 不需要认证的
                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
                }
            } else if (socksRequest instanceof Socks5PasswordAuthRequest) {
                if (auth) {
                    // 检查用户名和密码
                    Socks5PasswordAuthRequest request = (Socks5PasswordAuthRequest) socksRequest;
                    System.out.println("用户名： " + request.username());
                    System.out.println("密码： " + request.password());
                }

                ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                ctx.write(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));
            } else if (socksRequest instanceof Socks5CommandRequest) {
                Socks5CommandRequest socks5CmdRequest = (Socks5CommandRequest) socksRequest;
                if (socks5CmdRequest.type() == Socks5CommandType.CONNECT) {
                    ctx.pipeline().addLast(new SocksServerConnectHandler());
                    ctx.fireChannelRead(socksRequest);
                    ctx.pipeline().remove(this);
                } else {
                    ctx.close();
                }
            } else {
                ctx.close();
            }
            break;
        case UNKNOWN:
            ctx.close();
            break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        throwable.printStackTrace();
        SocksServerUtils.closeOnFlush(ctx.channel());
    }

}