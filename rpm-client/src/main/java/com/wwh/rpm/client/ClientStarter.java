package com.wwh.rpm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.ClinetConfiguration;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.ctrl.server.CtrlServer;

/**
 * 客户端启动器
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class ClientStarter {

    private static final Logger logger = LoggerFactory.getLogger(ClientStarter.class);

    private static volatile boolean isClosing = false;

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
            config = ClinetConfiguration.getEffectiveConfig();
        } catch (ConfigException e) {
            logger.error("配置文件错误：\n{}", e.getMessage());
            return;
        }

        ClientManager sm = new ClientManager(config);
        CtrlServer cs = new CtrlServer(sm);
        try {
            // 启动客户端
            sm.startClient();

            // 启动控制服务
            cs.start(config.getCtrlPort());

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

        } catch (Exception e1) {
            logger.error("启动客户端异常", e1);
        } finally {
            RpmMsgPrinter.printMsg("关闭客户端...");
            // 关闭
            sm.shutdownClient();
            cs.shutdown();
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!isClosing) {
                isClosing = true;
                logger.warn("shutdown hook exec");
            }
            shutdownNotify();
        }));
    }

    public static void shutdownNotify() {
        synchronized (lock) {
            isClosing = true;
            lock.notifyAll();
        }
    }
}
