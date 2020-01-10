package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.*;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.EnergyMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author zhuyicheng
 * @create 2019/10/25 9:50
 * @desc 用能监测service
 */
@Service
public class EnergyMonitoringServiceImpl implements EnergyMonitoringService {
    private static final Logger log = LoggerFactory.getLogger(EnergyMonitoringServiceImpl.class);

    @Autowired
    private HtImgRepo htImgRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private OrgTreeRepo orgTreeRepo;
    @Autowired
    private OrgTreeDetailRepo orgTreeDetailRepo;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;
    @Autowired
    private SysParameterRepo sysParameterRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private CommonService commonService;
    @Value("${ht_img_file_prefix}")
    private String filePrefix;

    private final String OBJTYPE_ISNULL = "对象类型不能为空";
    private final String OBJID_ISNULL = "对象标识不能为空";
    private final String HTIMGID_ISNULL = "组态画面标识不能为空";
    private final String HTIMGNAME_ISNULL = "组态画面名称不能为空";

    private final Integer VALUE_TYPE_0 = 0;//最新值
    private final Integer VALUE_TYPE_1 = 1;//当日差值
    private final Integer VALUE_TYPE_2 = 2;//当月差值
    private final Integer VALUE_TYPE_3 = 3;//当年差值
    private final Integer VALUE_TYPE_4 = 4;//当日求和
    private final Integer VALUE_TYPE_5 = 5;//当月求和
    private final Integer VALUE_TYPE_6 = 6;//当年求和
    private final String FORMULA_FLAG = "#";

    private final String TREE1 = "/public/images/treeIcon/tree1.png";
    private final String TREE2_1 = "/public/images/treeIcon/tree2-1.png";
    private final String TREE2_2 = "/public/images/treeIcon/tree2-2.png";
    private final String TREE3_1 = "/public/images/treeIcon/tree3-1.png";
    private final String TREE3_2 = "/public/images/treeIcon/tree3-2.png";
    private final String TREE3_3 = "/public/images/treeIcon/tree3-3.png";

    @Override
    public Response queryRightHtImg(String objType, String objId, String htImgId) {
        HtImg htImg = htImgRepo.findByObjTypeAndObjIdAndHtImgId(objType, objId, htImgId);
        if (htImg != null) {
            String filePath = filePrefix + htImg.getFilePath();
            if (filePath != null && !"".equals(filePath)) {
                String result = StringUtils.readToString(filePath);
                htImg.setCfgPic(result);
            }
        }
        Response ok = Response.ok(htImg);
        ok.setQueryPara(objId, objType, htImgId);
        return ok;
    }

    @Override
    public ServiceData insertRightHtImg(HtImg htImg) {
        try {
            String cfgPic = htImg.getCfgPic();
            String objType = htImg.getObjType();
            String objId = htImg.getObjId();
            String htImgId = htImg.getHtImgId();
            String htImgName = htImg.getHtImgName();

            if (objType == null || "".equals(objType)) {
                return ServiceData.error(OBJTYPE_ISNULL, currentUser);
            }
            if (objId == null || "".equals(objId)) {
                return ServiceData.error(OBJID_ISNULL, currentUser);
            }
            if (htImgId == null || "".equals(htImgId)) {
                return ServiceData.error(HTIMGID_ISNULL, currentUser);
            }
            if (htImgName == null || "".equals(htImgName)) {
                return ServiceData.error(HTIMGNAME_ISNULL, currentUser);
            }
            String path = StringUtils.sendFromFile(filePrefix, objType, objId, htImgId, cfgPic, null, null, null, "MENU0401_");
            String fileName = "MENU0401_" + objType + "_" + objId + "_" + htImgId + ".json";
            htImg.setFilePath(fileName);
            htImg.setCreateUserId(currentUser.userId());
            htImg.setCreateNow();
            htImgRepo.save(htImg);
            return ServiceData.success("右侧组态画面新增成功", currentUser);
        } catch (Exception e) {
            log.error("右侧组态画面新增失败,e={}", e.getMessage());
            return ServiceData.error("右侧组态画面新增失败", e, currentUser);
        }
    }

