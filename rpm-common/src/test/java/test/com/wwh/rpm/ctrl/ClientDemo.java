package test.com.wwh.rpm.ctrl;

import com.wwh.rpm.ctrl.client.CtrlClient;

public class ClientDemo {

    private static final int CTRL_PORT = 6666;

    public static void main(String[] args) {
        CtrlClient ctrlClient = new CtrlClient();

        System.out.println("发送关闭指令...");
        try {
            Boolean result = ctrlClient.sendShutdownCommand(CTRL_PORT);

            System.out.print("关闭结果：");
            System.out.println(result == null ? "未知" : result == true ? "成功" : "失败");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
