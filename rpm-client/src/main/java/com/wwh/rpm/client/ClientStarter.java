package com.wwh.rpm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.ClinetConfiguration;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.LogUtil;
import com.wwh.rpm.ctrl.Closeer;
import com.wwh.rpm.ctrl.server.CtrlServer;

/**
 * 客户端启动器
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class ClientStarter implements Closeer {

    private static final Logger logger = LoggerFactory.getLogger(ClientStarter.class);

    private static volatile boolean close = false;

    /**
     * 可能主服务刚刚启动成功就挂了，此时正在启动子服务，导致程序异常
     */
    private static volatile boolean startSuccess = true;

    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        LogUtil.msgLog.info("启动 RPM 客户端...");
        try {
            new ClientStarter().launch(args);
        } catch (Exception e) {
            logger.error("程序异常！", e);
        }
        LogUtil.msgLog.info("RPM 停止工作！");
    }

    public void launch(String[] args) throws Exception {
        // 读取配置文件
        ClientConfig config;
        try {
            config = ClinetConfiguration.getEffectiveConfig();
        } catch (ConfigException e) {
            logger.error("配置文件错误：\n{}", e.getMessage());
            return;
        }
        // 注册关闭钩子
        addShutdownHook();

        // 启动控制服务
        CtrlServer cs = new CtrlServer(this);
        cs.start(config.getCtrlPort());

        while (!close) {
            ClientManager cm = new ClientManager(config);
            try {
                startSuccess = true;
                // 启动客户端
                cm.startClient();

                // 等待关闭
                if (startSuccess) {
                    synchronized (lock) {
                        try {
                            logger.debug("等待关闭程序的信号...");
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e1) {
                logger.error("启动客户端异常", e1);
            } finally {
                LogUtil.msgLog.info("关闭客户端...");
                // 关闭
                cm.shutdownClient();
            }
            if (!close) {
                LogUtil.msgLog.info("5秒后重启客户端...");
                for (int i = 5; i > 0 && !close; i--) {
                    System.out.println(" [" + i + "] ...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        cs.shutdown();
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!close) {
                close = true;
                logger.warn("shutdown hook exec");
            }
            shutdownNotify();
        }));
    }

    public static void shutdownNotify() {
        synchronized (lock) {
            startSuccess = false;
            lock.notifyAll();
        }
    }

    @Override
    public void close() {
        close = true;
        shutdownNotify();
    }
}
