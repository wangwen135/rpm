package com.wwh.rpm.common;

/**
 * 常量
 * 
 * @author wangw
 *
 */
public class Constants {

    /**
     * 服务端默认超过200秒没有收到心跳则关闭连接
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 200;

    /**
     * 客户端默认发送心跳的频率
     */
    public static final int DEFAULT_HEARTBEAT = 10;

    //-----------------------------------
    
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
