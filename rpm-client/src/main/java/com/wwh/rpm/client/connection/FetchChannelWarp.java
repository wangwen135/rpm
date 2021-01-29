package com.wwh.rpm.client.connection;

import com.wwh.rpm.common.exception.RPMException;

import io.netty.channel.Channel;

/**
 * <pre>
 * 获取channel时的包装
 * 为支持异步
 * </pre>
 * 
 * @author wangwh
 * @date 2021-1-29
 */
public class FetchChannelWarp {

    private static final int DEFAULT_WAIT_TIME_SECOND = 3;

    private Object lock = new Object();

    private volatile Channel channel;

    private volatile Throwable cause;

    private volatile boolean isTimeout = false;

    private boolean isCalled = false;

    /**
     * <pre>
     * 一次性方法，阻塞方法
     * 等待获取一个有效的Channel对象，最多等3秒
     * 或者抛出异常
     * </pre>
     * 
     * @return
     * @throws Exception
     */
    public synchronized Channel getChannelOnce() throws Exception {
        return getChannelOnce(DEFAULT_WAIT_TIME_SECOND);
    }

    /**
     * <pre>
     * 一次性方法，阻塞方法
     * 等待获取一个有效的Channel对象
     * 或者抛出异常
     * </pre>
     * 
     * @param waitTimeSecond 最大等待时间，秒
     * @return
     * @throws Exception
     */
    public synchronized Channel getChannelOnce(int waitTimeSecond) throws Exception {
        if (isCalled) {
            throw new RPMException("获取通道的方法已经调用过一次了");
        }
        isCalled = true;

        for (int i = 0; i < waitTimeSecond; i++) {
            Channel c = get();
            if (c != null) {
                return c;
            }
            synchronized (lock) {
                lock.wait(1000);
            }
        }
        Channel c = get();
        if (c != null) {
            return c;
        }
        // 标记为超时，等链接建立好之后又要关闭掉
        isTimeout = true;
        throw new RPMException("获取通道超时");
    }

    private Channel get() throws Exception {
        if (channel != null) {
            return channel;
        }
        if (cause != null) {
            throw new Exception(cause);
        }
        return null;
    }

    public void setSuccess(Channel channel) {
        if (isTimeout) {
            closeChannel(channel);
            throw new RPMException("等待链接方认为超时了");
        }

        this.channel = channel;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void setError(Throwable cause) {
        if (isTimeout) {
            closeChannel(channel);
            throw new RPMException("等待链接方认为超时了");
        }
        this.cause = cause;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public boolean isTimeout() {
        return isTimeout;
    }

    private void closeChannel(Channel channel) {
        if (channel != null) {
            channel.close();
        }
    }
}
