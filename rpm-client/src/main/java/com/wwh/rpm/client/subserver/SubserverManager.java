package com.wwh.rpm.client.subserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ForwardOverServer;

import io.netty.channel.EventLoopGroup;

/**
 * 子服务管理器
 * 
 * @author wwh
 *
 */
public class SubserverManager {
    private static final Logger logger = LoggerFactory.getLogger(SubserverManager.class);

    private ClientManager clientManager;

    /**
     * 子服务列表
     */
    private List<Subserver> subserverList;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public SubserverManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * 启动
     */
    public void startAll(EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("客户端正在运行！");
            return;
        }
        List<ForwardOverServer> list = clientManager.getConfig().getForwardOverServer();
        subserverList = new ArrayList<>(list.size());

        for (ForwardOverServer forwardOverServer : list) {
            Subserver ser = new Subserver(this, forwardOverServer);
            subserverList.add(ser);
            ser.start(bossGroup, workerGroup);
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * 停止
     */
    public void stopAll() {
        if (!isRunning()) {
            logger.warn("子主服务没有启动");
            return;
        }
        for (Subserver subserver : subserverList) {
            subserver.shutdown();
        }

    }

    public ClientConfig getConfig() {
        return clientManager.getConfig();
    }

    public String getToken() {
        return clientManager.getToken();
    }

}
