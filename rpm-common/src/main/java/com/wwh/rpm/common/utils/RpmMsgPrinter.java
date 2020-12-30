package com.wwh.rpm.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpmMsgPrinter {

    // 单独的日志级别
    private static final Logger rpmMsgLog = LoggerFactory.getLogger("rpm.msg");

    public static void printMsg(String msg) {
        rpmMsgLog.info(msg);
    }

    public static void printMsg(String format, Object... arguments) {
        rpmMsgLog.info(format, arguments);
    }

}
