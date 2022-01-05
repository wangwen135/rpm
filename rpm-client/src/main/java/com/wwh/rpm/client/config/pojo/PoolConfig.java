package com.wwh.rpm.client.config.pojo;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.config.pojo.AbstractConfig;
import com.wwh.rpm.common.exception.ConfigException;

/**
 * 连接池配置
 * 
 * @author wangwh
 * @date 2021-12-30
 */
public class PoolConfig extends AbstractConfig {

    /**
     * 连接池大小
     */
    private Integer poolSize = Constants.DEFAULT_POOL_SIZE;

    public Integer getPoolSize() {
        return poolSize == null ? Constants.DEFAULT_POOL_SIZE : poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public String toString() {
        return "PoolConfig [poolSize=" + poolSize + "]";
    }

    @Override
    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("#连接池配置：\n");
        sbuf.append("  连接池大小 poolSize = ").append(getPoolSize()).append("\n");
        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {
        int poolSize = getPoolSize();
        if (poolSize < 1 || poolSize > Constants.MAX_POOL_SIZE) {
            throw new ConfigException("连接池大小配置错误【pool:poolSize】只能为1 ~ " + Constants.MAX_POOL_SIZE);
        }
    }
}
