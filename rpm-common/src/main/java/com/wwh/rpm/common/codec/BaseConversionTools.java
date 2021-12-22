package com.wwh.rpm.common.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 进制换算工具
 * 
 */
public class BaseConversionTools {
    private static final Character[] CHAR_ARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z' };

    /**
     * 支持的最小进制
     */
    public static final int SUPPORT_MIN_BASE = 2;
    /**
     * 支持的最大进制
     */
    public static final int SUPPORT_MAX_BASE = 36;

    /**
     * Binary
     */
    public static final int BASE2 = 2;
    /**
     * Octal
     */
    public static final int BASE8 = 8;
    /**
     * Decimal
     */
    public static final int BASE10 = 10;
    /**
     * HEX
     */
    public static final int BASE16 = 16;
    public static final int BASE32 = 32;
    public static final int BASE36 = 36;

    /**
     * 将十进制数字转成对应进制的字符串<br>
     * 有效字符串0~9，A~Z
     * 
     * @param decimal 十进制的数字
     * @param radix   进制，支持 2~36
     * @return 对应进制的字符串
     */
    public static String convert(long decimal, int radix) {
        if (decimal < 0) {
            throw new IllegalArgumentException("不支持负数");
        }
        if (radix < SUPPORT_MIN_BASE || radix > SUPPORT_MAX_BASE) {
            throw new IllegalArgumentException("有效的进制范围2 ~ 36");
        }

        StringBuffer sbuf = new StringBuffer();
        List<Character> list = new ArrayList<>();

        recursiveResolver(list, decimal, radix);

        Collections.reverse(list);

        for (Character character : list) {
            sbuf.append(character);
        }
        return sbuf.toString();
    }

    private static void recursiveResolver(List<Character> list, long decimal, int radix) {
        if (decimal < radix) {
            list.add(CHAR_ARRAY[(int) decimal]);
        } else {
            list.add(CHAR_ARRAY[(int) decimal % radix]);
            recursiveResolver(list, decimal / radix, radix);
        }
    }

    private static int getIndex(char c) {
        for (int i = 0; i < CHAR_ARRAY.length; i++) {
            if (CHAR_ARRAY[i] == c) {
                return i;
            }
        }
        throw new RuntimeException("无效的字符：" + c);
    }

    /**
     * a的b次幂
     * 
     * @param a
     * @param b
     * @return
     */
    private static long pow(long a, int b) {
        if (b <= 0) {
            return 1;
        }
        long ret = a;
        for (int i = 1; i < b; i++) {
            ret = ret * a;
        }
        return ret;
    }

    /**
     * 将指定进制的字符串转成十进制的数<br>
     * 有效字符串0~9，A~Z
     * 
     * @param numberStr 对应进制的字符串形式
     * @param radix     进制，支持 2~36
     * @return 十进制的数
     */
    public static long parse2Decimal(String numberStr, int radix) {
        if (numberStr == null || numberStr.length() < 1) {
            throw new IllegalArgumentException("将要转换的字符串不能为空");
        }
        if (radix < SUPPORT_MIN_BASE || radix > SUPPORT_MAX_BASE) {
            throw new IllegalArgumentException("有效的进制范围2 ~ 36");
        }
        numberStr = numberStr.toUpperCase();
        long ret = 0;
        for (int i = 0, k = numberStr.length() - 1; i < numberStr.length(); i++, k--) {
            char cn = numberStr.charAt(i);
            int a = getIndex(cn);
            if (a >= radix) {
                throw new IllegalArgumentException("第" + (i + 1) + "个字符" + cn + "超出" + radix + "进制的范围");

            }
            ret += pow(radix, k) * a;
        }

        return ret;
    }

    /**
     * 10进制数字转36进制字符串
     * 
     * @param decimal
     * @return
     */
    public static String convert2Base36(long decimal) {
        return convert(decimal, BASE36);
    }

    /**
     * 36进制字符串转10进制数
     * 
     * @param base36
     * @return
     */
    public static long parseBase36(String base36) {
        return parse2Decimal(base36, BASE36);
    }

    public static void main(String[] args) {
        System.out.println(convert2Base36(1790905));
        System.out.println(parseBase36("12DVD"));
        System.out.println("----------------");

        System.out.println(convert(100, 2));

        System.out.println(parse2Decimal("88", 9));
    }
}
