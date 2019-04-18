package com.transaction.common.util;

import java.util.Random;
import java.util.UUID;

/**
 * Created by HuaWeiBo on 2019/4/18.
 */
public class UniqueUtil {

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 时间戳 + 四位随机数
     * @return
     */
    public static String timeMillisAndRandom() {
        Random random = new Random();
        long no = System.currentTimeMillis() * 10000 + random.nextInt(10000);
        return String.valueOf(no);
    }


}