    @Override
    public ServiceData updateRightHtImg(HtImg htImg) {
        try {
            HtImg ht = htImgRepo.findById(htImg.getId()).get();
            String cfgPic = htImg.getCfgPic();
            String objType = htImg.getObjType();
            String objId = htImg.getObjId();
            String htImgId = htImg.getHtImgId();
            String htImgName = htImg.getHtImgName();

            if (objType == null || "".equals(objType)) {
                return ServiceData.error(OBJTYPE_ISNULL, currentUser);
            }
            if (objId == null || "".equals(objId)) {
                return ServiceData.error(OBJID_ISNULL, currentUser);
            }
            if (htImgId == null || "".equals(htImgId)) {
                return ServiceData.error(HTIMGID_ISNULL, currentUser);
            }
            if (htImgName == null || "".equals(htImgName)) {
                return ServiceData.error(HTIMGNAME_ISNULL, currentUser);
            }

            String path = StringUtils.sendFromFile(filePrefix, objType, objId, htImgId, cfgPic, ht.getObjType(), ht.getObjId(), ht.getHtImgId(), "MENU0401_");
            String fileName = "MENU0401_" + objType + "_" + objId + "_" + htImgId + ".json";

            ht.setObjType(objType);
            ht.setObjId(objId);
            ht.setHtImgId(htImgId);
            ht.setHtImgName(htImgName);
            ht.setParentId(htImg.getParentId());
            ht.setFilePath(fileName);
            ht.setSortId(htImg.getSortId());
            ht.setMemo(htImg.getMemo());

            ht.setUpdateUserId(currentUser.userId());
            ht.setUpdateNow();
            htImgRepo.save(ht);
            return ServiceData.success("右侧组态画面更新成功", currentUser);
        } catch (Exception e) {
            log.error("右侧组态画面更新失败,e={}", e.getMessage());
            return ServiceData.error("右侧组态画面更新失败", e, currentUser);
        }
    }

    @Override
    public ServiceData deleteRightHtImg(String objType, String objId, String htImgId) {
        try {
            htImgRepo.deleteRightHtImg(objType, objId, htImgId);
            StringUtils.deleteFile(filePrefix, objType, objId, htImgId, "MENU0401_");
            return ServiceData.success("右侧组态画面删除成功", currentUser);
        } catch (Exception e) {
            log.error("右侧组态画面删除,e={}", e.getMessage());
            return ServiceData.error("右侧组态画面删除失败", e, currentUser);
        }
    }

