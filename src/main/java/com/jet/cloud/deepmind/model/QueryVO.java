package com.jet.cloud.deepmind.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 查询分页模型
 *
 * @author yhy
 * @create 2019-10-14 16:10
 */
@Data
public class QueryVO implements Serializable {
    private static final long serialVersionUID = 6153981373031741993L;

    @NotNull
    private int limit;
    @NotNull
    private int page;

    /**
     * 搜索对象
     */
    private JSONObject key;

    @ApiIgnore
    public Pageable Pageable() {
        return PageRequest.of(page - 1, limit, new Sort(Sort.Direction.ASC, "id"));
    }

    @ApiIgnore
    public Pageable Pageable(Sort sort) {
        return PageRequest.of(page - 1, limit, sort);
    }
}
