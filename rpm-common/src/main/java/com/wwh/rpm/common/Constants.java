package com.wwh.rpm.common;

import java.nio.charset.Charset;

import io.netty.util.AttributeKey;

/**
 * 常量
 * 
 * @author wangw
 *
 */
public class Constants {

    /**
     * 默认编码
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 注册超时时间，10秒内未完成注册则关闭连接
     */
    public static final int DEFAULT_REGIST_TIMEOUT = 10;

    /**
     * 指定时间内没有收到心跳则关闭连接（秒）
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 100;

    /**
     * 客户端默认发送心跳的频率（秒）
     */
    public static final int DEFAULT_HEARTBEAT = 30;

    /**
     * 客户端等待Token返回的超时时间
     */
    public static final int CLIENT_WAIT_TOKEN_TIMEOUT = 5000;

    /**
     * 获取客户端转发通道超时时间
     */
    public static final int ACQUIRE_CLIENT_FORWARD_CHANNEL_TIMEOUT = 10000;

    /**
     * 异步方式（回调函数）获取客户端通道超时时间
     */
    public static final int ASYNC_ACQUIRE_CLIENT_FORWARD_CHANNEL_TIMEOUT = 20000;

    /**
     * channel 属性键值 用于存储 cid
     */
    public static final AttributeKey<String> ATTR_KEY_CID = AttributeKey.valueOf("cid");
    /**
     * channel 属性键值 用于存储 token
     */
    public static final AttributeKey<String> ATTR_KEY_TOKEN = AttributeKey.valueOf("token");

    /**
     * 日志打印处理名称
     */
    public static final String LOGGER_HANDLER_NAME = "logger";

    /**
     * 编码器名称
     */
    public static final String ENCODE_HANDLER_NAME = "encoder";
    /**
     * 解码器名称
     */
    public static final String DECODE_HANDLER_NAME = "decoder";

    /**
     * 指令处理器名称
     */
    public static final String COMMAND_HANDLER_NAME = "command";

    /**
     * 默认启用压缩
     */
    public static final boolean DEFAULT_ENABLE_COMPRESSION = true;

    /**
     * 默认压缩级别
     */
    public static final int DEFAULT_COMPRESSION_LEVEL = 6;

    /**
     * 最小压缩级别
     */
    public static final int COMPRESSION_LEVEL_MIN = 0;

    /**
     * 最大压缩级别
     */
    public static final int COMPRESSION_LEVEL_MAX = 9;

    /**
     * 默认连接池大小
     */
    public static final int DEFAULT_POOL_SIZE = 6;

    /**
     * 最大连接池大小
     */
    public static final int MAX_POOL_SIZE = 30;
    // -----------------------------------

    public static final int min_workgroup_size = 8;

    public static final int DEFAULT_WEIGHT = 100;
    public static final int DEFAULT_THREADS = 200;

    public static final boolean DEFAULT_KEEP_ALIVE = true;

    public static final int DEFAULT_QUEUES = 0;

    public static final int DEFAULT_ALIVE = 60 * 1000;

    public static final int DEFAULT_CONNECTIONS = 0;

    public static final int DEFAULT_ACCEPTS = 0;

    public static final int DEFAULT_TIMEOUT = 1000;

    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
}
