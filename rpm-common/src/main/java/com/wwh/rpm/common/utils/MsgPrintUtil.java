package com.wwh.rpm.common.utils;

public class MsgPrintUtil {

    public static void countDownPrint(int count) {
        for (int i = count; i > 0; i--) {
            System.out.println(" [" + i + "] ...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
