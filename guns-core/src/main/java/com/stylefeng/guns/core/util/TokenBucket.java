package com.stylefeng.guns.core.util;

/**
 * 令牌桶算法（限流）
 * @author zjxjwxk
 */
public class TokenBucket {

    /**
     * 桶的容量
     */
    private int bucketNums = 100;

    /**
     * 流入速度
     */
    private int rate = 1;

    /**
     * 当前令牌数
     */
    private int nowTokens;

    /**
     * 上次刷新当前令牌数的时间
     */
    private long lastTime = getNowTime();

    private long getNowTime() {
        return System.currentTimeMillis();
    }

    public boolean getToken() {

        // 记录当前时间
        long nowTime = getNowTime();

        // 刷新令牌
        nowTokens += (nowTime - lastTime) * rate;

        // 若令牌数比桶容量大，则改为桶容量
        nowTokens = Math.min(bucketNums, nowTokens);
        System.out.println("当前令牌数：" + nowTokens);

        // 修改上次刷新令牌数的时间为当前时间
        lastTime = nowTime;

        // 判断令牌是否足够
        if (nowTokens > 0) {
            nowTokens--;
            return true;
        } else {
            return false;
        }
    }
}
