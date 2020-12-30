package com.wwh.rpm.common.serialize.json;

import java.io.Serializable;

public class JsonSerializeObj implements Serializable {

    private static final long serialVersionUID = 1L;

    private Class<?> c;
    private byte[] d;

    public Class<?> getC() {
        return c;
    }

    public void setC(Class<?> c) {
        this.c = c;
    }

    public byte[] getD() {
        return d;
    }

    public void setD(byte[] d) {
        this.d = d;
    }

}
