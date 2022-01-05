package com.wwh.rpm.common.config.pojo;

import com.wwh.rpm.common.exception.ConfigException;

/**
 * 配置文件抽象类
 * 
 * @author wangwh
 * @date 2021-12-31
 */
public abstract class AbstractConfig {

    public abstract String toPrettyString();

    public abstract void check() throws ConfigException;
}
