package com.wwh.rpm.common.serialize;

import com.wwh.rpm.common.serialize.kryo.KryoSerializer;

public class SerializerFactory {

    public static final Serialization instance = new KryoSerializer();

    public static Serialization getSerializer() {
        return instance;
    }

}
