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
     * 服务端默认超过200秒没有收到心跳则关闭连接
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 200;

    /**
     * 客户端默认发送心跳的频率（秒）
     */
    public static final int DEFAULT_HEARTBEAT = 60;

    /**
     * 客户端等待Token返回的超时时间
     */
    public static final int CLIENT_WAIT_TOKEN_TIMEOUT = 5000;

    /**
     * 获取客户端转发通道超时时间
     */
    public static final int ACQUIRE_CLIENT_FORWARD_CHANNEL_TIMEOUT = 6000;

    /**
     * channel 属性键值 用于存储 cid
     */
    public static final AttributeKey<String> ATTR_KEY_CID = AttributeKey.valueOf("cid");
    /**
     * channel 属性键值 用于存储 token
     */
    public static final AttributeKey<String> ATTR_KEY_TOKEN = AttributeKey.valueOf("token");

    // -----------------------------------

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
