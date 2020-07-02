package com.wwh.rpm.common.enums;

public enum EncryptTypeEnum {
    SIMPLE(1, "简单异或");

    private int code;
    private String desc;

    private EncryptTypeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据Code获取枚举
     * 
     * @param code
     * @return
     */
    public static EncryptTypeEnum findByCode(int code) {
        for (EncryptTypeEnum et : EncryptTypeEnum.values()) {
            if (et.getCode() == code) {
                return et;
            }
        }
        return null;
    }
}
