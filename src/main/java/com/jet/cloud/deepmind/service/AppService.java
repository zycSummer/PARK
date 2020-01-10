package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.common.AppResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhuyicheng
 * @create 2019/12/23 10:40
 * @desc AppService
 */
public interface AppService {
    AppResult login(String userId, String password, HttpServletRequest request);

    AppResult logout(HttpServletRequest request);

    AppResult changePwd(String userId, String oldPassword, String newPassword, HttpServletRequest request);

    AppResult objList(HttpServletRequest request);

    AppResult objEnergySummary(String objType, String objId, String timeType, Long timeValue, HttpServletRequest request);

    AppResult objEnergyRank(String objType, String objId, String energyType, String timeType, Long timeValue, HttpServletRequest request);

    AppResult objEnergyAnalysis(String objType, String objId, String energyType, String timeType, Long timeValue, HttpServletRequest request);

    AppResult objNoticeInfo(String objType, String objId, Long noticeTime, HttpServletRequest request);

    AppResult objNoticeList(String objType, String objId, HttpServletRequest request);

    AppResult objAlarmInfo(String objType, String objId, String alarmId, Long alarmTime, HttpServletRequest request);

    AppResult objAlarmList(String objType, String objId, Long startTime, Long endTime, HttpServletRequest request);
}
