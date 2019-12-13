package com.jet.cloud.deepmind.model;

import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/12/4 15:14
 * @desc
 */
@Data
public class Apple {
    private String id;
    private String name;
    private String createAge;

    public Apple() {
    }

    public Apple(String id, String name, String createAge) {
        this.id = id;
        this.name = name;
        this.createAge = createAge;
    }
}
