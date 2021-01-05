package com.wwh.rpm.server.master;

import java.util.HashMap;
import java.util.Map;

import com.wwh.rpm.common.exception.RPMException;

import io.netty.channel.Channel;

public class ClientTokenManager {

    private Map<String, ClientToken> cidMap = new HashMap<>();
    private Map<String, ClientToken> tokenMap = new HashMap<>();

    public void regist(String cid, String token, Channel channel) {
        if (cid == null || token == null || channel == null) {
            throw new RPMException("参数不能为空");
        }
        unregist(cid);

        ClientToken ct = new ClientToken(cid, token, channel);
        cidMap.put(cid, ct);
        tokenMap.put(token, ct);
    }

    public void unregist(String cid) {
        ClientToken ct = cidMap.remove(cid);
        if (ct != null) {
            tokenMap.remove(ct.getToken());
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

    class ClientToken {
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
