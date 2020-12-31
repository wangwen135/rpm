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

    /////////////////////////////////// 不确定是否使用
    public static final AttributeKey<String> ATTR_KEY_CID = AttributeKey.valueOf("cid");
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
