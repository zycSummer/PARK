package com.jet.cloud.deepmind.model;

/**
 * @author zhuyicheng
 * @create 2020/1/2 9:27
 * @desc 懒汉
 */
public class LazyMan {
    private static LazyMan instance;

    public LazyMan() {
    }

    public static synchronized LazyMan getInstance() {
        if (instance == null) {
            instance = new LazyMan();
        }
        return instance;
    }
}
