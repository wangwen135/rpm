package com.wwh.rpm.protocol;

/**
 * 包类型枚举
 * 
 * @author wwh
 *
 */
public enum PacketTypeEnum {
    AUTH, HEARTBEAT;

    private byte type;
    private String desc;
    
    
}
