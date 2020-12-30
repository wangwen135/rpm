package com.wwh.rpm.server.channel;

import io.netty.channel.Channel;

public class ChannelManager {

    //每次都创建新连接吗？还是池化？
    //池化就需要考虑心跳问题
    //先每次搞新的，后面再优化
    /**
     * 获取客户端连接
     * 
     * @param cid
     * @return
     */
    public Channel getClientChannel(String cid) {
        return null;
    }
}
