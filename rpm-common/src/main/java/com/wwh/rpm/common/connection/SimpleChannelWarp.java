package com.wwh.rpm.common.connection;

import com.wwh.rpm.common.exception.RPMException;

import io.netty.channel.Channel;

public class SimpleChannelWarp {

    private Channel channel;
    private Throwable cause;

    public static SimpleChannelWarp ofChannel(Channel channel) {
        SimpleChannelWarp scw = new SimpleChannelWarp();
        scw.setChannel(channel);
        return scw;
    }

    public static SimpleChannelWarp ofErroe(Throwable cause) {
        SimpleChannelWarp scw = new SimpleChannelWarp();
        scw.setError(cause);
        return scw;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setError(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getError() {
        return cause;
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * 成功返回通道，否则抛出异常
     * 
     * @return
     */
    public Channel get() {
        if (cause != null) {
            if (cause instanceof RPMException) {
                throw (RPMException) cause;
            } else {
                throw new RPMException(cause);
            }
        }
        return channel;
    }
}
