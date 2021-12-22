package com.wwh.rpm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.client.config.ClinetConfiguration;
import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.common.utils.MsgPrintUtil;
import com.wwh.rpm.ctrl.client.CtrlClient;

/**
 * 关闭客户端
 * 
 * @author wangwh
 * @date 2021-1-22
 */
public class Shutdown {
    private static final Logger logger = LoggerFactory.getLogger(Shutdown.class);

    public static void main(String[] args) {

        logger.warn("主动关闭客户端...");

        // 读取配置文件
        ClientConfig config;
        try {
            config = ClinetConfiguration.getClientConfig();
        } catch (ConfigException e) {
            logger.error("配置文件错误：\n{}", e.getMessage());
            return;
        }

        CtrlClient ctrlClient = new CtrlClient();

        System.out.println("发送关闭指令...");
        try {
            Boolean result = ctrlClient.sendShutdownCommand(config.getCtrlPort());

            System.out.print("关闭结果：");
            System.out.println(result == null ? "未知" : result == true ? "成功" : "失败");
            //MsgPrintUtil.countDownPrint(3);
        } catch (Exception e) {
            e.printStackTrace();
            MsgPrintUtil.countDownPrint(3);
        }

    }
}
