package com.wwh.rpm.protocol;

/**
 * <pre>
 * 协议常量
 *
 * 协议暂定
 * 【魔法数字 1】【类型 1】【长度 4】【对象序列化字节】
 * </pre>
 *
 * @author wwh
 *
 */
public class ProtocolConstants {

    /**
     * 魔法数字
     */
    public static final byte MAGIC_NUMBER = (byte) 0x88;

    /**
     * 心跳包
     */
    public static final byte TYPE_HEARTBEAT = 3;

    /**
     * 结果包
     */
    public static final byte TYPE_RESULT = 7;

    /**
     * 成功
     */
    public static final byte TYPE_SUCCESS = 8;

    /**
     * 失败
     */
    public static final byte TYPE_FAIL = 9;

    /**
     * 注册包
     */
    public static final byte TYPE_REGIST = 10;

    /**
     * 认证包
     */
    public static final byte TYPE_AUTH = 11;

    /**
     * token
     */
    public static final byte TYPE_TOKEN = 12;

    /**
     * 指令
     */
    public static final byte TYPE_COMMAND = 30;

    /**
     * 转发指令
     */
    public static final byte TYPE_FORWARD_COMMAND = 31;

    /**
     * 转发指令执行结果
     */
    public static final byte TYPE_FORWARD_COMMAND_RESULT = 32;

    /**
     * 传输数据
     */
    public static final byte TYPE_TRANSPORT = 40;

    /**
     * 关闭指令
     */
    public static final byte TYPE_SHUTDOWN = 100;


    /**
     * 授权随机数最小值
     */
    public static final int AUTH_RANDOM_NUMBER_MIN = 1111111;
    /**
     * 授权随机数最大值
     */
    public static final int AUTH_RANDOM_NUMBER_MAX = 9999999;
    /**
     * 授权随机数增量
     */
    public static final int AUTH_RANDOM_NUMBER_INCREMENT = 123456;
}
