package com.wwh.rpm.common.config.pojo;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.enums.EncryptTypeEnum;

/**
 * 通讯配置
 * 
 * @author wangwh
 * @date 2021-3-1
 */
public class CommConfig {

    private static final String SPLIT = ":";
    /**
     * 加密方式
     */
    private EncryptTypeEnum encryptType;

    /**
     * 是否启用压缩
     */
    private Boolean enableCompression;

    /**
     * 压缩级别
     */
    private Integer compressionLevel;

    public EncryptTypeEnum getEncryptType() {
        return encryptType == null ? EncryptTypeEnum.NONE : encryptType;
    }

    public void setEncryptType(EncryptTypeEnum encryptType) {
        this.encryptType = encryptType;
    }

    public Boolean getEnableCompression() {
        return enableCompression == null ? Constants.DEFAULT_ENABLE_COMPRESSION : enableCompression;
    }

    public void setEnableCompression(Boolean enableCompression) {
        this.enableCompression = enableCompression;
    }

    public Integer getCompressionLevel() {
        return compressionLevel == null ? Constants.DEFAULT_COMPRESSION_LEVEL : compressionLevel;
    }

    public void setCompressionLevel(Integer compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public String toString() {
        return "CommConfig [encryptType=" + encryptType + ", enableCompression=" + enableCompression
                + ", compressionLevel=" + compressionLevel + "]";
    }

    public String toPrettyString() {
        return "加密方式=" + encryptType + ", 启用压缩=" + enableCompression + ", 压缩级别=" + compressionLevel;
    }

    /**
     * 编码成字符串
     * 
     * @return
     */
    public String code() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(getEncryptType().getCode());
        sbf.append(SPLIT);
        sbf.append(getEnableCompression() ? "T" : "F");
        sbf.append(SPLIT);
        sbf.append(getCompressionLevel());
        return sbf.toString();
    }

    /**
     * 解码成对象
     * 
     * @param str
     * @return
     */
    public static CommConfig decode(String str) {
        CommConfig cc = new CommConfig();
        String[] s = str.split(SPLIT);
        cc.setEncryptType(EncryptTypeEnum.getEnumByCode(Integer.valueOf(s[0])));
        cc.setEnableCompression("T".equals(s[1]));
        cc.setCompressionLevel(Integer.valueOf(s[2]));
        return cc;
    }

}
