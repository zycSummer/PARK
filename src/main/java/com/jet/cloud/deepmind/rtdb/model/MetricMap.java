package com.jet.cloud.deepmind.rtdb.model;

import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.springframework.web.context.ContextLoader;
import org.thymeleaf.spring5.context.SpringContextUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 * @author yhy
 * @create 2019-11-04 10:03
 */
@Data
public class MetricMap implements Serializable {
    private static final long serialVersionUID = -3470217368967109207L;

    private String metric;

    private List<String> queryMetric;

    private String formula;

    private List<Double> ratioList;

    private List<Double> values;
    private List<List<Double>> valueLists;

    private Long timestamp;

    //是否是公式
    private boolean withFormula;

    //是否带系数
    private boolean withRatio;
    //单侧点（或者公式中全部测点）超时
    private boolean isExpired = false;

    //公式中 部分测点超时
    private boolean partExpired = false;

    //公式中 部分测点有值  部分测点有值为 true
    private boolean partValues = false;

    public MetricMap(@NotNull String pointId, Map<String, Double> map) {

        final String FORMULA = "#";
        final String RATIO = "\\[";
        final char RATIOCHAR = '[';
        final String COMMA = ",";
        this.metric = pointId;
        queryMetric = new ArrayList<>();
        if (pointId.contains(FORMULA)) {
            withFormula = true;
            String[] split = pointId.split(FORMULA);
            List<String> points = Arrays.asList(split[0].split(COMMA));
            formula = split[1];
            ratioList = new ArrayList<>();
            for (String point : points) {
                String[] source = point.split(RATIO);
                String dataSource = source[0];
                queryMetric.add(dataSource);
                if (source.length > 1) {
                    String typeId = source[1].split("\\]")[0];
                    ratioList.add(map.get(typeId));
                }

            }
            //带有 # 且带有 [
            if (pointId.indexOf(RATIOCHAR) > 0) {
                withRatio = true;
            } else {
                withRatio = false;
                ratioList = null;
            }
        } else {
            //没有 # 且带有 [
            if (pointId.indexOf(RATIOCHAR) > 0) {
                ratioList = new ArrayList<>();
                withRatio = true;
                String[] source = pointId.split(RATIO);
                String dataSource = source[0];
                queryMetric.add(dataSource);
                if (source.length > 1) {
                    String typeId = source[1].split("\\]")[0];
                    ratioList.add(map.get(typeId));
                }
            } else {
                withRatio = false;
                ratioList = null;
                //普通测点
                queryMetric.add(pointId);
            }
            withFormula = false;
        }

    }

    public void setValues(Double value) {
        if (this.values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
    }

    public void setValueList(List<Double> temp) {
        this.values = temp;
    }
}
