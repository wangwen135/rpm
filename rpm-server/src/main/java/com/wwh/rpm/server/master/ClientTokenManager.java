package com.wwh.rpm.server.master;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.common.utils.LogUtil;

import io.netty.channel.Channel;

/**
 * cid 和 token 管理器
 * 
 * @author wangwh
 * @date 2021-1-5
 */
public class ClientTokenManager {

    private final ReentrantLock lock = new ReentrantLock();

    private Map<String, ClientToken> cidMap = new HashMap<>();
    private Map<String, ClientToken> tokenMap = new HashMap<>();

    private boolean kickoutModel = true;

    public void regist(String cid, String token, Channel channel) {
        if (cid == null || token == null || channel == null) {
            throw new RPMException("参数不能为空");
        }

        lock.lock();
        try {
            LogUtil.msgLog.info("客户端注册！ cid={}  address={}", cid, channel.remoteAddress());

            // 这里应该是不能踢掉别人的
            if (kickoutModel) {
                unregistByCid(cid);
            } else {
                ClientToken ct = cidMap.get(cid);
                if (ct != null) {
                    throw new RPMException("已有客户端【" + cid + "】在线，token=" + ct.getToken() + " address="
                            + ct.getChannel().remoteAddress());
                }
            }

            ClientToken ct = new ClientToken(cid, token, channel);
            cidMap.put(cid, ct);
            tokenMap.put(token, ct);
        } finally {
            lock.unlock();
        }
    }

    public void unregistByCid(String cid) {
        lock.lock();
        try {
            ClientToken ct = cidMap.remove(cid);
            if (ct != null) {
                LogUtil.msgLog.info("注销客户端【{}】{} token={}", ct.getCid(), ct.getChannel().remoteAddress(),
                        ct.getToken());
                tokenMap.remove(ct.getToken());
                ct.getChannel().close();
            }
        } finally {
            lock.unlock();
        }
    }

    public void unregistByToken(String token) {
        lock.lock();
        try {
            ClientToken ct = tokenMap.remove(token);
            if (ct != null) {
                LogUtil.msgLog.info("注销客户端【{}】{} token={}", ct.getCid(), ct.getChannel().remoteAddress(), token);
                cidMap.remove(ct.getCid());
                ct.getChannel().close();
            }
        } finally {
            lock.unlock();
        }

    }

    public String getCidByToken(String token) {
        ClientToken ct = tokenMap.get(token);
        if (ct != null) {
            return ct.getCid();
        } else {
            return null;
        }
    }

    public String getTokenByCid(String cid) {
        ClientToken ct = cidMap.get(cid);
        if (ct != null) {
            return ct.getToken();
        } else {
            return null;
        }
    }

    public Channel getChannelByCid(String cid) {
        ClientToken ct = cidMap.get(cid);
        if (ct != null) {
            return ct.getChannel();
        } else {
            return null;
        }
    }

    private class ClientToken {
        private String cid;
        private String token;
        private Channel channel;

        public ClientToken(String cid, String token, Channel channel) {
            this.cid = cid;
            this.token = token;
            this.channel = channel;
        }

        public String getCid() {
            return cid;
        }

        public String getToken() {
            return token;
        }

        public Channel getChannel() {
            return channel;
        }

    }
}
