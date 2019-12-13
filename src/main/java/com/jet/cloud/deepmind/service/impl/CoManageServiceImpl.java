package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.CoManageVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.DataSourceRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.DataPointResult;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CoManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.jet.cloud.deepmind.common.Constants.ENERGY_TYPE_STD_COAL;

/**
 * @author maohandong
 * @create 2019/10/28 9:58
 */

@Service
public class CoManageServiceImpl implements CoManageService {

    private static final Logger logger = LoggerFactory.getLogger(CoManageServiceImpl.class);

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Autowired
    private DataSourceRepo dataSourceRepo;

    @Autowired
    private KairosdbClient kairosdbClient;

    private final Integer INTERVAL = 1;

    //    time 2019-10 timeType month
    @Override
    public Response getData(String objType, String objId, String time, String timeType) {
        long start;
        long end;
        TimeUnit unit;
//        使用默认时区和区域设置获取日历。通过该方法生成Calendar对象
        Calendar cal = Calendar.getInstance();
        try {
            if ("year".equals(timeType)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy");
                Date beginTime = format.parse(time);
                start = beginTime.getTime();
                cal.setTime(beginTime);
                cal.add(Calendar.YEAR, 1);
                Date endTime = cal.getTime();
                end = endTime.getTime();
                unit = TimeUnit.MONTHS;
            } else {
//                timeType是"month",按下面格式解析
//                SimpleDateFormat(String pattern)：用指定的格式和默认的语言环境构造 SimpleDateFormat。
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
//                parse():将String的对象根据 模板提供的yyyy-MM进行转化成为Date类型
                Date beginTime = format.parse(time);
                start = beginTime.getTime();
//                使用给定的Date设置此日历的时间  Date------Calendar
                cal.setTime(beginTime);
//                按照日历的规则，给指定字段添加或减少时间量
                cal.add(Calendar.MONTH, 1);
//                返回一个Date表示此日历的时间
                Date endTime = cal.getTime();
                end = endTime.getTime();
                unit = TimeUnit.DAYS;
            }
            List<SysEnergyType> energyTypes = sysEnergyTypeRepo.findAllByEnergyTypeIdNot(ENERGY_TYPE_STD_COAL);
            List<String> pointIds = new ArrayList<>();
            HashMap<String, Double> co2Coeffs = new HashMap<>();
            for (SysEnergyType energyType : energyTypes) {
                String energyTypeId = energyType.getEnergyTypeId();
                String energyUsageParaId = energyType.getEnergyUsageParaId();
                Double co2Coeff = energyType.getCo2Coeff();
                String dataSource = null;
                DataSource dataSourceObj = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, energyTypeId, energyUsageParaId);
                if (dataSourceObj != null) {
                    dataSource = dataSourceObj.getDataSource();
                    pointIds.add(dataSource);
                    co2Coeffs.put(dataSource, co2Coeff);
                } else {
                    logger.info(energyType.getEnergyTypeName() + "没有数据源：{}", dataSource);
                }
            }
            AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryDiff(pointIds, start, end, INTERVAL, unit);
            List<DataPointResult> values = aggregatorDataResponse.getValues();
            for (DataPointResult value : values) {
                Double co2 = co2Coeffs.get(value.getMetricName());
                List<Double> pointValues = new ArrayList<>();
                for (int i = 0; i < value.getValues().size(); i++) {
                    if (value.getValues().get(i) == null) {
                        pointValues.add(value.getValues().get(i));
                    } else {
                        pointValues.add(value.getValues().get(i) * co2);
                    }
                }
                value.setValues(pointValues);
            }
            List<Long> timestamps = aggregatorDataResponse.getTimestamps();
            List<Double> valueListResult = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                List<Double> valueArrList = new ArrayList<>();
                for (DataPointResult value : values) {
                    valueArrList.add(value.getValues().get(i));
                }
                List<Double> valueListResultCal = new ArrayList<>();
                Boolean flag = true;
                for (Double double1 : valueArrList) {
                    if (double1 != null) {
                        flag = false;
                    }
                }
                if (flag) {
                    valueListResult.add(null);
                } else {
                    for (Double double1 : valueArrList) {
                        if (double1 == null) {
                            valueListResultCal.add(0.00);
                        } else {
                            valueListResultCal.add(double1);
                        }
                    }
                    Double sum = 0.00;
                    for (int i1 = 0; i1 < valueListResultCal.size(); i1++) {
                        sum = valueListResultCal.get(i1) + sum;
                    }
                    valueListResult.add(sum);
                }
            }
            DecimalFormat df = new DecimalFormat("#0.00");
            CoManageVO coManageVO = new CoManageVO();
            ArrayList<Double> value = new ArrayList<>();
            for (int i = 0; i < valueListResult.size(); i++) {
                value.add(valueListResult.get(i) == null ? null : Double.parseDouble(df.format(valueListResult.get(i))));
            }
            coManageVO.setValue(value);
            ArrayList<Integer> date = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                date.add(i + 1);
            }
            coManageVO.setDate(date);
            Response ok = Response.ok(coManageVO);
            ok.setQueryPara(objType, objId, time, timeType);
            return ok;
        } catch (ParseException e) {
            e.printStackTrace();
            Response error = Response.error("操作失败");
            error.setQueryPara(objType, objId, time, timeType);
            return error;
        }
    }

}

