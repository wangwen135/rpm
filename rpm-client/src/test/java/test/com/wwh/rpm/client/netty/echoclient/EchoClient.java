package test.com.wwh.rpm.client.netty.echoclient;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;

/*
  * Copyright 2012 The Netty Project
  *
  * The Netty Project licenses this file to you under the Apache License,
  * version 2.0 (the "License"); you may not use this file except in compliance
  * with the License. You may obtain a copy of the License at:
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Sends one message when a connection is open and echoes back any received data to the server. Simply put, the echo client initiates the ping-pong traffic between the echo client and server by sending the first message
 * to the server.
 */
public final class EchoClient {
    private static final boolean compression = false;
    private static final boolean simpleEncryption = true;

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8800"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // netty 日志记录器
                            p.addLast(new LoggingHandler(LogLevel.INFO));

                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }

                            if (compression) {
                                // 压缩
                                p.addLast(new JdkZlibEncoder());
                                p.addLast(new JdkZlibDecoder());
                            }
                            if (simpleEncryption) {
                                // 简单加密
                                p.addLast(new SimpleEncryptionEncoder("aaa"));
                                p.addLast(new SimpleEncryptionDecoder("aaa"));
                            }

                            // 编码解码器
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());

                            // 心跳
                            p.addLast(new IdleStateHandler(0, 50, 0, TimeUnit.SECONDS));
                            p.addLast(new HeartbeatHandler());

                            p.addLast(new StringPrintHandler());
                            // p.addLast(new EchoClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            System.out.println("启动成功！");
            System.out.println("在控制输入内容");
            System.out.println("exit 客户端退出");
            System.out.println("quit 服务端退出");

            Scanner scanner = new Scanner(System.in);

            String line = null;
            Channel channel = f.channel();
            while (true) {
                line = scanner.nextLine();

                if ("exit".equalsIgnoreCase(line)) {
                    System.out.println("客户端退出！");
                    scanner.close();
                    channel.close().sync();
                    return;
                } else {
                    if (channel.isActive()) {
                        channel.writeAndFlush(line);
                    } else {
                        System.out.println("连接关闭了，退出");
                        scanner.close();
                        return;
                    }
                }
            }

            // Wait until the connection is closed.
            // f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
