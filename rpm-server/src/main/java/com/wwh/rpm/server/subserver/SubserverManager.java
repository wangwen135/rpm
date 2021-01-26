package com.wwh.rpm.server.subserver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.server.ServerManager;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;

import io.netty.channel.EventLoopGroup;

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
     * @param bossGroup
     * @param workerGroup
     * @throws Exception
     */
    public void startAll(EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws Exception {
        List<ForwardOverClient> configList = serverManager.getConfig().getForwardOverClient();

        if (configList == null || configList.isEmpty()) {
            logger.debug("经由客户端转发的列表为空，无子服务");
            return;
        }

        for (ForwardOverClient forwardOverClient : configList) {
            Subserver ser = new Subserver(this, forwardOverClient);
            subserverList.add(ser);
            ser.start(bossGroup, workerGroup);
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
}
