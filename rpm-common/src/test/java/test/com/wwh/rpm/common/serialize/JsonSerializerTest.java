package test.com.wwh.rpm.common.serialize;

import java.io.IOException;

import com.wwh.rpm.common.serialize.json.JsonSerializer;

public class JsonSerializerTest {
    public static StudentPojo getObject() {
        StudentPojo student = new StudentPojo("studentName", 11, true);
        return student;
    }

    public static void main2(String[] args) throws IOException {
        JsonSerializer jsonSer = new JsonSerializer();

        byte[] bytes = jsonSer.serialize2("String Object");
        System.out.println(new String(bytes));

        byte[] bytes2 = jsonSer.serialize2(getObject());
        System.out.println(new String(bytes2));

        StudentPojo student = jsonSer.deserialize2(bytes2);
        System.out.println(student.toString());
    }

    public static void main(String[] args) throws IOException {
        JsonSerializer jsonSer = new JsonSerializer();

        byte[] bytes = jsonSer.serialize("String Object");
        System.out.println(new String(bytes));

        byte[] bytes2 = jsonSer.serialize(getObject());
        System.out.println(new String(bytes2));

        StudentPojo student = jsonSer.deserialize(bytes2, StudentPojo.class);
        System.out.println(student.toString());
    }
}
