package com.wwh.rpm.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.ctrl.server.CtrlServer;
import com.wwh.rpm.server.config.ServerConfiguration;
import com.wwh.rpm.server.config.pojo.ServerConfig;

/**
 * 服务端启动器
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class ServerStarter {

    private static final Logger logger = LoggerFactory.getLogger(ServerStarter.class);

    private static volatile boolean isClosing = false;

    private static Object lock = new Object();

    public static void main(String[] args) {
        RpmMsgPrinter.printMsg("启动 RPM 服务端...");
        try {
            launch(args);
        } catch (Exception e) {
            logger.error("程序异常！", e);
        }
        RpmMsgPrinter.printMsg("RPM 停止工作！");
    }

    public static void launch(String[] args) {
        // 读取配置文件
        ServerConfig config;
        try {
            config = ServerConfiguration.getEffectiveConfig();
        } catch (ConfigException e) {
            logger.error("配置文件错误：\n{}", e.getMessage());
            return;
        }

        ServerManager sm = new ServerManager(config);
        CtrlServer cs = new CtrlServer(sm);
        try {
            // 启动服务
            sm.startServer();

            // 启动控制服务
            cs.start(config.getCtrlPort());

            // 注册关闭钩子
            addShutdownHook(sm);

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
            logger.error("启动服务异常", e1);
        } finally {
            RpmMsgPrinter.printMsg("关闭服务...");
            // 关闭
            sm.shutdownServer();
            cs.shutdown();
        }
    }

    private static void addShutdownHook(ServerManager sm) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!isClosing) {
                isClosing = true;
                logger.warn("shutdown hook exec");
            }
            synchronized (lock) {
                lock.notifyAll();
            }
        }));
    }

    public static void shutdownNotify() {
        synchronized (lock) {
            isClosing = true;
            lock.notifyAll();
        }
    }
}
