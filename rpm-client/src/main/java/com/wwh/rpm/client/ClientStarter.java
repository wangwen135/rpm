package com.wwh.rpm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.ClinetConfiguration;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;

/**
 * 客户端启动器
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class ClientStarter {

    private static final Logger logger = LoggerFactory.getLogger(ClientStarter.class);

    private static Object lock = new Object();

    public static void main(String[] args) {
        RpmMsgPrinter.printMsg("启动 RPM 客户端...");
        try {
            launch(args);
        } catch (Exception e) {
            logger.error("程序异常！", e);
        }
        RpmMsgPrinter.printMsg("RPM 停止工作！");
    }

    public static void launch(String[] args) {
        // 读取配置文件
        ClientConfig config;
        try {
            config = ClinetConfiguration.getClientConfig();
        } catch (ConfigException e) {
            logger.error("配置文件错误：\n{}", e.getMessage());
            return;
        }

        ClientManager sm = new ClientManager(config);

        try {
            // 启动客户端
            sm.startClient();

            // 注册关闭钩子
            addShutdownHook();

            // 等待关闭
            synchronized (lock) {
                try {
                    logger.debug("等待关闭程序的信号...");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.warn("关闭客户端...");

        } catch (Exception e1) {
            logger.error("启动客户端异常", e1);
        } finally {
            // 关闭
            sm.shutdownClient();
        }

    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("shutdown hook exec");
            synchronized (lock) {
                lock.notifyAll();
            }
        }));
    }

    public static void shutdownNotify() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
