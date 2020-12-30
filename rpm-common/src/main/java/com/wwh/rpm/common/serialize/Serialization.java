package com.wwh.rpm.common.serialize;

import java.io.IOException;

/**
 * 序列化
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> c) throws IOException;
}
