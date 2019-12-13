package com.jet.cloud.deepmind.common;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author yhy
 * @create 2019-10-09 11:48
 */
public interface Constants {

    /**
     * 用户相关
     */
    String SESSION_USER_ID = "user_in_session";

    ObjectMapper mapper = new ObjectMapper();

    String USER_NAME_LOGIN = "user_name_login";
    String SESSION_USER_NAME = "user_name_in_session";
    String SESSION_LAST_LOGIN_TIME = "last_login_time_session";

    String CURRENT_USER_MENUS = "current_user_menus";
    String CURRENT_USER_BUTTONS = "current_user_buttons";


    /**
     * | 分隔符
     */
    String SYMBOL_SPLIT = "|";
    /**
     * 平台平层
     */
    String SESSION_PLATFORM_NAME = "session_platform_name";

    /**
     * 查询采集最小时间间隔分钟数
     */
    Integer MINIMUM_MINUTE = 5;

    String OBJ_TYPE_SITE = "SITE";
    String OBJ_TYPE_PARK = "PARK";

    String ENERGY_TYPE_ELECTRICITY = "electricity";
    String ENERGY_TYPE_WATER = "water";
    String ENERGY_TYPE_STEAM = "steam";
    String ENERGY_TYPE_STD_COAL = "std_coal";

}