    /**
     * valueType(0-最新值,1-每日差值,2-每月差值,3-每年差值,4-当日求和，5=当月求和，6=当年求和)
     *
     * @param opNameList
     * @param objId
     * @return
     */
    @Override
    public Map<String, Object> queryActualMonitorData(List<Map<String, Object>> opNameList, String objId, String objType) {
        Map<String, Object> mapAll = new HashMap<>();
        List<String> list0 = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        List<String> list4 = new ArrayList<>();
        List<String> list5 = new ArrayList<>();
        List<String> list6 = new ArrayList<>();

        // 获取超时时间
        try {
            Long parameterValue = commonService.getTimeOutValue();
            for (Map<String, Object> stringObjectMap : opNameList) {
                String opName = stringObjectMap.get("opName").toString();
                Object type = stringObjectMap.get("valueType");
                if (type != null) {
                    Integer valueType = Integer.parseInt(stringObjectMap.get("valueType").toString());
                    judgeList(list0, list1, list2, list3, list4, list5, list6, opName, valueType);
                }
            }
            // 0-最新值
            if (list0 != null && !list0.isEmpty()) {
                List<SampleDataResponse> sampleDataResponses = kairosdbClient.queryLast(list0, parameterValue);
                if (sampleDataResponses != null && !sampleDataResponses.isEmpty()) {
                    for (SampleDataResponse sampleDataRespons : sampleDataResponses) {
                        mapAll.put(sampleDataRespons.getPoint() + VALUE_TYPE_0, sampleDataRespons.getValue());
                    }
                }
            }

            // 1-每日差值
            Long today = DateUtil.getDate(new Date()).getTime();
            Long today24 = DateUtil.initDateByDay24().getTime();
            if (list1 != null && !list1.isEmpty()) {
                AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryDiff(list1, today, today24, 1, TimeUnit.DAYS);
                mapAll = queryDiff(aggregatorDataResponse, VALUE_TYPE_1, mapAll);

            }

            // 2-每月差值
            Long month = DateUtil.firstDayMonth().getTime();
            Long monthNext = DateUtil.getFirstDayOfNextMonth().getTime();
            if (list2 != null && !list2.isEmpty()) {
                AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryDiff(list2, month, monthNext, 1, TimeUnit.MONTHS);
                mapAll = queryDiff(aggregatorDataResponse, VALUE_TYPE_2, mapAll);
            }

            // 3-每年差值
            Long year = DateUtil.getCurrYearFirst().getTime();
            Long yearNext = DateUtil.getNextYearFirst().getTime();
            if (list3 != null && !list3.isEmpty()) {
                AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryDiff(list3, year, yearNext, 1, TimeUnit.YEARS);
                mapAll = queryDiff(aggregatorDataResponse, VALUE_TYPE_3, mapAll);
            }

            // 4-当日求和
            if (list4 != null && !list4.isEmpty()) {
                AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryHis(list4, today, today24, 1, TimeUnit.DAYS);
                mapAll = querySum(aggregatorDataResponse, VALUE_TYPE_4, mapAll);
            }

            // 5=当月求和
            if (list5 != null && !list5.isEmpty()) {
                AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryHis(list5, month, monthNext, 1, TimeUnit.MONTHS);
                mapAll = querySum(aggregatorDataResponse, VALUE_TYPE_5, mapAll);
            }

            // 6=当年求和
            if (list6 != null && !list6.isEmpty()) {
                AggregatorDataResponse aggregatorDataResponse = kairosdbClient.queryHis(list6, year, yearNext, 1, TimeUnit.YEARS);
                mapAll = querySum(aggregatorDataResponse, VALUE_TYPE_6, mapAll);
            }
        } catch (Exception e) {
            log.error("查询实时监测-页面数据失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return mapAll;
    }

    private Map<String, Object> queryDiff(AggregatorDataResponse aggregatorDataResponse, Integer type, Map<String, Object> mapAll) {
        if (aggregatorDataResponse != null) {
            List<DataPointResult> dataPointResults = aggregatorDataResponse.getValues();
            if (dataPointResults != null && !dataPointResults.isEmpty()) {
                for (DataPointResult dataPointResult : dataPointResults) {
                    List<Double> values = dataPointResult.getValues();
                    String metricName = dataPointResult.getMetricName();
                    mapAll.put(metricName + type, values.get(0));
                }
            }
        }
        return mapAll;
    }

    private Map<String, Object> querySum(AggregatorDataResponse aggregatorDataResponse, Integer type, Map<String, Object> mapAll) {
        if (aggregatorDataResponse != null) {
            List<DataPointResult> dataPointResults = aggregatorDataResponse.getValues();
            if (dataPointResults != null && !dataPointResults.isEmpty()) {
                for (DataPointResult dataPointResult : dataPointResults) {
                    List<Double> values = dataPointResult.getValues();
                    String metricName = dataPointResult.getMetricName();
                    mapAll.put(metricName + type, values.get(0));
                }
            }
        }
        return mapAll;
    }

    @Override
    public Response queryHistoryLeftData() {
        /**
         * 1. 能源种类：取系统能源种类表中的配置的所有能源种类
         * （SELECT * FROM tb_sys_energy_type ORDER BY sort_id），
         * 展示 energy_type_name字段。默认选择第一个。
         */
        List<SysEnergyTypeVO> sysEnergyTypeVOS = new ArrayList<>();
        List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAllByOrderBySortId();
        if (sysEnergyTypes != null && !sysEnergyTypes.isEmpty()) {
            for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                String energyTypeId = sysEnergyType.getEnergyTypeId();
                String energyTypeName = sysEnergyType.getEnergyTypeName();
                String energyLoadParaId = sysEnergyType.getEnergyLoadParaId();
                SysEnergyTypeVO sysEnergyTypeVO = new SysEnergyTypeVO(energyTypeId, energyTypeName, energyLoadParaId);
                sysEnergyTypeVOS.add(sysEnergyTypeVO);
            }
        }
        Response ok = Response.ok(sysEnergyTypeVOS);
        ok.setQueryPara("获取能源种类");
        return ok;
    }

    @Override
    public Response queryLeftHtImg(String objType, String objId) {
        List<HtImg> htImgs = htImgRepo.findByObjTypeAndObjIdOrderBySortId(objType, objId);
        List<HtImgVO> htImgVOS = new ArrayList<>();
        if (htImgs != null && !htImgs.isEmpty()) {
            Multimap<String, HtImgVO> htImgVOMultimap = ArrayListMultimap.create();
            for (HtImg htImg : htImgs) {
                String parentId = htImg.getParentId();
                if (parentId == null || "".equals(parentId)) {
                    htImgVOS.add(new HtImgVO(htImg, null));
                    continue;
                }
                HtImg h = htImgRepo.findByObjTypeAndObjIdAndHtImgId(objType, objId, parentId);
                htImgVOMultimap.put(htImg.getParentId(), new HtImgVO(htImg, h.getHtImgName()));
            }
            for (HtImgVO htImgVO : htImgVOS) {
                commonService.addChild(htImgVO, htImgVOMultimap, 10);
            }
        }
        Response ok = Response.ok(htImgVOS);
        ok.setQueryPara(objId, objType);
        return ok;
    }

    @Override
    public Response queryHistoryLeftTree(String objType, String objId, String energyTypeId) {
        /**
         * 2. 展示结构树名称：在对象展示结构树表中取为 当前导航栏中所选择的对象及左侧所选择的能源种类
         * 所配置的当前使用的一个或者多个展示结构树，展示org_tree_name字段，按照sort_id字段升序。默认展开第一个。
         * SELECT * FROM tb_obj_org_tree WHERE obj_type = ? AND obj_id = ? AND energy_type_id = ? AND is_use = 'Y' ORDER BY sort_id;
         */
        try {
            Long timeOutValue = commonService.getTimeOutValue();
            List<OrgTree> orgTrees = orgTreeRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndIsUseOrderBySortId(objType, objId, energyTypeId, true);
            // 查询所有能源种类
            List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAll();
            Map<String, String> mapType = new HashMap<>();
            for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                mapType.put(sysEnergyType.getEnergyTypeId(), sysEnergyType.getEnergyLoadParaId());
            }
            List<OrgTreeVO> orgTreeVOS = new ArrayList<>();
            if (orgTrees != null && !orgTrees.isEmpty()) {
                for (OrgTree orgTree : orgTrees) {
                    String orgTreeId = orgTree.getOrgTreeId();
                    String energyLoadParaId = mapType.get(orgTree.getEnergyTypeId());
                    OrgTreeVO orgTreeVO = queryTreeDetail(objType, objId, orgTreeId, timeOutValue, energyLoadParaId, energyTypeId);
                    String orgTreeName = orgTree.getOrgTreeName();
                    orgTreeVO.setOrgTreeId(orgTreeId);
                    orgTreeVO.setOrgTreeName(orgTreeName);
                    orgTreeVO.setEnergyTypeId(energyTypeId);
                    orgTreeVOS.add(orgTreeVO);
                }
            }
            Response ok = Response.ok(orgTreeVOS);
            ok.setQueryPara(objType, objId, energyTypeId);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error("查询失败", e);
            error.setQueryPara(objType, objId, energyTypeId);
            return error;
        }
    }

