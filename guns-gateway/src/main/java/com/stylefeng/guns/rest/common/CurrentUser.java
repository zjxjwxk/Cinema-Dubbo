package com.stylefeng.guns.rest.common;

public class CurrentUser {

    /**
     * 线程绑定的存储空间，使用InheritableThreadLocal，而不是ThreadLocal，
     * 是因为Hystrix熔断器会进行线程切换，切换后则拿不到登录时的ThreadLocal，
     * 也就拿不到用户信息，会误以为用户未登录
     */
    private static final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void saveUserId(String userId) {
        threadLocal.set(userId);
    }

    public static String getCurrentUser() {
        return threadLocal.get();
    }
}
