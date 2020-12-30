package com.wwh.rpm.common.serialize.json;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.wwh.rpm.common.serialize.Serialization;

public class JsonSerializer implements Serialization {

    public byte[] serialize2(Object obj) throws IOException {
        byte[] date = JSON.toJSONBytes(obj);
        JsonSerializeObj serObj = new JsonSerializeObj();
        serObj.setD(date);
        serObj.setC(obj.getClass());
        return JSON.toJSONBytes(serObj);
    }

    public <T> T deserialize2(byte[] bytes) throws IOException {
        JsonSerializeObj serObj = JSON.parseObject(bytes, JsonSerializeObj.class);
        return JSON.parseObject(serObj.getD(), serObj.getC());
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> c) throws IOException {
        return JSON.parseObject(bytes, c);
    }

}
