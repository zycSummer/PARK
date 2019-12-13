package com.jet.cloud.deepmind.service.scheduler;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.exception.AlarmConditionNotConfiguredException;
import com.jet.cloud.deepmind.exception.AlarmNotConfiguredException;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.entity.AlarmCondition;
import com.jet.cloud.deepmind.entity.AlarmMsg;
import com.jet.cloud.deepmind.enums.AlarmEnum;
import com.jet.cloud.deepmind.model.AlarmMsgVO;
import com.jet.cloud.deepmind.repository.AlarmMsgRepo;
import com.jet.cloud.deepmind.repository.AlarmRepo;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.SampleDataResponse;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.AlarmSender;
import com.jet.cloud.deepmind.service.CommonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNullOrEmpty;

/**
 * @author yhy
 * @create 2019-11-14 11:39
 */
@Service
@Log4j2
public class AlarmSchedulerTask {


    @Autowired
    private AlarmRepo alarmRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private CommonService commonService;
    @Autowired
    private AlarmMsgRepo alarmMsgRepo;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AlarmSender alarmSender;

    public void check() {

        try {
            List<Alarm> checkAlarmList = alarmRepo.findByIsUseTrue();
            //Long timeOutValue = commonService.getTimeOutValue();
            Long timeOutValue = commonService.getTimeOutValue();
            List<Alarm> alarmList = new ArrayList<>();
            List<Alarm> recoveryList = new ArrayList<>();

            for (Alarm alarm : checkAlarmList) {
                boolean hasRecoveryTime = true;
                for (AlarmMsg alarmMsg : alarm.getAlarmMsgSet()) {
                    if (alarmMsg.getRecoveryTime() == null) {
                        hasRecoveryTime = false;
                        break;
                    }
                }

                try {
                    boolean conditionAlarm = produceAlarm(alarm, timeOutValue);
                    if (hasRecoveryTime && conditionAlarm) {
                        //满足报警条件 且 报警条件满足 数据库插入一条数据，且发送邮件和短信
                        alarm.setAlarmTime(LocalDateTime.now());
                        alarmList.add(alarm);
                    } else if (!hasRecoveryTime && !conditionAlarm) {
                        //没有回复时间 且 报警条件不满足 填入回复时间
                        recoveryList.add(alarm);
                    }
                } catch (AlarmNotConfiguredException | AlarmConditionNotConfiguredException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }

            handleAlarmList(alarmList);
            handleRecoveryAlarmList(recoveryList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 没有回复时间 且 报警条件不满足 填入回复时间更新到数据库
     */
    public void handleRecoveryAlarmList(List<Alarm> recoveryList) {

        if (isNullOrEmpty(recoveryList)) {
            log.info("本轮没有已恢复的报警");
            return;
        }

        String sql = "UPDATE tb_obj_alarm_msg " +
                "SET recovery_time = NOW( ) " +
                ",update_time = NOW( )" +
                ",update_user_id = ?" +
                " WHERE " +
                "obj_type = ? " +
                "AND obj_id = ? " +
                "AND alarm_id = ? " +
                "AND recovery_time IS NULL;";
        jdbcTemplate.batchUpdate(sql, recoveryList, 1000, (ps, alarm) -> {
            ps.setString(1, "system");
            ps.setString(2, alarm.getObjType());
            ps.setString(3, alarm.getObjId());
            ps.setString(4, alarm.getAlarmId());
        });
        log.info("本轮已恢复的报警数为{}", recoveryList.size());
        List<AlarmMsgVO> alarmMsgVOS = new ArrayList<>();
        // 发送中软报警信息
        for (Alarm alarm : recoveryList) {
            AlarmMsgVO alarmMsgVO = new AlarmMsgVO();
            alarmMsgVO.setParkId(alarm.getObjId());
            alarmMsgVO.setAlarmId(alarm.getAlarmId());
            alarmMsgVO.setAlarmName(alarm.getAlarmName());
            alarmMsgVO.setAlarmType(alarm.getAlarmType());
            alarmMsgVO.setAlarmMsg(alarm.getAlarmMsg());
            alarmMsgVO.setAlarmMsg(alarm.getAlarmMsg());
            alarmMsgVO.setAlarmTime(DateUtil.localDateTimeToLong(alarm.getAlarmTime()));
            alarmMsgVO.setRecoveryTime(DateUtil.localDateTimeToLong(LocalDateTime.now()));
            alarmMsgVOS.add(alarmMsgVO);
        }
        alarmSender.sendHttpAlarm(alarmMsgVOS);
    }

    /**
     * 满足报警条件 且 报警条件满足 数据库插入一条数据，且发送邮件和短信
     */
    private void handleAlarmList(List<Alarm> alarmList) {

        if (isNullOrEmpty(alarmList)) {
            log.info("本轮没有满足条件的报警，检查结束");
            return;
        }

        //入库
        insertAlarm2DB(alarmList);
        Multimap<String, Alarm> phoneMap = ArrayListMultimap.create();
        Multimap<String, Alarm> emailMap = ArrayListMultimap.create();
        List<AlarmMsgVO> alarmMsgVOS = new ArrayList<>();
        //短信或邮件
        for (Alarm alarm : alarmList) {
            for (String addr : alarm.getMailRecv()) {
                if (StringUtils.isEmail(addr)) {
                    emailMap.put(addr, alarm);
                }
            }
            for (String addr : alarm.getMsgRecv()) {
                if (StringUtils.isMobilePhone(addr)) {
                    phoneMap.put(addr, alarm);
                }
            }
            AlarmMsgVO alarmMsgVO = new AlarmMsgVO();
            alarmMsgVO.setParkId(alarm.getObjId());
            alarmMsgVO.setAlarmId(alarm.getAlarmId());
            alarmMsgVO.setAlarmName(alarm.getAlarmName());
            alarmMsgVO.setAlarmType(alarm.getAlarmType());
            alarmMsgVO.setAlarmMsg(alarm.getAlarmMsg());
            alarmMsgVO.setAlarmMsg(alarm.getAlarmMsg());
            alarmMsgVO.setAlarmTime(DateUtil.localDateTimeToLong(alarm.getAlarmTime()));
            alarmMsgVO.setRecoveryTime(null);
            alarmMsgVOS.add(alarmMsgVO);
        }
        alarmSender.emailSend(emailMap);
        alarmSender.smsSend(phoneMap);
        // 发送中软报警信息
        alarmSender.sendHttpAlarm(alarmMsgVOS);
    }

    /**
     * 插入报警信息
     */
    @Async
    public void insertAlarm2DB(List<Alarm> alarmList) {
        String sql = "INSERT INTO tb_obj_alarm_msg ( " +
                "obj_type, obj_id, alarm_id, alarm_name, alarm_type, alarm_msg, alarm_time, is_ack, create_user_id, create_time ) " +
                " VALUES " +
                "( ?,?,?,?,?,?,NOW(),'N',?,NOW())";
        jdbcTemplate.batchUpdate(sql, alarmList, 1000, (ps, alarm) -> {
            ps.setString(1, alarm.getObjType());
            ps.setString(2, alarm.getObjId());
            ps.setString(3, alarm.getAlarmId());
            ps.setString(4, alarm.getAlarmName());
            ps.setString(5, alarm.getAlarmType());
            ps.setString(6, alarm.getAlarmMsg());
            ps.setString(7, "system");
        });
        log.info("本轮产生的报警数为{}", alarmList.size());
    }

    /**
     * 根据条件产生报警
     */
    private boolean produceAlarm(Alarm alarm, Long timeOutValue) throws AlarmConditionNotConfiguredException, AlarmNotConfiguredException {
        List<AlarmCondition> alarmConditionList = CommonUtil.setToArrayList(alarm.getAlarmConditionSet());
        if (alarmConditionList == null || alarmConditionList.size() == 0) {
            log.error("{}-{}-{}-{}:报警未配置报警条件", alarm.getObjType(), alarm.getObjId()
                    , alarm.getAlarmId(), alarm.getAlarmName());
            throw new AlarmNotConfiguredException(alarm, "报警未配置报警条件");
        } else {
            if (alarmConditionList.size() == 1) {
                //对于只有一个报警条件的，如果满足则产生报警
                AlarmCondition condition = alarmConditionList.get(0);
                alarm.setNeedAlarmConditionList(condition);
                return checkAlarmCondition(condition, timeOutValue);
            } else {
                //多个条件满足就产生报警
                //String logicSymbol = Objects.equals(alarm.getMultiConditionsLogic(), "AND") ? "&&" : "||";

                //“与”条件
                if (Objects.equals(alarm.getMultiConditionsLogic(), "AND")) {
                    for (AlarmCondition condition : alarmConditionList) {
                        boolean alarmResult = checkAlarmCondition(condition, timeOutValue);
                        if (!alarmResult) {
                            alarm.setNeedAlarmConditionList(null);
                            return false;
                        } else {
                            alarm.setNeedAlarmConditionList(condition);
                        }
                    }
                    return true;
                } else {
                    //“或”条件
                    for (AlarmCondition condition : alarmConditionList) {
                        boolean alarmResult = checkAlarmCondition(condition, timeOutValue);
                        if (alarmResult) {
                            alarm.setNeedAlarmConditionList(condition);
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
    }

    /**
     * 检查报警条件
     */
    private boolean checkAlarmCondition(AlarmCondition condition, Long timeOutValue) throws
            AlarmConditionNotConfiguredException {
        String condition1Op = Objects.equals(condition.getCondition1Op(), "=") ? "==" : condition.getCondition1Op();
        Double condition1Value = condition.getCondition1Value();

        String dataSource = condition.getDataSource();

        String condition2Op = Objects.equals(condition.getCondition2Op(), "=") ? "==" : condition.getCondition2Op();
        Double condition2Value = condition.getCondition2Value();

        String logicSymbol = condition.getConditionsLogic();

        if (isNullOrEmpty(condition1Op, condition1Value)) {
            log.error("{}-{}-{}-{}:报警条件未配置表达式", condition.getObjType(), condition.getObjId()
                    , condition.getAlarmId(), condition.getAlarmConditionId());
            throw new AlarmConditionNotConfiguredException(condition, "报警条件未配置表达式");
        }

        if (isNullOrEmpty(dataSource)) {
            log.error("{}-{}-{}-{}:报警条件未配置数据源", condition.getObjType(), condition.getObjId()
                    , condition.getAlarmId(), condition.getAlarmConditionId());
            throw new AlarmConditionNotConfiguredException(condition, "报警条件未配置数据源");
        }

        AlarmEnum.ValueType valueType = AlarmEnum.ValueType.valueOf(condition.getValueType());
        //最新值、最新值时间、当日差值、当月差值、当年差值、当前时间减去最新值时间

        SampleData4KairosResp diffValue = null;
        boolean isTimeValue;
        Long timestamp = null;

        SampleDataResponse queryLast;
        LocalDateTime start;
        LocalDateTime end;
        TimeUnit unit;

        switch (valueType) {
            case LAST_VALUE:
                queryLast = kairosdbClient.queryLast(dataSource, timeOutValue);
                diffValue = new SampleData4KairosResp();
                diffValue.setValues(Lists.newArrayList(queryLast.getValue()));
                isTimeValue = false;
                break;
            case TODAY_DIFF:
                start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
                end = start.plusDays(1);
                unit = TimeUnit.DAYS;
                diffValue = kairosdbClient.queryDiff(dataSource, start, end, 1, unit);
                isTimeValue = false;
                break;
            case THIS_MONTH_DIFF:
                start = LocalDateTime.of(LocalDate.now().withMonth(1), LocalTime.of(0, 0));
                end = start.plusMonths(1);
                unit = TimeUnit.MONTHS;
                diffValue = kairosdbClient.queryDiff(dataSource, start, end, 1, unit);
                isTimeValue = false;
                break;
            case THIS_YEAR_DIFF:
                start = LocalDateTime.of(LocalDate.now().withDayOfYear(1), LocalTime.of(0, 0));
                end = start.plusYears(1);
                unit = TimeUnit.YEARS;
                diffValue = kairosdbClient.queryDiff(dataSource, start, end, 1, unit);
                isTimeValue = false;
                break;
            case LAST_VALUE_TIMESTAMP:
                queryLast = kairosdbClient.queryLast(dataSource, timeOutValue);
                timestamp = queryLast.getTimestamp();
                isTimeValue = true;
                break;
            case NOW_MINUS_LAST_VALUE_TIMESTAMP:
                queryLast = kairosdbClient.queryLast(dataSource, timeOutValue);
                timestamp = System.currentTimeMillis() - (null == queryLast.getTimestamp() ? 0 : queryLast.getTimestamp());
                isTimeValue = true;
                break;
            default:
                log.error("{}-{}-{}-{}:报警条件数值类型配置错误", condition.getObjType(), condition.getObjId()
                        , condition.getAlarmId(), condition.getAlarmConditionId());
                throw new AlarmConditionNotConfiguredException(condition, "报警条件数值类型配置错误");

        }
        logicSymbol = Objects.equals(logicSymbol, "AND") ? "&&" : "||";
        if (isTimeValue) {
            condition.setVal(timestamp);
            return calByCondition(timestamp, condition1Op, condition1Value, condition2Op, condition2Value, logicSymbol);
        } else {

            boolean diffValueExist = diffValue != null && diffValue.getValues() != null
                    && diffValue.getValues().size() == 1 && diffValue.getValues().get(0) != null;
            Double val;
            //对 == null 做特殊处理
            if (Objects.equals("==", condition1Op) && !diffValueExist) {
                val = null;
                condition.setVal(null);
                return calByCondition(val, condition1Op, condition1Value, condition2Op, condition2Value, logicSymbol);
            }
            if (!diffValueExist) {
                return false;
            }
            val = diffValue.getValues().get(0);
            condition.setVal(val);
            return calByCondition(val, condition1Op, condition1Value, condition2Op, condition2Value, logicSymbol);
        }
    }


    /**
     * 计算 报警表达式   2 == 0 && 2 != 5
     *
     * @param value           待计算的值 2
     * @param condition1Op    条件1条件 == [其他关系操作符]
     * @param condition1Value 条件1值 0
     * @param condition2Op    条件2条件 != [其他关系操作符]
     * @param condition2Value 条件2值 5
     * @param logicSymbol     两个条件之间的 关系运算符 && [||]
     * @return
     */
    private boolean calByCondition(Object value, String condition1Op, Object condition1Value, String condition2Op
            , Object condition2Value, String logicSymbol) {
        String expression = value + condition1Op + condition1Value;
        boolean flag1 = MathUtil.calByBoolean(expression);

        if (isNullOrEmpty(logicSymbol, condition2Op, condition2Value)) {
            return flag1;
        }
        expression = value + condition2Op + condition2Value;
        boolean flag2 = MathUtil.calByBoolean(expression);
        return MathUtil.calByBoolean(flag1 + logicSymbol + flag2);
    }
}
