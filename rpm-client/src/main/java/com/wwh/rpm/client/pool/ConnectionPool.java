package com.wwh.rpm.client.pool;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.client.config.pojo.ServerConfig;
import com.wwh.rpm.client.pool.connection.CommonConnection;
import com.wwh.rpm.client.pool.connection.RegisterConnection;
import com.wwh.rpm.common.config.pojo.CommunicationConfig;
import com.wwh.rpm.common.exception.RPMException;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 连接池<br>
 * 注意线程安全问题
 * 
 * @author wangwh
 * @date 2021-12-28
 */
public class ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private ClientConfig clientConfig;
    private BufferManager bufferManager;
    /**
     * 获取连接计数器
     */
    private AtomicLong counter = new AtomicLong(0);

    /**
     * ID 生成器
     */
    private AtomicInteger idCreater = new AtomicInteger(0);

    /**
     * 主连接
     */
    private RegisterConnection regConnection;

    /**
     * 预连接
     */
    private Map<Integer, RpmConnection> preconnectMap = new ConcurrentHashMap<>();

    /**
     * 已连接
     */
    private Map<Integer, RpmConnection> connectedMap = new ConcurrentHashMap<>();

    /**
     * 服务端返回的token
     */
    private String token;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private boolean startingSuccess = false;

    private EventLoopGroup workerGroup;

    public ConnectionPool(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.bufferManager = new BufferManager();
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public String getToken() {
        return token;
    }

    /**
     * <pre>
     * 启动连接池
     * 阻塞，直到获取到token
     * 并启动剩余连接
     * </pre>
     * 
     * @throws Exception
     */
    public void startPool() throws Exception {
        if (!isRunning.compareAndSet(false, true)) {
            logger.error("连接池已经启动！");
            return;
        }
        workerGroup = new NioEventLoopGroup();

        logger.debug("启动注册连接...");
        // 一个客户端只能注册一次，避免有多个客户端ID相同时注册的对不上

        int id = idCreater.getAndIncrement();
        regConnection = new RegisterConnection(this, id);
        try {
            regConnection.start();
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            throw e;
        }
        //
        preconnectMap.put(id, regConnection);

        // 获取到token，剩下的用token注册
        token = regConnection.waitToken();
        logger.debug("获取到token：{}", token);
        startingSuccess = true;

        logger.debug("启动剩余连接...");
        initCommonConnection();
    }

    /**
     * 关闭连接池
     */
    public void shutdownPool() {
        if (!isRunning()) {
            logger.warn("连接池没有启动");
            return;
        }

        logger.debug("关闭连接池...");

        logger.debug("关闭池中子连接，共：{}个", preconnectMap.size());
        preconnectMap.values().forEach(conn -> {
            conn.shutdown();
        });

        logger.debug("关闭工作线程池");
        workerGroup.shutdownGracefully();

    }

    public int getPoolSize() {
        return clientConfig.getPool().getPoolSize();
    }

    /**
     * <pre>
     * 获取连接
     * 连接不够时会新建连接
     * </pre>
     * 
     * @return
     */
    public RpmConnection getConnection() {
        if (!isRunning()) {
            logger.warn("连接池没有启动");
            return null;
        }
        if (!startingSuccess) {
            logger.warn("连接池还未启动成功");
            return null;
        }

        repairPool();

        if (connectedMap.size() == 0) {
            throw new RPMException("连接池中无可用连接");
        }

        long index = counter.incrementAndGet();

        // 取模
        RpmConnection rpmConn = connectedMap.values().stream().skip(index % connectedMap.size()).findFirst()
                .orElseThrow(() -> new RPMException("没有可用连接"));

        if (!rpmConn.isOk()) {
            logger.warn("连接[{}]不是正常状态", rpmConn.getConnectionId());
            rpmConn.shutdown();
            return null;
        }

        return rpmConn;

    }

    /**
     * 启动普通连接
     */
    private void initCommonConnection() {
        for (int i = 1; i < getPoolSize(); i++) {
            createConnection();
        }
    }

    public void createConnection() {
        int id = idCreater.incrementAndGet();
        CommonConnection connection = new CommonConnection(this, id);
        preconnectMap.put(id, connection);
        try {
            logger.debug("启动通信连接【{}】...", id);
            connection.start();
        } catch (Exception e) {
            logger.error("启动通信连接【{}】异常", id, e);
            connection.shutdown();
            preconnectMap.remove(id);
        }
    }

    private void repairPool() {
        if (preconnectMap.size() < getPoolSize()) {
            createConnection();
        }
    }

    /**
     * 连接成功
     * 
     * @param id
     * @param connection
     */
    public void connectionSuccessful(Integer id, RpmConnection connection) {
        preconnectMap.put(id, connection);
        connectedMap.put(id, connection);
    }

    /**
     * 连接失败
     * 
     * @param id
     */
    public void connectionFail(Integer id) {
        preconnectMap.remove(id);
        connectedMap.remove(id);
    }

    /**
     * 总共获取过多少次连接
     * 
     * @return
     */
    public long totalObtained() {
        return counter.get();
    }

    /**
     * 获取当前池大小（当前池中连接数）
     * 
     * @return
     */
    public int getCurrentPoolSize() {
        return connectedMap.size();
    }

    public static void main(String[] args) {
        int index = 126;
        Collection<Integer> c = Arrays.asList(1, 2, 3, 4, 5, 6);

        Optional<Integer> optional = c.stream().skip(index % c.size()).findFirst();

        System.out.println(optional.orElseThrow(() -> new RPMException("没有可用连接")));
    }

    public CommunicationConfig getCommunicationConfig() {
        return clientConfig.getCommunication();
    }

    public ServerConfig getServerConf() {
        return clientConfig.getServerConf();
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    /**
     * 通信中断
     */
    public void communicationInterrupt() {
        Log.warn("通信中断！！");
        shutdownPool();
    }

}
