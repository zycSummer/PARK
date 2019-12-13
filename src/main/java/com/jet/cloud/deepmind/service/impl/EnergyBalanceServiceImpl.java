package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.OrgTree;
import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.DataSourceJsonVO;
import com.jet.cloud.deepmind.model.OrgTreeDetailVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.OrgTreeDetailRepo;
import com.jet.cloud.deepmind.repository.OrgTreeRepo;
import com.jet.cloud.deepmind.repository.SysEnergyParaRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.DataPointResult;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.EnergyBalanceService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.jet.cloud.deepmind.common.Constants.ENERGY_TYPE_STD_COAL;
import static com.jet.cloud.deepmind.common.util.MathUtil.double2StringPercent;

/**
 * @author yhy
 * @create 2019-11-07 16:44
 */
@Service
public class EnergyBalanceServiceImpl implements EnergyBalanceService {

    @Autowired
    private OrgTreeRepo treeRepo;
    @Autowired
    private OrgTreeDetailRepo orgTreeDetailRepo;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;

    @Override
    public Response getObjectClassType(String objType, String objId, String energyTypeId) {

        try {
            List<OrgTree> treeList = treeRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndIsUseOrderBySortId(objType, objId
                    , energyTypeId, true);
            Response ok = Response.ok(treeList);
            ok.setQueryPara(objId, objType, energyTypeId);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objId, objType, energyTypeId);
            return error;
        }
    }

    /**
     * 获取能源平衡树
     *
     * @param objType
     * @param objId
     * @param objTreeId
     * @param timestamp
     * @param timeUnit
     * @return
     */
    @Override
    public Response getTreeData(String objType, String objId, String objTreeId, String energyTypeId, Long timestamp, String timeUnit) {

        try {
            SysEnergyType energyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
            List<OrgTreeDetail> treeDetailList = orgTreeDetailRepo.findByObjTypeAndObjIdAndOrgTreeIdOrderBySortId(objType, objId, objTreeId);
            //Set<OrgTreeDetail> treeDetailSet = new HashSet<>();
            Map<String, OrgTreeDetail> pointIdMapOrgTreeDetail = new HashMap<>();
            if (Objects.equals(ENERGY_TYPE_STD_COAL, energyTypeId)) {

                for (OrgTreeDetail detail : treeDetailList) {
                    String dataSource = detail.getDataSource();
                    //treeDetailSet.add(detail);
                    if (StringUtils.isNotNullAndEmpty(dataSource) && dataSource.contains("[")) {
                        List<DataSourceJsonVO> voList = null;
                        try {
                            voList = JSONArray.parseArray(dataSource, DataSourceJsonVO.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new Exception("结构树dataSource配置json配置错误：" + objType + "-" + objId + "-" + objTreeId);
                        }
                        if (voList != null) {
                            for (DataSourceJsonVO vo : voList) {
                                String paraId = vo.getParaId();
                                if (Objects.equals(paraId, "Usage")) {
                                    detail.setDataSource(vo.getDataSource());
                                    pointIdMapOrgTreeDetail.put(vo.getDataSource(), detail);
                                    break;
                                }
                            }
                        }


                    }
                }
            } else {
                if (energyType == null || energyType.getEnergyUsageParaId() == null) {
                    throw new NotFoundException("[" + energyTypeId + "]energyType能源类型配置错误");
                }


                for (OrgTreeDetail detail : treeDetailList) {
                    //treeDetailSet.add(detail);
                    String dataSource = detail.getDataSource();
                    if (StringUtils.isNotNullAndEmpty(dataSource)) {
                        detail.setDataSource(StringUtils.splicingFormula(energyType.getEnergyUsageParaId(), dataSource));
                        pointIdMapOrgTreeDetail.put(detail.getDataSource(), detail);
                    }
                }
            }

            AggregatorDataResponse response = null;
            if (pointIdMapOrgTreeDetail.size() > 0) {
                TimeUnit unit = null;
                LocalDateTime start = DateUtil.longToLocalTime(timestamp);
                LocalDateTime end = null;
                switch (timeUnit) {
                    case "years":
                        unit = TimeUnit.YEARS;
                        end = start.plusYears(1);
                        break;
                    case "months":
                        unit = TimeUnit.MONTHS;
                        end = start.plusMonths(1);
                        break;
                    case "days":
                    default:
                        unit = TimeUnit.DAYS;
                        end = start.plusDays(1);
                        break;

                }

                response = kairosdbClient.queryDiff(CommonUtil.setToArrayList(pointIdMapOrgTreeDetail.keySet()), start, end, 1, unit);
            }
            if (response != null && response.getValues() != null) {

                Map<String, Double> respMap = new HashMap<>();
                for (DataPointResult result : response.getValues()) {
                    if (result.getValues() != null && result.getValues().size() > 0) {
                        respMap.put(result.getMetricName(), result.getValues().get(0));
                    }
                }

                for (OrgTreeDetail detail : treeDetailList) {
                    String dataSource = detail.getDataSource();
                    if (StringUtils.isNotNullAndEmpty(dataSource)) {
                        Double val = respMap.get(dataSource);
                        detail.setVal(val);
                    }
                }

            }

            Multimap<String, OrgTreeDetailVO> multimap = ArrayListMultimap.create();
            Map<String, OrgTreeDetailVO> nodeIdMapVO = new HashMap<>();
            for (OrgTreeDetail detail : treeDetailList) {
                OrgTreeDetailVO vo = new OrgTreeDetailVO(detail);
                vo.setIconPathByDataSource(detail.getDataSource());
                vo.setDataSource(null);
                multimap.put(StringUtils.isNullOrEmpty(detail.getParentId()) ? null : detail.getParentId(), vo);
                nodeIdMapVO.put(StringUtils.isNullOrEmpty(detail.getNodeId()) ? null : detail.getNodeId(), new OrgTreeDetailVO(detail));
            }

            List<OrgTreeDetailVO> result = new ArrayList<>();
            for (OrgTreeDetailVO vo : multimap.get(null)) {
                addChild(vo, multimap, 10);
                vo.setNodeIdList(vo.getChildren());
                result.add(vo);
            }


            //1. 如果展示结构树有多个并列的根节点，则拓扑图在最上面构造一个总节点，数据等于多个并列的根节点之和；
            // 如果组织结构树只有一个根节点，则无需再构造一个。
            //2. 拓扑图上除第一级外都需要增加损耗节点，值等于上级节点减去下级各个节点之和。
            // 当拓扑图第一级为构造的总节点时，第二级无需增加损耗节点。
            OrgTreeDetailVO fakeTopNode;
            if (result.size() > 1) {
                fakeTopNode = OrgTreeDetailVO.fakeNode(energyType.getEnergyTypeName());
                fakeTopNode.setChildren(result);
                fakeTopNode.setIconPathByDataSource(null);
                fakeTopNode.setNodeIdList(result);
            } else if (result.size() == 1) {
                fakeTopNode = result.get(0);
            } else {
                throw new Exception("没有树结构");
            }


            //递归遍历树结构
            //如果展示结构树中某个节点没有设置数据源（为空），
            // 则其数值等于下级之和（要考虑下级、下下级.......还有节点为空的情况），
            // 如果没有下级，则数值为空
            setCalcVal(fakeTopNode, nodeIdMapVO);
            iterSumTree(fakeTopNode, nodeIdMapVO);
            iterSetLossValAndPercent(fakeTopNode);

            JSONObject object = new JSONObject();
            SysEnergyPara energyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyType.getEnergyUsageParaId());
            object.put("name", energyPara.getEnergyParaName());
            object.put("unit", energyPara.getUnit());
            object.put("node", fakeTopNode);
            Response ok = Response.ok(object);
            ok.setQueryPara(objType, objId, objTreeId, energyTypeId, timestamp, timeUnit);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, objTreeId, energyTypeId, timestamp, timeUnit);
            return error;
        }
    }

    private void iterSetLossValAndPercent(OrgTreeDetailVO vo) {
        setLossValAndPercent(vo);
        List<OrgTreeDetailVO> children = vo.getChildren();
        if (children == null || children.size() == 0) return;
        for (OrgTreeDetailVO child : vo.getChildren()) {
            List<OrgTreeDetailVO> subList = child.getChildren();
            if (subList == null || subList.size() == 0) {
                ;
            } else {
                iterSetLossValAndPercent(child);
            }
        }
    }

    /**
     * 递归计算 datasource 为空的 值
     *
     * @param vo
     * @param nodeIdMapVO
     */
    private void iterSumTree(OrgTreeDetailVO vo, Map<String, OrgTreeDetailVO> nodeIdMapVO) {
        setCalcVal(vo, nodeIdMapVO);
        List<OrgTreeDetailVO> children = vo.getChildren();
        if (children == null || children.size() == 0) return;
        for (OrgTreeDetailVO child : vo.getChildren()) {
            List<OrgTreeDetailVO> subList = child.getChildren();
            if (subList == null || subList.size() == 0) {
                ;
            } else {
                iterSumTree(child, nodeIdMapVO);
            }
        }

    }

    private void addChild(OrgTreeDetailVO orgTreeDetailVO, Multimap<String, OrgTreeDetailVO> dataMultimap, int size) {
        if (size > 0 && orgTreeDetailVO != null) {
            orgTreeDetailVO.setChildren(new ArrayList<>());
            Collection<OrgTreeDetailVO> objs = dataMultimap.get(orgTreeDetailVO.getNodeId());
            if (objs.size() > 0) {
                for (OrgTreeDetailVO subModel : objs) {
                    addChild(subModel, dataMultimap, --size);
                    subModel.setNodeIdList(subModel.getChildren());
                    orgTreeDetailVO.getChildren().add(subModel);
                }
            } else {
                orgTreeDetailVO.setChildren(null);
            }
        }
    }

    /**
     * 递归遍历树结构
     * 如果展示结构树中某个节点没有设置数据源（为空），
     * 则其数值等于下级之和（要考虑下级、下下级.......还有节点为空的情况），
     * 如果没有下级，则数值为空
     *
     * @param vo
     * @param nodeIdMapVO
     */
    private void setCalcVal(OrgTreeDetailVO vo, Map<String, OrgTreeDetailVO> nodeIdMapVO) {

        if (!vo.isNeedCalc() || vo.getNodeIdList() == null) {
            return;
        }
        Double sum = null;
        for (String nodeId : vo.getNodeIdList()) {
            OrgTreeDetailVO d = nodeIdMapVO.get(nodeId);
            if (d.isNeedCalc() && d.isWithChild()) {
                continue;
            } else {
                if (sum == null) sum = 0d;
                if (d.getVal() != null) sum += Double.parseDouble(d.getVal().toString());
            }
            vo.setVal(sum);
        }
    }

    private void setLossValAndPercent(OrgTreeDetailVO child) {
        Object topVal = child.getVal();
        Double sum = null;
        if (child.getChildren() != null) {
            for (OrgTreeDetailVO t : child.getChildren()) {
                if (t.getVal() == null) continue;
                if (sum == null) sum = 0d;
                sum += Double.parseDouble(t.getVal().toString());
                if (topVal != null) {
                    double v = Double.parseDouble(t.getVal().toString()) / Double.parseDouble(topVal.toString());
                    t.setPercent(double2StringPercent(v, 2));
                }
            }
            if (child.isFake()) return;
            OrgTreeDetailVO loss = new OrgTreeDetailVO("LOSS", "损耗", child.getNodeId());
            child.getChildren().add(loss);
            if (topVal != null && sum != null) loss.setVal(Double.parseDouble(topVal.toString()) - sum);
            if (topVal != null && loss.getVal() != null) {
                double v = Double.parseDouble(loss.getVal().toString()) / Double.parseDouble(topVal.toString());
                loss.setPercent(double2StringPercent(v, 2));
            }
        }
    }
}
