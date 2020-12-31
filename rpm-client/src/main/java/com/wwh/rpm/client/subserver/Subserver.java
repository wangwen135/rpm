package com.wwh.rpm.client.subserver;

import com.wwh.rpm.client.config.pojo.ForwardOverServer;

/**
 * 子服务
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class Subserver {

    private SubserverManager subserverManager;
    private ForwardOverServer config;

    public Subserver(SubserverManager subserverManager, ForwardOverServer config) {
        this.subserverManager = subserverManager;
        this.config = config;

    }

    // 启动

    // 停止
}
