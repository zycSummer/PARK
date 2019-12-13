package com.jet.cloud.deepmind.enums;

import com.jet.cloud.deepmind.annotation.EnumValid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author yhy
 * @create 2019-11-12 14:19
 */
public final class AlarmEnum {

    /**
     * 数值类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum ValueType {
        //为 最新值、最新值时间、当日差值、当月差值、当年差值、当前时间减去最新值时间
        //'LAST_VALUE','LAST_VALUE_TIMESTAMP','TODAY_DIFF','THIS_MONTH_DIFF','THIS_YEAR_DIFF','NOW_MINUS_LAST_VALUE_TIMESTAMP'

        LAST_VALUE("LAST_VALUE", "最新值"),
        LAST_VALUE_TIMESTAMP("LAST_VALUE_TIMESTAMP", "最新值时间"),
        TODAY_DIFF("TODAY_DIFF", "当日差值"),
        THIS_MONTH_DIFF("THIS_MONTH_DIFF", "当月差值"),
        THIS_YEAR_DIFF("THIS_YEAR_DIFF", "当年差值"),
        NOW_MINUS_LAST_VALUE_TIMESTAMP("NOW_MINUS_LAST_VALUE_TIMESTAMP", "当前时间减去最新值时间");

        @EnumValid
        private final String type;
        private final String description;

        public static ValueType getByType(String type) {
            for (ValueType item : ValueType.values()) {
                if (Objects.equals(type, item.getType())) {
                    return item;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum LogicalOperator {
        True("AND", "且"),
        False("OR", "或"),
        NA(null, "无");

        @EnumValid
        private final String type;
        private final String value;

        public static LogicalOperator getByType(String type) {
            for (LogicalOperator item : LogicalOperator.values()) {
                if (Objects.equals(type, item.getType())) {
                    return item;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum RelationalOperator {
        //LogicalOperator

        //'>','>=','<','<=','=','!='

        GT(">", "大于"),
        GE(">=", "大于等于"),
        lt("<", "小于"),
        LE("<=", "小于等于"),
        EQ("=", "等于"),
        NE("!=", "不等于"),
        NA(null, "无");

        @EnumValid
        private final String type;
        private final String value;

        public static RelationalOperator getByType(String type) {
            for (RelationalOperator item : RelationalOperator.values()) {
                if (Objects.equals(type, item.getType())) {
                    return item;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
    }

}
