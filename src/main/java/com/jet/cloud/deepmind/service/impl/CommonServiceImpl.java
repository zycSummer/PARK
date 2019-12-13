package com.jet.cloud.deepmind.service.impl;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.MenuMappingRoleService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.jet.cloud.deepmind.common.Constants.*;
import static com.jet.cloud.deepmind.common.Constants.ENERGY_TYPE_STD_COAL;
import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author zhuyicheng
 * @create 2019/10/23 18:04
 * @desc 公共ServiceImpl
 */
@Service
public class CommonServiceImpl implements CommonService {
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private CommonRepo commonRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private SiteRepo siteRepo;
    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private MenuMappingRoleService menuMappingRoleService;
    @Autowired
    private MenuFunctionMappingRoleRepo menuFunctionMappingRoleRepo;
    @Autowired
    private SysParameterRepo sysParameterRepo;
    @Autowired
    private UserGroupMappingObjRepo userGroupMappingObjRepo;
    @Autowired
    private MeterRepo meterRepo;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;


    @Override
    public Response queryParkOrSite() {
        try {
            /**
             * 第一部分为对象选择，后面才是具体的菜单、子菜单
             *
             * 点击第一部分，出现对象选择下拉框，可选项的获取逻辑如下：         *
             * 1.  查询用户所属的用户组所关联的园区
             * 根据当前用户所属的用户组标识去tb_sys_user_group_mapping_obj表中查询obj_type = 'PARK'，并且obj_id存在于tb_park表中park_id字段中 的记录
             *
             * 如果有结果，则此对象选择的第一级固定展示为“园区”两个字，其具有两个属性：
             *  obj_type = 'PARK'
             *  obj_id = 查询出的park_id
             */
            String userGroupId = currentUser.userGroupId();
            List<ComprehensiveShowVO> userGroupParks = commonRepo.queryParkDetails(userGroupId);

            /**
             * 2. 查询用户所属的用户组所关联的企业
             * 根据当前用户所属的用户组标识去tb_sys_user_group_mapping_obj表中查询obj_type = 'SITE'，并且obj_id存在于tb_site表中site_id字段中(且tb_site表中is_online = 'Y' ) 的记录，按照tb_site表中sort_id排序（升序）
             *
             * 如果第一步中查询有结果，则第二步结果的site_name列表作为下拉选择框可选项的第二级；
             * 如果第一步中查询没有结果，则第二步结果的site_name列表作为下拉选择框可选项的第一级。
             *
             * 第二步的结果同样具有两个属性
             * obj_type = SITE'
             * obj_id = 查询出的site_id
             */
            Response ok = OKResponse.ok();
            ok.setQueryPara("查询导航栏");
            List<ComprehensiveShowVO> userGroupSites = commonRepo.querySiteDetails(userGroupId);
            List<NavigationSiteVO> navigationVOSites = new ArrayList<>();
            List<NavigationParkVO> navigationVOParks = new ArrayList<>();
            if (userGroupParks != null && !userGroupParks.isEmpty()) {
                for (ComprehensiveShowVO userGroupPark : userGroupParks) {
                    NavigationParkVO navigationParkVO = new NavigationParkVO();
                    navigationParkVO.setTitle("园区");
                    navigationParkVO.setName(userGroupPark.getParkName());
                    navigationParkVO.setId(userGroupPark.getObjId());
                    navigationParkVO.setType(userGroupPark.getObjType());
                    navigationParkVO.setLatitude(userGroupPark.getLatitude());
                    navigationParkVO.setLongitude(userGroupPark.getLongitude());
                    navigationParkVO.setScale(userGroupPark.getScale());
                    navigationParkVO.setRtdbProjectId(userGroupPark.getRtdbTenantId());
                    if (userGroupSites != null && !userGroupSites.isEmpty()) {
                        navigationParkVO.setSpread(true);
                        for (ComprehensiveShowVO userGroupSite : userGroupSites) {
                            NavigationSiteVO navigationSiteVO = new NavigationSiteVO();
                            String objId = userGroupSite.getObjId();
                            navigationSiteVO.setId(objId);
                            navigationSiteVO.setTitle(userGroupSite.getSiteName());
                            navigationSiteVO.setType(userGroupSite.getObjType());
                            navigationSiteVO.setLatitude(userGroupSite.getLatitude());
                            navigationSiteVO.setLongitude(userGroupSite.getLongitude());
                            navigationSiteVO.setRtdbProjectId(userGroupSite.getRtdbProjectId());
                            navigationVOSites.add(navigationSiteVO);
                        }
                    }
                    navigationParkVO.setChildren(navigationVOSites);
                    navigationVOParks.add(navigationParkVO);
                }
                ok.setData(navigationVOParks);
                return ok;
            } else {
                if (userGroupSites != null && !userGroupSites.isEmpty()) {
                    for (ComprehensiveShowVO userGroupSite : userGroupSites) {
                        NavigationSiteVO navigationVO = new NavigationSiteVO();
                        navigationVO.setTitle(userGroupSite.getSiteName());
                        navigationVO.setId(userGroupSite.getObjId());
                        navigationVO.setType(userGroupSite.getObjType());
                        navigationVO.setLatitude(userGroupSite.getLatitude());
                        navigationVO.setLongitude(userGroupSite.getLongitude());
                        navigationVO.setRtdbProjectId(userGroupSite.getRtdbProjectId());
                        navigationVOSites.add(navigationVO);
                    }
                    ok.setData(navigationVOSites);
                    return ok;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error("查询导航栏错误");
            error.setQueryPara("查询导航栏");
            log.error("查询导航栏错误,e={}", e.getMessage());
            return error;
        }
        return null;
    }

    @Override
    public Response getCurrentClickButtons(String menuId) {

        List<String> roleIds = menuMappingRoleService.getRoleIdsByUserId(currentUser.userId());
        List<MenuVO> result = new ArrayList<>();
        for (MenuFunctionMappingRole temp : menuFunctionMappingRoleRepo.findByRoleIdInAndMenuId(roleIds, menuId)) {
            result.add(new MenuVO(temp.getSysMenuFunction()));
        }
        Response ok = Response.ok(result);
        ok.setQueryPara("获取当前用户 角色集 对应的按钮", menuId);
        return ok;
    }

    @Override
    public Long getTimeOutValue() {
        SysParameter equipTimeOut = sysParameterRepo.findByParaId("LastValueTimeOut");
        if (equipTimeOut != null) {
            String paraValue = equipTimeOut.getParaValue();
            if (paraValue != null) {
                Long parameterValue = Long.parseLong(paraValue);
                if (parameterValue >= 0) {
                    parameterValue = parameterValue * 60 * 1000; // 分钟数转化为毫秒数*/
                    return parameterValue;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getObjNameByObjTypeAndObjId(String objType, String objId) {

        if (StringUtils.isNullOrEmpty(objId, objType)) {
            return null;
        }
        switch (objType) {
            case OBJ_TYPE_SITE:
                Site obj = siteRepo.findBySiteId(objId);
                return obj == null ? null : obj.getSiteName();
            case OBJ_TYPE_PARK:
                Park p = parkRepo.findByParkId(objId);
                return p == null ? null : p.getParkName();
            default:
                return null;
        }
    }

    @Override
    public CalcPointsVO getMathHandlePoints(List<Long> timestamps, List<Double> values) {
        CalcPointsVO res = new CalcPointsVO();
        if (values != null && timestamps != null && values.size() == timestamps.size() && StringUtils.isNotNullAndEmpty(values)) {
            Double max = null;
            LocalDateTime maxTime = null;
            LocalDateTime minTime = null;
            Double min = null;
            Double sum = null;

            int index = 0;
            for (Double value : values) {

                LocalDateTime time = DateUtil.longToLocalTime(timestamps.get(index));
                if (max == null) {
                    max = value;
                    min = value;
                    maxTime = time;
                    minTime = time;
                }

                if (value != null) {
                    if (sum == null) {
                        sum = 0d;
                    }
                    sum += value;

                    if (value > max) {
                        max = value;
                        maxTime = time;
                    }
                    if (value < min) {
                        min = value;
                        minTime = time;
                    }
                }
                index++;
            }
            res.setAvg(MathUtil.double2String(sum == null ? null : sum / values.size()));
            res.setMaxTime(maxTime);
            res.setMinTime(minTime);
            res.setMaxVal(MathUtil.double2String(max));
            res.setMinVal(MathUtil.double2String(min));
        }
        return res;
    }

    @Override
    public List<Site> getCurrentUserSiteList() {
        List<String> siteIdList = getCurrentUserSiteIdList();
        if (siteIdList.size() == 0) {
            return new ArrayList<>();
        }
        return siteRepo.findBySiteIdIn(siteIdList);
    }

    @Override
    public List<String> getCurrentUserSiteIdList() {
        List<UserGroupMappingObj> objList = userGroupMappingObjRepo.findAllByUserGroupIdAndObjType(currentUser.userGroupId(), OBJ_TYPE_SITE);

        List<String> siteIdList = new ArrayList<>();
        for (UserGroupMappingObj obj : objList) {
            siteIdList.add(obj.getObjId());
        }
        if (StringUtils.isNullOrEmpty(siteIdList)) return new ArrayList<>();
        return siteRepo.findSiteIdBySiteIdIn(siteIdList);
    }

    /**
     * 2.1 当所选能源种类不是标煤时，即energy_type_id <> 'std_coal' 时:
     * <p>
     * 2.1.1 查询出当前所选能源种类的负荷参数(即tb_sys_energy_type表的energy_load_para_id字段值)
     * SELECT energy_load_para_id FROM `tb_sys_energy_type` where energy_type_id = ?;
     * <p>
     * 2.1.2 查询出当前所选企业符合条件的仪表信息列表
     * select * from `tb_obj_meter` where obj_type = ? AND obj_id = ? AND energy_type_id = ? and is_ranking = 'Y';
     * 其中energy_type_id 设置的值为当前所选的具体能源种类标识。
     * <p>
     * 2.1.3
     * 查询出tb_park表中第一条记录的rtdb_tenant_id字段值
     * 查询出每个仪表所属企业对应的rtdb_project_id字段值
     * 每个仪表标识
     * 负荷参数
     * 将以上4个字段以"."拼接得到每个仪表负荷参数的实时库点名，去实时库查询所有测点最新值，最后将所有仪表的负荷参数从高到低展示出来。
     * <p>
     * 2.2 当所选能源种类是标煤时，即energy_type_id == 'std_coal' 时:
     * <p>
     * 2.2.1 查询出在tb_sys_energy_type表中除标煤外（std_coal）的所有能源种类的负荷参数(即tb_sys_energy_type表的energy_load_para_id字段值)
     * SELECT * FROM `tb_sys_energy_type` where energy_type_id  <> 'std_coal';
     * <p>
     * 2.2.2 查询出当前所选企业符合条件的仪表信息列表
     * select * from `tb_obj_meter` where obj_type = ? AND obj_id = ? AND energy_type_id  in (  SELECT energy_type_id FROM `tb_sys_energy_type` where energy_type_id  <> 'std_coal' ) and is_ranking = 'Y';
     * <p>
     * 2.2.3
     * 查询出tb_park表中第一条记录的rtdb_tenant_id字段值
     * 查询出每个仪表所属企业对应的rtdb_project_id字段值
     * 每个仪表标识
     * 每个仪表所属能源种类对应的负荷参数
     *
     * @param energyTypeId 如果是具体能源类型 则传参，否则传null
     */
    @Override
    public List<CombinePointVO> getSiteCombinePointList(String objId, String energyTypeId) throws NotFoundException {
        List<CombinePointVO> voList = new ArrayList<>();
        String objType = OBJ_TYPE_SITE;
        //具体企业
        if (ENERGY_TYPE_STD_COAL.equals(energyTypeId)) {
            //标煤
            List<SysEnergyType> energyTypeList = sysEnergyTypeRepo.findAllByEnergyTypeIdNot(ENERGY_TYPE_STD_COAL);
            Map<String, String> energyTypeMapLoadPara = new HashMap<>();
            for (SysEnergyType energyType : energyTypeList) {
                energyTypeMapLoadPara.put(energyType.getEnergyTypeId(), energyType.getEnergyLoadParaId());
            }
            if (energyTypeMapLoadPara.size() == 0) {
                throw new NotFoundException("除标煤外（std_coal）的所有能源种类的负荷参数均未配置");
            }
            //查询出当前所选企业符合条件的仪表信息列表
            List<Meter> meterList = meterRepo.findAllByObjTypeAndObjIdAndEnergyTypeIdInAndIsRankingTrue(OBJ_TYPE_SITE, objId
                    , CommonUtil.setToArrayList(energyTypeMapLoadPara.keySet()));

            if (meterList.size() == 0) throw new NotFoundException("[" + objType + "-" + objId + "[：未找到符合条件的仪表信息列表");
            Park park = getPark();
            Map<String, Site> siteIdMapSite = getSiteIdMapSite(meterList);
            String tenantId = park.getRtdbTenantId();
            for (Meter meter : meterList) {
                CombinePointVO vo = new CombinePointVO();
                vo.setMeterName(meter.getMeterName());
                Site site = siteIdMapSite.get(meter.getObjId());
                vo.setPointId(tenantId, site.getRtdbProjectId(), meter.getMeterId(), energyTypeMapLoadPara.get(meter.getEnergyTypeId()));
                voList.add(vo);
            }
        } else {
            //具体能源
            SysEnergyType energyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
            if (energyType == null) {
                throw new NotFoundException("[" + energyTypeId + "]的负荷参数未配置");
            }
            List<Meter> meterList = meterRepo.findAllByObjTypeAndObjIdAndEnergyTypeIdAndIsRankingTrue(OBJ_TYPE_SITE, objId, energyTypeId);

            if (meterList.size() == 0) throw new NotFoundException("[" + objType + "-" + objId + "[：未找到符合条件的仪表信息列表");
            Park park = getPark();
            Map<String, Site> siteIdMapSite = getSiteIdMapSite(meterList);
            String tenantId = park.getRtdbTenantId();
            for (Meter meter : meterList) {
                CombinePointVO vo = new CombinePointVO();
                vo.setMeterName(meter.getMeterName());
                Site site = siteIdMapSite.get(meter.getObjId());
                vo.setPointId(tenantId, site.getRtdbProjectId(), meter.getMeterId(), energyType.getEnergyLoadParaId());
                voList.add(vo);
            }
        }
        return voList;
    }

    private Map<String, Site> getSiteIdMapSite(List<Meter> meterList) {
        List<String> siteIdList = new ArrayList<>();
        for (Meter meter : meterList) {
            siteIdList.add(meter.getObjId());
        }
        Map<String, Site> siteIdMapSite = new HashMap<>();
        for (Site site : siteRepo.findBySiteIdIn(siteIdList)) {
            siteIdMapSite.put(site.getSiteId(), site);
        }
        return siteIdMapSite;
    }

    private Park getPark() throws NotFoundException {
        Park park = parkRepo.findFirstByOrderByParkIdAsc();
        if (park == null || StringUtils.isNullOrEmpty(park.getRtdbTenantId())) {
            throw new NotFoundException("未找到对应园区或对应园区未配置rtdb_tenant_id");
        }
        return park;
    }

    @Override
    public <T> void addChild(T t, Multimap<String, T> dataMultimap, int size) {
        if (t instanceof HtImgVO) {
            HtImgVO htImgVO = (HtImgVO) t;
            if (size > 0 && htImgVO != null) {
                htImgVO.setChildren(new ArrayList<>());
                Collection<HtImgVO> objs = (Collection<HtImgVO>) dataMultimap.get(htImgVO.getHtImgId());
                if (objs.size() > 0) {
                    htImgVO.setSpread(true);
                    for (HtImgVO subModel : objs) {
                        addChild((T) subModel, dataMultimap, --size);
                        htImgVO.getChildren().add(subModel);
                    }
                } else {
                    htImgVO.setChildren(null);
                }
            }
        }
        if (t instanceof OrgTreeDetailVO) {
            OrgTreeDetailVO orgTreeDetailVO = (OrgTreeDetailVO) t;
            if (size > 0 && orgTreeDetailVO != null) {
                orgTreeDetailVO.setChildren(new ArrayList<>());
                Collection<OrgTreeDetailVO> objs = (Collection<OrgTreeDetailVO>) dataMultimap.get(orgTreeDetailVO.getNodeId());
                if (objs.size() > 0) {
                    for (OrgTreeDetailVO subModel : objs) {
                        addChild((T) subModel, dataMultimap, --size);
                        orgTreeDetailVO.getChildren().add(subModel);
                    }
                } else {
                    orgTreeDetailVO.setChildren(null);
                }
            }
        }
        if (t instanceof OrgTreeDetailVOs) {
            OrgTreeDetailVOs orgTreeDetailVOs = (OrgTreeDetailVOs) t;
            if (size > 0 && orgTreeDetailVOs != null) {
                orgTreeDetailVOs.setChildren(new ArrayList<>());
                Collection<OrgTreeDetailVOs> objs = (Collection<OrgTreeDetailVOs>) dataMultimap.get(orgTreeDetailVOs.getNodeId());
                if (objs.size() > 0) {
                    for (OrgTreeDetailVOs subModel : objs) {
                        addChild((T) subModel, dataMultimap, --size);
                        orgTreeDetailVOs.getChildren().add(subModel);
                    }
                } else {
                    orgTreeDetailVOs.setChildren(null);
                }
            }
        }
    }

    @Override
    public Response getAllCurrentSiteResp(String key) {
        try {
            Response ok = Response.ok(getAllCurrentSite(key));
            ok.setQueryPara(key);
            return ok;
        } catch (Exception e) {
            Response error = Response.error(e.getMessage());
            error.setQueryPara(key);
            return error;
        }
    }

    @Override
    public List<ComprehensiveShowVO> getAllCurrentSite(String key) {
        List<ComprehensiveShowVO> comprehensiveShowVOS;
        String userGroupId = currentUser.userGroupId();
        if (StringUtils.isNullOrEmpty(key)) {
            comprehensiveShowVOS = commonRepo.querySiteDetails(userGroupId);
        } else {
            comprehensiveShowVOS = commonRepo.querySiteDetails(userGroupId, key);
        }
        return comprehensiveShowVOS;
    }

    /**
     * 查询除标煤之外的能源种类
     */
    @Override
    public Response queryEnergyTypes() {
        List<SysEnergyType> list = sysEnergyTypeRepo.findAllByEnergyTypeIdNotOrderBySortId(ENERGY_TYPE_STD_COAL);
        Response ok = Response.ok(list);
        ok.setQueryPara("查询除标煤之外的能源种类");
        return ok;
    }

    @Override
    public String getRtdbTenantId() {
        Park park = parkRepo.findFirstPark();
        return park.getRtdbTenantId();
    }

    @Override
    public Response queryEnergyTypesAll() {
        List<SysEnergyType> list = sysEnergyTypeRepo.findAllByOrderBySortId();
        Response ok = Response.ok(list);
        ok.setQueryPara("查询全部的能源种类");
        return ok;
    }

    @Override
    public Response queryEnergyParaIdOrEnergyParaName(String key, String energyTypeId) {
        List<SysEnergyPara> sysEnergyParas;
        if (Objects.equals("", key) || key == null) {
            sysEnergyParas = sysEnergyParaRepo.findByEnergyTypeIdOrderBySortId(energyTypeId);
        } else {
            sysEnergyParas = sysEnergyParaRepo.queryEnergyParaIdOrEnergyParaName("%" + key + "%", energyTypeId);
        }
        Response ok = Response.ok(sysEnergyParas);
        ok.setQueryPara(key, energyTypeId);
        return ok;
    }
}
