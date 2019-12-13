package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 三个元素的元组，用于在一个方法里返回三种类型的值
 *
 * @author yhy
 * @create 2019-12-03 10:29
 */
@Data
public class Triple<L, M, R> implements Serializable {
    public L left;
    public M mid;
    public R right;

    public Triple() {
    }

    public Triple(L left, M mid, R right) {
        this.left = left;
        this.mid = mid;
        this.right = right;
    }
}
