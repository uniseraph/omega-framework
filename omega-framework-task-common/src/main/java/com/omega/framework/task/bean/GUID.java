package com.omega.framework.task.bean;

import java.util.UUID;

/**
 * UUID默认输出是40个字符形式，改成26个字符形式，节省存储，提高查找效率
 */
public class GUID {

    public static String get() {
        UUID uuid = UUID.randomUUID();
        StringBuffer sb = new StringBuffer();
        get(sb, uuid.getMostSignificantBits());
        get(sb, uuid.getLeastSignificantBits());
        return sb.toString();
    }

    private static StringBuffer get(StringBuffer sb, long bits) {
        int count = 13;
        while (count-- > 0) {
            long low = bits & 0x1FL;
            if (low < 10L) {
                sb.append((char) ('0' + low));
            } else {
                sb.append((char) ('A' + (low - 10)));
            }

            bits >>>= 5;
        }

        return sb;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(GUID.get());
        }
    }

}
