package com.wwh.rpm.common.enums;

public enum EncryptTypeEnum {
    /**
     * 不加密
     */
    NONE(0),
    /**
     * 简单异或
     */
    SIMPLE(1);

    private int code;

    private EncryptTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EncryptTypeEnum getEnumByCode(int code) {
        for (EncryptTypeEnum e : EncryptTypeEnum.values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
