package com.jet.cloud.deepmind.rtdb.model;

import com.jet.cloud.deepmind.common.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-31 09:55
 */
@Data
public class QueryBody implements Serializable {
    private static final long serialVersionUID = -8504152181082384075L;

    private List<String> points;

    private Long startTime;
    private Long endTime;

    private Integer interval;
    /**
     * (不区分大小写)
     * MILLISECONDS,
     * SECONDS,
     * MINUTES,
     * HOURS,
     * DAYS,
     * WEEKS,
     * MONTHS,
     * YEARS
     */
    private TimeUnit unit;
    private Integer aggregationInterval;
    private TimeUnit aggregationUnit;

    public QueryBody() {
    }

    public QueryBody(List<String> points) {
        this.points = points;
    }

    public QueryBody(List<String> points, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
        this.points = points;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.unit = unit;
    }

    public void setUnit(String unit) {
        this.unit = StringUtils.isNullOrEmpty(unit) ? null : TimeUnit.valueOf(unit.toUpperCase());
    }

    public void setAggregationUnit(String aggregationUnit) {
        this.aggregationUnit = StringUtils.isNullOrEmpty(aggregationUnit) ? null : TimeUnit.valueOf(aggregationUnit.toUpperCase());
    }

    public void setAggregationUnit(TimeUnit aggregationUnit) {
        this.aggregationUnit = aggregationUnit;
    }

}
