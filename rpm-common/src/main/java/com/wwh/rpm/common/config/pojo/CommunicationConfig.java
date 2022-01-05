package com.wwh.rpm.common.config.pojo;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.enums.EncryptTypeEnum;
import com.wwh.rpm.common.exception.ConfigException;

/**
 * 通讯配置
 * 
 * @author wangwh
 * @date 2021-3-1
 */
public class CommunicationConfig extends AbstractConfig {

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

    @Override
    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("\n#通信配置：\n");
        sbuf.append("  加密方式   encryptType = ").append(getEncryptType()).append("\n");
        sbuf.append("  是否压缩   enableCompression = ").append(getEnableCompression()).append("\n");
        sbuf.append("  压缩级别   compressionLevel = ").append(getCompressionLevel()).append("\n");

        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {

        Integer compressionLevel = getCompressionLevel();
        if (compressionLevel < Constants.COMPRESSION_LEVEL_MIN || compressionLevel > Constants.COMPRESSION_LEVEL_MAX) {
            throw new ConfigException("压缩级别【compressionLevel】配置错误，只支持 0-9");
        }

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
    public static CommunicationConfig decode(String str) {
        CommunicationConfig cc = new CommunicationConfig();
        String[] s = str.split(SPLIT);
        cc.setEncryptType(EncryptTypeEnum.getEnumByCode(Integer.valueOf(s[0])));
        cc.setEnableCompression("T".equals(s[1]));
        cc.setCompressionLevel(Integer.valueOf(s[2]));
        return cc;
    }

}
