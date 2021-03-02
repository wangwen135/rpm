package com.wwh.rpm.protocol.packet.auth;

import com.wwh.rpm.common.config.pojo.CommConfig;
import com.wwh.rpm.protocol.ProtocolConstants;
import com.wwh.rpm.protocol.packet.AbstractPacket;

/**
 * token
 * 
 * @author wangwh
 * @date 2020-12-30
 */
public class TokenPacket extends AbstractPacket {

    private static final long serialVersionUID = 1L;

    private String token;

    /**
     * 通讯配置
     */
    private CommConfig commConfig;

    @Override
    public byte getType() {
        return ProtocolConstants.TYPE_TOKEN;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CommConfig getCommConfig() {
        return commConfig;
    }

    public void setCommConfig(CommConfig commConfig) {
        this.commConfig = commConfig;
    }

}
