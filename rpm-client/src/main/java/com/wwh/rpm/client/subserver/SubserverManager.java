package com.wwh.rpm.client.subserver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.ClientManager;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ForwardOverServer;
import com.wwh.rpm.client.pool.ConnectionPool;

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

    public SubserverManager(ClientManager clientManager) {
        this.clientManager = clientManager;
        subserverList = new ArrayList<>();
    }

    /**
     * 启动全部子服务
     * 
     * @throws Exception
     */
    public void startAll() throws Exception {
        List<ForwardOverServer> configList = clientManager.getConfig().getForwardOverServer();

        if (configList == null || configList.isEmpty()) {
            logger.debug("经由服务端转发的列表为空，无子服务");
            return;
        }

        for (ForwardOverServer forwardOverServer : configList) {
            Subserver ser = new Subserver(this, forwardOverServer);
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

    public ClientConfig getConfig() {
        return clientManager.getConfig();
    }

/*    public String getToken() {
return clientManager.getToken();
}
*/
    public ConnectionPool getConnectionPool() {
        return clientManager.getConnectionPool();
    }

    public long getNextId() {
        return clientManager.getNextId();
    }
}
