package com.wwh.rpm.client.pool;

import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.wwh.rpm.common.exception.RPMException;

import io.netty.channel.Channel;

public class BufferManager implements Runnable {

    /**
     * 这个bufferManager挂在哪个连接下
     */
    private RpmConnection rpmConnection;

    private Thread thread;

    // 服务端和客户端的连接怎么区分，是不是要在ID上加其他标记
    // 比如说正负数
    private Map<Long, Channel> channels = new ConcurrentHashMap<>();
    // 缓冲数据
    private Map<Long, Queue<byte[]>> buffers = new ConcurrentHashMap<>();
    // 最后刷写时间
    private Map<Long, Long> lastTime = new ConcurrentHashMap<>();

    private Object condition = new Object();

    public BufferManager(RpmConnection rpmConnection) {
        this.rpmConnection = rpmConnection;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 注册连接，这里指的是子连接
     * 
     * @param id
     * @param channel
     */
    public void register(Long id, Channel channel) {
        if (channels.containsKey(id)) {
            throw new RPMException("重复注册，id=" + id);
        }
        channels.put(id, channel);
        // 初始化队列
        Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
        buffers.putIfAbsent(id, queue);
        // 更新最后时间
        lastTime.putIfAbsent(id, System.currentTimeMillis());
    }

    public void unregister(Long id) {

    }

    public void putBuffer(long id, byte[] buffer) {
        // 还应该记录时间，长期没有写出去的数据，线程应该将其销毁

    }

    /**
     * 关闭并清理
     */
    private void close() {

    }

    @Override
    public void run() {
        // 循环检测，并将数据刷新到目标channel中

    }

}