    private OrgTreeVO queryTreeDetail(String objType, String objId, String orgTreeId, Long timeOutValue, String energyLoadParaId, String energyTypeId) {
        List<OrgTreeDetail> orgTreeDetails = orgTreeDetailRepo.findByObjTypeAndObjIdAndOrgTreeIdOrderBySortId(objType, objId, orgTreeId);
        OrgTreeVO orgTreeVO = new OrgTreeVO();
        if (orgTreeDetails != null && !orgTreeDetails.isEmpty()) {
            // 组装所有测点查询最新值，判断是否超时和有值
            Map<String, String> datasourcesMap = new HashMap<>();
            for (OrgTreeDetail orgTreeDetail : orgTreeDetails) {
                String nodeId = orgTreeDetail.getNodeId();
                String dataSource = orgTreeDetail.getDataSource();
                // 加上此展示结构树对应的能源类型所设置的负荷参数标识，去实时库查询最新值及最新值时间
                if (dataSource != null && !"std_coal".equals(energyTypeId) && !Objects.equals("", dataSource)) {
                    datasourcesMap.put(objType + objId + orgTreeId + nodeId, StringUtils.splicingFormula(energyLoadParaId, dataSource));
                } else if (dataSource != null && "std_coal".equals(energyTypeId) && !Objects.equals("", dataSource)) {
                    JSONArray jsonArray = JSONArray.parseArray(dataSource);
                    List<Map> maps = jsonArray.toJavaList(Map.class);
                    String source = maps.get(0).get("data_source").toString();
                    if (source != null) {
                        datasourcesMap.put(objType + objId + orgTreeId + nodeId, source);
                    }
                }
            }

            Collection<String> values = datasourcesMap.values();
            List<String> points = new ArrayList<String>(values);
            List<String> list = StringUtils.removeDuplicate(points);
            List<SampleDataResponse> sampleDataResponses = kairosdbClient.queryLast(list, timeOutValue);

            List<OrgTreeDetailVO> orgTreeDetailVOS = new ArrayList<>();
            Multimap<String, OrgTreeDetailVO> OrgTreeDetailVOMultimap = ArrayListMultimap.create();
            for (OrgTreeDetail orgTreeDetail : orgTreeDetails) {
                String parentId = orgTreeDetail.getParentId();
                String nodeId = orgTreeDetail.getNodeId();
                String pointId = objType + objId + orgTreeId + nodeId;
                String pointPZ = datasourcesMap.get(pointId);
                if (parentId == null || "".equals(parentId)) {
                    if (StringUtils.isNullOrEmpty(pointPZ)) {
                        orgTreeDetailVOS.add(new OrgTreeDetailVO(orgTreeDetail, TREE1));
                    } else {
                        for (SampleDataResponse sampleDataRespons : sampleDataResponses) {
                            Double value = sampleDataRespons.getValue();
                            boolean expired = sampleDataRespons.isAllExpired(); // 全部超时 的时候 是 true
                            boolean partExpired = sampleDataRespons.isPartExpired(); // 全部不超时 的时候 是 false
                            boolean partValues = sampleDataRespons.isPartValues(); // 部分测点有值为 true
                            String point = sampleDataRespons.getPoint();
                            String dataSZ = datasourcesMap.get(pointId);
                            if (Objects.equals(point + nodeId, dataSZ + nodeId)) {
                                // true 超时
                                if (point.contains("#")) {
                                    //  如果所有测点有值且没有超时，在节点前展示图标31；
                                    if (value != null && !expired && !partExpired && !partValues) {
                                        orgTreeDetailVOS.add(new OrgTreeDetailVO(orgTreeDetail, TREE3_1));
                                    }
                                    //  如果部分测点有值且没有超时，在节点前展示图标32；
                                    if (value != null && !expired && !partExpired && partValues) {
                                        orgTreeDetailVOS.add(new OrgTreeDetailVO(orgTreeDetail, TREE3_2));
                                    }
                                    //  如果所有测点都没有值或者超时，在节点前展示图标33。
                                    if (value == null || expired || partExpired) {
                                        orgTreeDetailVOS.add(new OrgTreeDetailVO(orgTreeDetail, TREE3_3));
                                    }
                                } else {
                                    // 如果有值且没有超时（最新值时间和tb_sys_parameter设置的LastValueTimeOut），在节点前展示图标21；
                                    if (value != null && !expired && !partExpired) {
                                        orgTreeDetailVOS.add(new OrgTreeDetailVO(orgTreeDetail, TREE2_1));
                                    }
                                    // 如果没有值或者超时，在节点前展示图标22。
                                    if (value == null || expired || partExpired) {
                                        orgTreeDetailVOS.add(new OrgTreeDetailVO(orgTreeDetail, TREE2_2));
                                    }
                                }
                                break;
                            }
                        }
                        continue;
                    }
                }
                if (StringUtils.isNullOrEmpty(pointPZ)) {
                    OrgTreeDetailVOMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVO(orgTreeDetail, TREE1));
                } else {
                    for (SampleDataResponse sampleDataRespons : sampleDataResponses) {
                        Double value = sampleDataRespons.getValue();
                        boolean expired = sampleDataRespons.isAllExpired();
                        boolean partExpired = sampleDataRespons.isPartExpired();
                        boolean partValues = sampleDataRespons.isPartValues();
                        String point = sampleDataRespons.getPoint();
                        String dataSZ = datasourcesMap.get(pointId);
                        if (Objects.equals(point + nodeId, dataSZ + nodeId)) {
                            if (point.contains("#")) {
                                //  如果所有测点有值且没有超时，在节点前展示图标31；
                                if (value != null && !expired && !partExpired && !partValues) {
                                    OrgTreeDetailVOMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVO(orgTreeDetail, TREE3_1));
                                }
                                //  如果部分测点有值且没有超时，在节点前展示图标32；
                                if (value != null && !expired && !partExpired && partValues) {
                                    OrgTreeDetailVOMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVO(orgTreeDetail, TREE3_2));
                                }
                                //  如果所有测点都没有值或者超时，在节点前展示图标33。
                                if (value == null || expired || partExpired) {
                                    OrgTreeDetailVOMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVO(orgTreeDetail, TREE3_3));
                                }
                            } else {
                                // 如果有值且没有超时（最新值时间和tb_sys_parameter设置的LastValueTimeOut），在节点前展示图标21；
                                if (value != null && !expired && !partExpired) {
                                    OrgTreeDetailVOMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVO(orgTreeDetail, TREE2_1));
                                }
                                // 如果没有值或者超时，在节点前展示图标22。
                                if (value == null || expired || partExpired) {
                                    OrgTreeDetailVOMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVO(orgTreeDetail, TREE2_2));
                                }
                            }
                        }
                    }
                }
            }
            for (OrgTreeDetailVO orgTreeDetailVO : orgTreeDetailVOS) {
                commonService.addChild(orgTreeDetailVO, OrgTreeDetailVOMultimap, 10);
            }
            orgTreeVO.setChildren(orgTreeDetailVOS);
        }
        return orgTreeVO;
    }

    @Override
    public Response queryParameter(String energyTypeId) {
        List<SysEnergyPara> list = sysEnergyParaRepo.findByEnergyTypeIdOrderBySortId(energyTypeId);
        Response ok = Response.ok(list);
        ok.setQueryPara(energyTypeId);
        return ok;
    }

    @Override
    public Response queryPageInfoData(HistoryDataVO historyDataVO) {
        List<HistoryVO> historyVOS = new ArrayList<>();
        /**
         * 当最左侧能源种类选择的不是标煤(std_coal)时：展示结构树中各个节点的数据源中配置的是 具体仪表 或者 由多个具体表构成的公式 或者为 空，
         * 示例1：LYGSHCYJD.DGWSCL.M1-1
         * 示例2：LYGSHCYJD.DGWSCL.M1-1,LYGSHCYJD.DGWSCL.M2-1#?+?
         * 根据节点配置的数据源 中每个仪表 加上 查询条件中所选的参数（以“.”连接），然后结合查询条件中选择的时间及时间间隔
         * 去实时库查询对应的数据（时刻值  | 平均值 | 最大值 | 最小值 | 差值），然后如果配置的是公式还需要按照公式进行计算，最后将结果展示出来。
         *
         *
         * 当最左侧能源种类选择的是标煤(std_coal)时： 展示结构树中各个节点的数据源中配置的是 JSON数组 或者 为空
         * JSON数组中每个元素具有2个属性：para_id 和 data_source，
         * 其中para_id 的可选值就是tb_sys_energy_para表中energy_type_id='std_coal'的结果列表中的energy_para_id字段；
         * data_source则是配置的具体的数据源，即具体仪表的具体参数 或者 由多个具体仪表的具体参数构成的公式 或者为空，同时最后通过中括号指明具体能源种类标识。
         * [
         *     {
         *         "para_id": "Usage",
         *         "data_source": "LYGSHCYJD.DGWSCL.M1-1.Ep_imp[electricity],LYGSHCYJD.DGWSCL.M3-1.Totalflow[water]#?+?"
         *     }
         * ]
         * 取JSON数组中 para_id="查询条件所选参数"的记录，取出对应的data_source属性值，将其中每个测点的能源种类标识去掉，
         * 然后结合查询条件中选择的时间及时间间隔 去实时库查询对应的数据（时刻值  | 平均值 | 最大值 | 最小值 | 差值），
         * 接着需要将各个测点的数据乘以各个测点对应的具体能源种类的折标系数（tb_sys_energy_type表的std_coal_coeff字段），
         * 然后如果配置的是公式还需要按照公式进行计算，最后将结果展示出来。
         */
        try {
            String energyTypeId = historyDataVO.getEnergyTypeId();// 能源种类标识(electricity...)
            List<NodeInfoVO> nodeInfos = historyDataVO.getNodeInfo(); // 数据源+节点id+对象类型+对象标识+展示结构树标识
            String time = historyDataVO.getTime();// 时间"2019-10-17"
            String timeType = historyDataVO.getTimeType();// 时间year,month,day
            Integer interval = historyDataVO.getInterval();// 时间间隔分钟
            String type = historyDataVO.getType();//数值类型 first/average/max/min/diff
            List<String> energyParaIds = historyDataVO.getEnergyParaIds();// 能源参数标识(Pa,Ic,Ep_imp,Usage...)
            List<String> points = new ArrayList<>();
            Multimap<String, String> multimap = ArrayListMultimap.create();
            for (NodeInfoVO nodeInfo : nodeInfos) {
                String dataSource = nodeInfo.getDataSource();
                List<Map<String, List<String>>> nodeInfoSourceMap = new ArrayList<>();
                if (!"std_coal".equals(energyTypeId)) {
                    if (dataSource != null && !Objects.equals("", dataSource)) {
                        Map<String, List<String>> map = new HashMap<>();
                        for (String energyParaId : energyParaIds) {
                            String newDataSource = null;
                            if (dataSource.contains("#")) {
                                newDataSource = StringUtils.splicingFormula(energyParaId, dataSource);
                                multimap.put(nodeInfo.getNodeId() + energyParaId, newDataSource);
                                points.add(newDataSource);
                            } else {
                                newDataSource = dataSource + "." + energyParaId;
                                multimap.put(nodeInfo.getNodeId() + energyParaId, newDataSource);
                                points.add(newDataSource);
                            }
                        }
                    }
                } else {
                    if (dataSource != null && !Objects.equals("", dataSource)) {
                        JSONArray jsonArray = JSONArray.parseArray(dataSource);
                        List<Map> maps = jsonArray.toJavaList(Map.class);
                        for (Map map : maps) {
                            for (String energyParaId : energyParaIds) {
                                String paraId = map.get("para_id").toString();
                                if (Objects.equals(paraId, energyParaId)) {
                                    String source = map.get("data_source").toString();
                                    if (source != null && !Objects.equals("", source)) {
                                        multimap.put(nodeInfo.getNodeId() + energyParaId, source);
                                        points.add(source);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SysParameter sysParameter = sysParameterRepo.findByParaId("thisDiscardMinutesBeforeNow");
            Integer minute = null;
            if (sysParameter != null) {
                String paraValue = sysParameter.getParaValue();
                if (StringUtils.isNotNullAndEmpty(paraValue)) {
                    minute = Integer.valueOf(paraValue);
                }
            }
            switch (timeType) {
                case "year":
                    Long startYTime = DateUtil.stringToLong(time + "-01-01 00:00:00");
                    Long endYTime = DateUtil.stringToLong((Integer.valueOf(time) + 1) + "-01-01 00:00:00");
                    AggregatorDataResponse aggYear = queryData(type, interval, points, startYTime, endYTime);
                    historyVOS = queryAggInfo(nodeInfos, aggYear, energyParaIds, energyTypeId, multimap, timeType, minute);
                    break;
                case "month":
                    Long startMTime = DateUtil.stringToLong(time + "-01 00:00:00");
                    Date date = DateUtil.stringToDate2(time);
                    LocalDate localDate = DateUtil.dateToLocalDate(date).plusMonths(1);
                    String end = DateUtil.localDateToString(localDate);
                    Long endMTime = DateUtil.stringToLong(end + " 00:00:00");
                    AggregatorDataResponse aggMonth = queryData(type, interval, points, startMTime, endMTime);
                    historyVOS = queryAggInfo(nodeInfos, aggMonth, energyParaIds, energyTypeId, multimap, timeType, minute);
                    break;
                case "day":
                    Long startDTime = DateUtil.stringToLong(time + " 00:00:00");
                    Date dateD = DateUtil.stringToDate(time);
                    LocalDate localDateD = DateUtil.dateToLocalDate(dateD).plusDays(1);
                    String endD = DateUtil.localDateToString(localDateD);
                    Long endDTime = DateUtil.stringToLong(endD + " 00:00:00");
                    AggregatorDataResponse aggDay = queryData(type, interval, points, startDTime, endDTime);
                    historyVOS = queryAggInfo(nodeInfos, aggDay, energyParaIds, energyTypeId, multimap, timeType, minute);
                    break;
            }
            Response ok = Response.ok("查询成功", historyVOS);
            ok.setQueryPara(historyDataVO);
            return ok;
        } catch (Exception e) {
            log.error("页面具体计算逻辑失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询失败", e);
            error.setQueryPara(historyDataVO);
            return error;
        }
    }

    private List<HistoryVO> queryAggInfo(List<NodeInfoVO> nodeInfos, AggregatorDataResponse agg, List<String> energyParaIds, String energyTypeId, Multimap<String, String> multimap, String timeType, Integer minute) {
        List<HistoryVO> historyVOS = new ArrayList<>();
        for (String energyParaId : energyParaIds) {
            HistoryVO historyVO = new HistoryVO();
            historyVO.setEnergyParaId(energyParaId);
            SysEnergyPara sysEnergyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyParaId);
            historyVO.setEnergyParaName(sysEnergyPara.getEnergyParaName());
            historyVO.setUnit(sysEnergyPara.getUnit());
            List<NodeVO> nodeVOS = new ArrayList<>();
            for (NodeInfoVO nodeInfo : nodeInfos) {
                NodeVO nodeVO = new NodeVO();
                String nodeId = nodeInfo.getNodeId();
                nodeVO.setNodeId(nodeId);
                nodeVO.setObjType(nodeInfo.getObjType());
                nodeVO.setObjId(nodeInfo.getObjId());
                nodeVO.setNodeName(nodeInfo.getNodeName());
                String dataSource = nodeInfo.getDataSource();
                nodeVO.setDataSource(dataSource);
                if (dataSource != null && !Objects.equals("", dataSource)) {
                    if (dataSource.contains("#") && !dataSource.contains("[")) {
                        nodeVO.setDataSource(StringUtils.splicingFormula(energyParaId, dataSource));
                    } else if (!dataSource.contains("[") && !dataSource.contains("#")) {
                        nodeVO.setDataSource(dataSource + "." + energyParaId);
                    }
                }
                List<String> points = (List<String>) multimap.get(nodeId + energyParaId);
                if (agg != null) {
                    List<Long> timestamps = agg.getTimestamps();
                    List<DataPointResult> dataPointResults = agg.getValues();
                    if (dataPointResults != null && !dataPointResults.isEmpty()) {
                        boolean flag = false;
                        for (DataPointResult dataPointResult : dataPointResults) {
                            if (flag) {
                                break;
                            }
                            String metricName = dataPointResult.getMetricName();
                            for (String point : points) {
                                if (Objects.equals(metricName, point)) {
                                    List<Double> values = dataPointResult.getValues();
                                    values = commonService.queryHistoryData(values, timestamps);
                                    CalcPointsVO calcPointsVO = commonService.getMathHandlePoints(timestamps, values);
                                    if (calcPointsVO != null) {
                                        String avg = calcPointsVO.getAvg();
                                        if (avg != null) {
                                            Double average = Double.valueOf(avg);
                                            nodeVO.setAverage(average);
                                        } else {
                                            nodeVO.setAverage(null);
                                        }
                                        String maxVal = calcPointsVO.getMaxVal();
                                        if (maxVal != null) {
                                            Double max = Double.valueOf(maxVal);
                                            nodeVO.setMax(max);
                                        } else {
                                            nodeVO.setMax(null);
                                        }
                                        String minVal = calcPointsVO.getMinVal();
                                        if (minVal != null) {
                                            Double min = Double.valueOf(minVal);
                                            nodeVO.setMin(min);
                                        } else {
                                            nodeVO.setMin(null);
                                        }
                                        LocalDateTime maxTime = calcPointsVO.getMaxTime();
                                        LocalDateTime minTime = calcPointsVO.getMinTime();

                                        if ("year".equals(timeType)) {
                                            if (maxTime != null) {
                                                String max = maxTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"));
                                                nodeVO.setMaxTime(max);
                                            } else {
                                                nodeVO.setMaxTime(null);
                                            }
                                            if (minTime != null) {
                                                String min = minTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"));
                                                nodeVO.setMinTime(min);
                                            } else {
                                                nodeVO.setMinTime(null);
                                            }
                                        }
                                        if ("month".equals(timeType)) {
                                            if (maxTime != null) {
                                                String max = maxTime.format(DateTimeFormatter.ofPattern("dd HH:mm:ss"));
                                                nodeVO.setMaxTime(max);
                                            } else {
                                                nodeVO.setMaxTime(null);
                                            }
                                            if (minTime != null) {
                                                String min = minTime.format(DateTimeFormatter.ofPattern("dd HH:mm:ss"));
                                                nodeVO.setMinTime(min);
                                            } else {
                                                nodeVO.setMinTime(null);
                                            }
                                        }
                                        if ("day".equals(timeType)) {
                                            if (maxTime != null) {
                                                String max = maxTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                                nodeVO.setMaxTime(max);
                                            } else {
                                                nodeVO.setMaxTime(null);
                                            }
                                            if (minTime != null) {
                                                String min = minTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                                nodeVO.setMinTime(min);
                                            } else {
                                                nodeVO.setMinTime(null);
                                            }
                                        }
                                        nodeVO.setValues(values);
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        nodeVO.setMax(null);
                        nodeVO.setMin(null);
                        nodeVO.setAverage(null);
                        nodeVO.setMaxTime(null);
                        nodeVO.setMinTime(null);
                        nodeVO.setValues(null);
                    }
                    nodeVO.setTimes(timestamps);
                } else {
                    nodeVO.setMax(null);
                    nodeVO.setMin(null);
                    nodeVO.setAverage(null);
                    nodeVO.setMaxTime(null);
                    nodeVO.setMinTime(null);
                    nodeVO.setValues(null);
                }
                nodeVOS.add(nodeVO);
                historyVO.setNodeVOs(nodeVOS);
            }
            historyVOS.add(historyVO);
        }
        return historyVOS;
    }

    /**
     * 查询历史数据窗口
     *
     * @param historyInfoDataVO
     */
    @Override
    public Response queryHistoryInfoData(HistoryInfoDataVO historyInfoDataVO) {
        try {
            String start = historyInfoDataVO.getStart();
            String end = historyInfoDataVO.getEnd();
            String point = historyInfoDataVO.getPoint();
            Integer interval = historyInfoDataVO.getInterval();
            Long startTime = DateUtil.stringToLong(start);
            Long endTime = DateUtil.stringToLong(end);
            if (startTime > endTime) {
                return Response.error("开始时间不能大于结束时间");
            }
            SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryHis(point, startTime, endTime, interval, TimeUnit.MINUTES);
            Response ok = Response.ok("查询历史数据窗口成功", sampleData4KairosResp);
            ok.setQueryPara(historyInfoDataVO);
            return ok;
        } catch (Exception e) {
            log.error("查询历史数据窗口失败，e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询历史数据窗口失败", e);
            error.setQueryPara(historyInfoDataVO);
            return error;
        }
    }

    private AggregatorDataResponse queryData(String type, Integer interval, List<String> points, Long startTime, Long endTime) {
        if (points != null && !points.isEmpty()) {
            switch (type) {
                case "first":
                    AggregatorDataResponse aggFirst = null;
                    if (interval == 43200) {
                        aggFirst = kairosdbClient.queryHis(points, startTime, endTime, 1, TimeUnit.MONTHS);
                    } else {
                        aggFirst = kairosdbClient.queryHis(points, startTime, endTime, interval, TimeUnit.MINUTES);
                    }
                    return aggFirst;
                case "average":
                    AggregatorDataResponse aggAverage = null;
                    if (interval == 43200) {
                        aggAverage = kairosdbClient.queryAvg(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, TimeUnit.MONTHS);
                    } else {
                        aggAverage = kairosdbClient.queryAvg(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, interval, TimeUnit.MINUTES);
                    }
                    return aggAverage;
                case "max":
                    AggregatorDataResponse aggMax = null;
                    if (interval == 43200) {
                        aggMax = kairosdbClient.queryMax(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, TimeUnit.MONTHS);
                    } else {
                        aggMax = kairosdbClient.queryMax(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, interval, TimeUnit.MINUTES);
                    }
                    return aggMax;
                case "min":
                    AggregatorDataResponse aggMin = null;
                    if (interval == 43200) {
                        aggMin = kairosdbClient.queryMin(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, TimeUnit.MONTHS);
                    } else {
                        aggMin = kairosdbClient.queryMin(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, interval, TimeUnit.MINUTES);
                    }
                    return aggMin;
                case "diff":
                    AggregatorDataResponse aggDiff = null;
                    if (interval == 43200) {
                        aggDiff = kairosdbClient.queryDiff(points, startTime, endTime, 1, TimeUnit.MONTHS);
                    } else {
                        aggDiff = kairosdbClient.queryDiff(points, startTime, endTime, interval, TimeUnit.MINUTES);
                    }
                    return aggDiff;
            }
        }
        return null;
    }

    private void judgeList(List<String> list0, List<String> list1, List<String> list2, List<String> list3, List<String> list4, List<String> list5, List<String> list6, String opName, Integer valueType) {
        if (VALUE_TYPE_0 == valueType) {
            list0.add(opName);
        } else if (VALUE_TYPE_1 == valueType) {
            list1.add(opName);
        } else if (VALUE_TYPE_2 == valueType) {
            list2.add(opName);
        } else if (VALUE_TYPE_3 == valueType) {
            list3.add(opName);
        } else if (VALUE_TYPE_4 == valueType) {
            list4.add(opName);
        } else if (VALUE_TYPE_5 == valueType) {
            list5.add(opName);
        } else if (VALUE_TYPE_6 == valueType) {
            list6.add(opName);
        }
    }
}
