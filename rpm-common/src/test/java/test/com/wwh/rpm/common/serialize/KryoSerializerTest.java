package test.com.wwh.rpm.common.serialize;

import java.io.IOException;

import com.wwh.rpm.common.serialize.kryo.KryoSerializer;

public class KryoSerializerTest {

    public static StudentPojo getObject() {
        StudentPojo student = new StudentPojo("studentName", 11, true);
        return student;
    }

    public static void main2(String[] args) throws IOException {
        KryoSerializer kryoSer = new KryoSerializer();

        byte[] bytes = kryoSer.serialize("String Object");
        System.out.println(new String(bytes));

        byte[] bytes2 = kryoSer.serialize(getObject());
        System.out.println(new String(bytes2));

        StudentPojo student = kryoSer.deserialize2(bytes2);
        System.out.println(student.toString());
    }

    public static void main(String[] args) throws IOException {
        KryoSerializer kryoSer = new KryoSerializer();

        byte[] bytes = kryoSer.serialize("String Object");
        System.out.println(new String(bytes));

        byte[] bytes2 = kryoSer.serialize(getObject());
        System.out.println(new String(bytes2));

        StudentPojo student = kryoSer.deserialize(bytes2, StudentPojo.class);
        System.out.println(student.toString());
    }

}
