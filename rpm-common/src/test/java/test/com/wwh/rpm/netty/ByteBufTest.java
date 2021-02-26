package test.com.wwh.rpm.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf heapBuf = Unpooled.buffer(10);

        System.out.println(heapBuf.toString());
        System.out.println("初始写入数据");

        for (int i = 0; i < heapBuf.capacity(); i++) {
            heapBuf.writeByte(i);
        }

        System.out.println("writerIndex : " + heapBuf.writerIndex());
        int readableBytes = heapBuf.readableBytes();
        System.out.println("readableBytes : " + readableBytes);
        System.out.println(heapBuf.toString());

        heapBuf.markReaderIndex();
        System.out.println("mark Reader Index  第一次读取：");
        for (int i = 0; i < readableBytes; i++) {
            byte b = heapBuf.readByte();
            System.out.println("第" + i + "个值是：" + b);
        }
        System.out.println(heapBuf.toString());

        heapBuf.resetReaderIndex();
        System.out.println("重置读取指针");
        System.out.println(heapBuf.toString());

        int readerIndex = heapBuf.readerIndex();
        System.out.println("当前读指针位置：" + readerIndex);

        System.out.println("重新写入 修改的数据");
        for (int i = 0; i < readableBytes; i++) {
            byte b = heapBuf.readByte();
            heapBuf.setByte(readerIndex++, b + 1);
        }

        heapBuf.resetReaderIndex();
        System.out.println("重新读取数据");
        for (int i = 0; i < readableBytes; i++) {
            byte b = heapBuf.readByte();
            System.out.println("第" + i + "个值是：" + b);
        }
    }
}
