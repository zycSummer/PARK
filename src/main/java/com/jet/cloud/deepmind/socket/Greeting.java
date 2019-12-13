package com.jet.cloud.deepmind.socket;

import lombok.Data;

/**
 * @author yhy
 * @create 2019-10-22 10:41
 */
@Data
public class Greeting {
    private String content;

    public Greeting(String content) {
        this.content = content;
    }
}

