package com.wwh.rpm.protocol.security;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.codec.AESCoder;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.auth.AuthPacket;

/**
 * <pre>
 * 随机数编码解码器
 * 使用AES128加密解密，秘钥为key的md5值
 * </pre>
 * 
 * @author wangwh
 * @date 2020-12-31
 */
public class RandomNumberCodec {

    /**
     * 解密随机数
     * 
     * @param authPacket
     * @param key
     * @return
     */
    public static int decrypt(AuthPacket authPacket, String key) {
        try {
            byte[] ciphertext = authPacket.getChiphers();
            byte[] cleartext = AESCoder.decrypt(ciphertext, AESCoder.getKeyByPassword(key));
            return Integer.valueOf(new String(cleartext, Constants.DEFAULT_CHARSET));
        } catch (Exception e) {
            throw new RPMException("解密随机数异常，请检查SID", e);
        }
    }

    /**
     * 加密随机数
     * 
     * @param randomNumber
     * @param key
     * @return
     */
    public static AuthPacket encrypt(int randomNumber, String key) {
        try {
            byte[] cleartext = Integer.toString(randomNumber).getBytes(Constants.DEFAULT_CHARSET);
            byte[] chiphertext = AESCoder.encrypt(cleartext, AESCoder.getKeyByPassword(key));
            AuthPacket auth = new AuthPacket();
            auth.setChiphers(chiphertext);
            return auth;
        } catch (Exception e) {
            throw new RPMException("加密随机数异常，请检查SID", e);
        }
    }
}
