package com.wwh.rpm.common.serialize.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer {
	public byte[] serialize(Object obj) {
		Kryo kryo = kryoLocal.get();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteArrayOutputStream);// <1>
		kryo.writeClassAndObject(output, obj);// <2>
		output.close();
		return byteArrayOutputStream.toByteArray();
	}

	public <T> T deserialize(byte[] bytes) {
		Kryo kryo = kryoLocal.get();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		Input input = new Input(byteArrayInputStream);// <1>
		input.close();
		return (T) kryo.readClassAndObject(input);// <2>
	}

	private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {// <3>
		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.setReferences(true);// 默认值为 true, 强调作用
			kryo.setRegistrationRequired(false);// 默认值为 false, 强调作用
			return kryo;
		}
	};
}
