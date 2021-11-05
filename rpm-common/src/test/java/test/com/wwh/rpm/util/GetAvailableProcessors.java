package test.com.wwh.rpm.util;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

public class GetAvailableProcessors {

    public static void main(String[] args) {
        int i = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
        System.out.println(i);
    }
}
