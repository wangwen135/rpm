package com.wwh.rpm.common.config.pojo;

import com.wwh.rpm.common.exception.ConfigException;

public class Arguments extends AbstractConfig {

    private boolean enableNettyLog;
    private String nettyLogLevel;

    public boolean isEnableNettyLog() {
        return enableNettyLog;
    }

    public void setEnableNettyLog(boolean enableNettyLog) {
        this.enableNettyLog = enableNettyLog;
    }

    public String getNettyLogLevel() {
        return nettyLogLevel;
    }

    public void setNettyLogLevel(String nettyLogLevel) {
        this.nettyLogLevel = nettyLogLevel;
    }

    @Override
    public String toString() {
        return "Arguments [enableNettyLog=" + enableNettyLog + ", nettyLogLevel=" + nettyLogLevel + "]";
    }

    @Override
    public String toPrettyString() {
        StringBuffer sbuf = new StringBuffer();
        if (enableNettyLog) {
            sbuf.append("\n#其他配置：\n");
            sbuf.append("  enableNettyLog = ").append(enableNettyLog).append("\n");
            sbuf.append("  nettyLogLevel = ").append(nettyLogLevel).append("\n");
        }
        return sbuf.toString();
    }

    @Override
    public void check() throws ConfigException {

    }
}
