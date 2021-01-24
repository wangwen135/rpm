package com.wwh.rpm.server.subserver;

import com.wwh.rpm.server.ServerManager;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import io.netty.channel.EventLoopGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 子服务管理器
 *
 * @author wwh
 */
public class SubserverManager {

    private ServerManager serverManager;

    private List<Subserver> subserverList;

    public SubserverManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }


    // 创建全部子服务

    // 启动
    public void startAll(EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws Exception {
        List<ForwardOverClient> configList = serverManager.getConfig().getForwardOverClient();
        subserverList = new ArrayList<>();

        for (ForwardOverClient forwardOverClient : configList) {

        }

    }

    // 停止
    public void stopAll() {
        for (Subserver subserver : subserverList) {
            subserver.shutdown();
        }
    }

    public ServerConfig getConfig() {
        return serverManager.getConfig();
    }
}
