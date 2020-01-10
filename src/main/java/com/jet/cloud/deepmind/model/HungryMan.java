package com.jet.cloud.deepmind.model;

/**
 * @author zhuyicheng
 * @create 2020/1/2 9:33
 * @desc 饿汉
 */
public class HungryMan {
    private static HungryMan instance = new HungryMan();

    public HungryMan() {
    }

    public static HungryMan getInstance() {
        return instance;
    }
}
