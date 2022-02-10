package com.wwh.rpm.client.pool.connection.handler;

import static com.wwh.rpm.common.Constants.DEFAULT_HEARTBEAT;
import static com.wwh.rpm.common.Constants.DEFAULT_IDLE_TIMEOUT;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.pool.connection.CommonConnection;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;
import com.wwh.rpm.common.enums.EncryptTypeEnum;
import com.wwh.rpm.common.handler.HandlerInitHelper;
import com.wwh.rpm.protocol.codec.PacketDecoder;
import com.wwh.rpm.protocol.codec.PacketEncoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionDecoder;
import com.wwh.rpm.protocol.security.SimpleEncryptionEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 通信连接初始化
 * 
 * @author wangwh
 * @date 2022-1-5
 */
public class CommonHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(CommonHandlerInitializer.class);

    private CommonConnection commonConnection;

    public CommonHandlerInitializer(CommonConnection commonConnection) {
        this.commonConnection = commonConnection;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        ClientConfig config = commonConnection.getClientConfig();

        // 打印日志
        HandlerInitHelper.initNettyLoggingHandler(pipeline, config.getArguments());

        CommunicationConfig commConfig = config.getCommunication();
        // 加密
        EncryptTypeEnum encryptType = commConfig.getEncryptType();
        if (EncryptTypeEnum.NONE == encryptType) {
            logger.warn("注意：与服务端的通信未加密！");
        } else if (EncryptTypeEnum.SIMPLE == encryptType) {
            String sid = config.getServerConf().getSid();
            // 加密
            pipeline.addFirst(new SimpleEncryptionEncoder(sid));
            pipeline.addFirst(new SimpleEncryptionDecoder(sid));
        } else {
            logger.error("暂时不支持的加密方式：{}", encryptType);
        }
        // 压缩
        boolean compression = commConfig.getEnableCompression();
        // 是否需要进行压缩
        if (compression) {
            int level = commConfig.getCompressionLevel();
            // 压缩
            pipeline.addFirst(new JdkZlibEncoder(level));
            pipeline.addFirst(new JdkZlibDecoder());
        }

        // 先添加编码器
        pipeline.addLast(new PacketDecoder());
        pipeline.addLast(new PacketEncoder());

        // 客户端注册
        pipeline.addLast(new CommonRegistHandler(commonConnection));

        // 心跳处理
        pipeline.addLast(new IdleStateHandler(DEFAULT_IDLE_TIMEOUT, DEFAULT_HEARTBEAT, 0, TimeUnit.SECONDS));
        pipeline.addLast(new ClientHeartbeatHandler());

        // 服务端指令处理
        pipeline.addLast(new CommandHandler(commonConnection));

    }

}
