package test.com.wwh.rpm.ctrl;

import com.wwh.rpm.ctrl.Closeer;
import com.wwh.rpm.ctrl.server.CtrlServer;

public class ServerDemo implements Closeer {

    private static final int CTRL_PORT = 6666;

    public static void main(String[] args) {
        System.out.println("模拟服务端");

        ServerDemo sd = new ServerDemo();
        CtrlServer ctrlServer = new CtrlServer(sd);

        System.out.println("启动控制服务...");
        try {
            ctrlServer.start(CTRL_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sd.doSomething();
        System.out.println("程序运行结束！");
        ctrlServer.shutdown();
    }

    private boolean flag = true;

    private void doSomething() {
        while (flag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("do something ...");
        }
    }

    @Override
    public void close() {
        System.out.println("关闭方法被调用");
        flag = false;

    }
}
