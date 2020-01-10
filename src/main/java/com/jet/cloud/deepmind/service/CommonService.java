package com.jet.cloud.deepmind.service;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.entity.Site;
import com.jet.cloud.deepmind.model.CalcPointsVO;
import com.jet.cloud.deepmind.model.CombinePointVO;
import com.jet.cloud.deepmind.model.ComprehensiveShowVO;
import com.jet.cloud.deepmind.model.Response;
import javassist.NotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/23 18:04
 * @desc 公共service
 */
public interface CommonService {
    Response queryParkOrSite();

    Response getCurrentClickButtons(String menuId);

    Long getTimeOutValue();

    /**
     * 根据 类型 和 id 获取 park 或者 site的名称
     *
     * @param objType 类型
     * @param objId   id
     * @return
     */
    String getObjNameByObjTypeAndObjId(String objType, String objId);

    /**
     * 输入 时间戳集合 和 值 集合 找出 其中的最大值 最大值时间 最小值 最小值时间 平均值
     */
    CalcPointsVO getMathHandlePoints(List<Long> timestamps, List<Double> values);

    /**
     * 获取当前用户可以看见的所有site集合
     *
     * @return
     */
    List<Site> getCurrentUserSiteList();

    List<String> getCurrentUserSiteIdList();

    /**
     * 拼接 企业测点 兼容 能源种类 是标煤的 和不是标煤的 两种
     *
     * @param siteId
     * @param energyTypeId 如果是具体能源类型 则传参，否则传null
     * @return
     * @throws NotFoundException
     */
    List<CombinePointVO> getSiteCombinePointList(String siteId, String energyTypeId) throws NotFoundException;

    <T> void addChild(T t, Multimap<String, T> dataMultimap, int size);

    /**
     * 获取当前用户可以看见的在线所有企业
     */
    Response getAllCurrentSiteResp(String key);

    /**
     * 获取当前用户可以看见的在线所有企业
     */
    List<ComprehensiveShowVO> getAllCurrentSite(String key);

    /**
     * 查询除标煤之外的能源种类
     *
     * @return
     */
    Response queryEnergyTypes();

    /**
     * SELECT rtdb_tenant_id FROM `tb_park` LIMIT 1
     */
    String getRtdbTenantId();

    /**
     * 查询全部的能源种类
     *
     * @return
     */
    Response queryEnergyTypesAll();

    /**
     * @param key
     * @param energyTypeId
     * @return
     * @apiNote 按仪表标识或者名称查询
     */
    Response queryEnergyParaIdOrEnergyParaName(String key, String energyTypeId);

    /**
     * 模板文件下载
     *
     * @param fileName
     * @param response
     */
    void download(String fileName, HttpServletResponse response);

    /**
     * 添加thisPeriodDiscardMinutesBeforeNow
     */
    List<Double> queryHistoryData(List<Double> values, List<Long> timestamps);
}
