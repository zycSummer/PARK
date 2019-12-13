package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/11/18 13:31
 * @desc 报表查询全部内容
 */
@Data
public class ReportInfosVO implements Serializable {
    private static final long serialVersionUID = 4019519841613399484L;
    private List<ReportTableVO> reportTableVOS;
    private List<Map<String, Object>> reportInfoVOS;
    private List<Long> timeStamps;
}
