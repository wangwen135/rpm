package com.wwh.rpm.client.pool;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.RPMException;

import io.netty.channel.Channel;

/**
 * 缓冲管理器
 * 
 * @author wangwh
 * @date 2022-2-9
 */
public class BufferManager implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BufferManager.class);

    private Object condition = new Object();
    private volatile boolean runFlag = true;
    private volatile boolean activate = true;
    private Thread thread;

    // 正向连接用正数，反向连接用负数
    private Map<Long, Channel> channels = new ConcurrentHashMap<>();

    private Map<Long, Queue<byte[]>> buffers = new ConcurrentHashMap<>();

    private Map<Long, Long> lastOutputTime = new ConcurrentHashMap<>();

    private Map<Long, Long> lastReceiveTime = new ConcurrentHashMap<>();

    public BufferManager() {
        thread = new Thread(this, "BufferManager");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 注册连接，这里指的是子连接
     * 
     * @param id
     * @param channel
     */
    public void registerSubChannel(Long id, Channel channel) {
        if (channels.containsKey(id)) {
            throw new RPMException("重复注册，id=" + id);
        }
        channels.put(id, channel);

        lastOutputTime.put(id, System.currentTimeMillis());

        activate();
    }

    public Channel unregister(Long id) {
        Channel channel = channels.remove(id);
        Queue<byte[]> queue = buffers.remove(id);
        if (channel != null && queue != null && queue.size() > 0) {
            byte[] data;
            while ((data = queue.poll()) != null) {
                channel.write(data);
            }
            channel.flush();
        }

        lastOutputTime.remove(id);
        lastReceiveTime.remove(id);

        return channel;
    }

    public void putBuffer(long id, byte[] buffer) {

        Queue<byte[]> queue = buffers.getOrDefault(id, new ConcurrentLinkedQueue<>());
        queue.add(buffer);
        lastReceiveTime.put(id, System.currentTimeMillis());

        activate();
    }

    /**
     * 关闭并清理
     */
    public void close() {
        // 关闭线程
        runFlag = false;
        activate();
        // 反注册
        for (Long id : channels.keySet()) {
            unregister(id);
        }
    }

    private void activate() {
        activate = true;
        synchronized (condition) {
            condition.notify();
        }
    }

    @Override
    public void run() {
        logger.debug("缓冲管理器内部线程启动");

        while (runFlag) {
            if (!activate) {
                synchronized (condition) {
                    try {
                        condition.wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            activate = false;

            // 刷写数据
            flushData();
            // 清理异常数据
            clearErrorData();

        }

        logger.debug("缓冲管理器内部线程停止");
    }

    private void flushData() {
        for (Map.Entry<Long, Channel> e : channels.entrySet()) {
            Long id = e.getKey();
            Channel channel = e.getValue();

            Queue<byte[]> queue = buffers.get(id);
            if (queue != null && queue.size() > 0) {
                byte[] data;
                while ((data = queue.poll()) != null) {
                    channel.write(data);
                }
                channel.flush();
                lastOutputTime.put(id, System.currentTimeMillis());
            }
        }
    }

    private static final long deathTime = 30 * 60 * 1000;// 30分钟没有收发任何数据，就认为失效

    private void clearErrorData() {
        long timeLine = System.currentTimeMillis() - deathTime;

        Set<Long> idSet = lastOutputTime.keySet();
        idSet.addAll(lastReceiveTime.keySet());

        for (Long id : idSet) {
            Long outputTime = lastOutputTime.get(id);
            Long receiveTime = lastReceiveTime.get(id);

            if ((outputTime == null || outputTime < timeLine) && (receiveTime == null || receiveTime < timeLine)) {
                unregister(id);
            }
        }
    }

}
