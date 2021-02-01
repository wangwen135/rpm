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

    private volatile boolean isClose = false;

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

    /**
     * 通道建立成功时调用
     * 
     * @param channel 成功获取到的通道
     */
    public void setSuccess(Channel channel) {
        if (isClose) {
            closeChannel(channel);
            this.cause = new RPMException("已经关闭");
        } else if (isTimeout) {
            closeChannel(channel);
            this.cause = new RPMException("已经超时");
        } else {
            this.channel = channel;
        }
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * 通道建立失败时调用
     * 
     * @param cause 失败原因
     */
    public void setError(Throwable cause) {
        if (isClose) {
            cause = new RPMException("已经标记为关闭", cause);
        } else if (isTimeout) {
            cause = new RPMException("等待链接方认为超时了", cause);
        }
        this.cause = cause;
        closeChannel(channel);
        this.channel = null;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * 关闭或标记为关闭<br>
     * 如果标记为关闭，当连接进入时会将其关闭
     */
    public void close() {
        this.isClose = true;
        closeChannel(channel);
    }

    /**
     * 是否超时
     * 
     * @return
     */
    public boolean isTimeout() {
        return isTimeout;
    }

    private void closeChannel(Channel channel) {
        if (channel != null) {
            channel.close();
        }
    }
}
