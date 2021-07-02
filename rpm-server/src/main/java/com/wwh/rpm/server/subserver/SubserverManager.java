package com.wwh.rpm.server.subserver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.connection.SimpleChannelWarp;
import com.wwh.rpm.server.ServerManager;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;

/**
 * 子服务管理器
 *
 * @author wwh
 */
public class SubserverManager {
    private static final Logger logger = LoggerFactory.getLogger(SubserverManager.class);

    private ServerManager serverManager;

    private List<Subserver> subserverList;

    public SubserverManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        subserverList = new ArrayList<>();
    }

    /**
     * 启动全部子服务
     * 
     * @throws Exception
     */
    public void startAll() throws Exception {
        List<ForwardOverClient> configList = serverManager.getConfig().getForwardOverClient();

        if (configList == null || configList.isEmpty()) {
            logger.debug("经由客户端转发的列表为空，无子服务");
            return;
        }

        for (ForwardOverClient forwardOverClient : configList) {
            Subserver ser = new Subserver(this, forwardOverClient);
            subserverList.add(ser);
            ser.start();
        }
    }

    /**
     * 停止全部子服务
     */
    public void stopAll() {
        for (Subserver subserver : subserverList) {
            subserver.shutdown();
        }
    }

    public ServerConfig getConfig() {
        return serverManager.getConfig();
    }

    /**
     * 获取一个客户端的转发通道
     * 
     * @param subserver
     * @param callback  回调方法（成功、失败、超时）
     */
    public void acquireClientForwardChannel(Subserver subserver, Consumer<SimpleChannelWarp> callback) {
        ForwardOverClient forwardConfig = subserver.getForwardConfig();
        serverManager.getMasterServer().acquireClientForwardChannel(forwardConfig, callback);
    }

}
