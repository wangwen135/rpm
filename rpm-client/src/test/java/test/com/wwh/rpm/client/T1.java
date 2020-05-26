package test.com.wwh.rpm.client;

import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;

public class T1 {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    // 新建一个Channel时会自动新建一个ChannelPipeline，也就是说他们之间是一对一的关系。另外需要注意的是：ChannelPipeline是线程安全的，也就是说，我们可以动态的添加、删除其中的ChannelHandler
    // 服务器需要对用户登录信息进行加密，而其他信息不加密，则可以首先将加密Handler添加到ChannelPipeline，验证完用户信息后，主动从ChnanelPipeline中删除，从而实现该需求。

}
