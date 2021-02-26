package com.wwh.rpm.protocol.security;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.wwh.rpm.common.codec.CodecUtils;
import com.wwh.rpm.common.codec.HexUtil;

public class SimpleEncryptionUtil {

    /**
     * 根据sid计算一个用于加密的秘钥，进行多次消息摘要
     * 
     * @param sid
     * @return
     */
    public static byte[] getEncryptionKeyBySid(String sid) {
        if (StringUtils.isBlank(sid)) {
            throw new IllegalArgumentException("sid 不能为空");
        }
        try {
            byte[] b1 = sid.getBytes(CodecUtils.DEFAULT_URL_ENCODING);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(CodecUtils.sha512(b1));
            baos.write(CodecUtils.md5(b1));
            baos.write(CodecUtils.sha256(b1));
            baos.write(CodecUtils.md5(b1));
            baos.write(CodecUtils.sha1(b1));
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("根据sid计算加密秘钥异常", e);
        }
    }
    
    public static void main(String[] args) {
        String sid ="1asdf413413dfadsfasdf";
        byte[] bytes = getEncryptionKeyBySid(sid);
        
        System.out.println(HexUtil.convertByteToHex(bytes));
    }

}
