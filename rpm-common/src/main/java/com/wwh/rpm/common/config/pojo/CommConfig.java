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

    /**
     * 加密方式
     */
    private EncryptTypeEnum encryptType;

    /**
     * 是否启用压缩
     */
    private Boolean enableCompression;

    /**
     * 要是级别
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

}
