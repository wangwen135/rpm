package com.wwh.rpm.common.handler;

import static com.wwh.rpm.common.Constants.LOGGER_HANDLER_NAME;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(HandlerInitHelper.class);

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
            logger.debug("添加LoggingHandler，级别：{}", logLevel);
            pipeline.addLast(LOGGER_HANDLER_NAME, new LoggingHandler(logLevel));
        }
    }
}
