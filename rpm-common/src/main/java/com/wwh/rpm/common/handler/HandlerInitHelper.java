package com.wwh.rpm.common.handler;

import org.apache.commons.lang3.StringUtils;

import com.wwh.rpm.common.config.pojo.Arguments;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * handler初始化助手
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class HandlerInitHelper {

    /**
     * 打印日志
     * 
     * @param pipeline
     * @param arguments
     */
    public static void initNettyLoggingHandler(ChannelPipeline pipeline, Arguments arguments) {
        if (arguments == null) {
            return;
        }
        if (arguments.isEnableNettyLog()) {
            LogLevel logLevel = LogLevel.INFO;
            String levelStr = arguments.getNettyLogLevel();
            if (StringUtils.isNotBlank(levelStr)) {
                for (LogLevel l : LogLevel.values()) {
                    if (l.toString().equalsIgnoreCase(levelStr)) {
                        logLevel = l;
                        break;
                    }
                }
            }

            pipeline.addLast(new LoggingHandler(logLevel));
        }
    }
}
