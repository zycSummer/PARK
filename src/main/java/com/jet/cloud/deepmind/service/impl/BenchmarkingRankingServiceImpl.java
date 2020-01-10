package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.BenchmarkingObj;
import com.jet.cloud.deepmind.entity.BenchmarkingObjData;
import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.entity.GdpMonthly;
import com.jet.cloud.deepmind.model.BenchmarkingRankingVO;
import com.jet.cloud.deepmind.model.BenchmarkingVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.BenchmarkingRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jet.cloud.deepmind.common.Constants.*;

/**
 * @author maohandong
 * @create 2019/12/11 11:51
 */
@Service
public class BenchmarkingRankingServiceImpl implements BenchmarkingRankingService {
    @Autowired
    private BenchmarkingObjDataRepo benchmarkingObjDataRepo;
    @Autowired
    private BenchmarkingObjRepo benchmarkingObjRepo;
    @Autowired
    private DataSourceRepo dataSourceRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private GdpMonthlyRepo gdpMonthlyRepo;
    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private SiteRepo siteRepo;

    private final String GDPELECTRICITY = "gdpElectricity";
    private final String GDPWATER = "gdpWater";
    private final String GDPSTDCOAL = "gdpStdCoal";
    private final String ADDVALUESTDCOAL = "addValueStdCoal";

    @Override
    public Response queryObj(String objType, String objId, String benchmarkingType, Long timestamp, String timeUnit) {
        TimeUnit unit = null;
        LocalDateTime end = null;
        try {
            LocalDateTime start = DateUtil.longToLocalTime(timestamp);
            int year = start.getYear();
            int month = 0;
            switch (timeUnit) {
                case "years":
                    unit = TimeUnit.YEARS;
                    end = start.plusYears(1);
                    break;
                default:
                    unit = TimeUnit.MONTHS;
                    end = start.plusMonths(1);
                    month = start.getMonth().getValue();
                    break;
            }
            String pointId = null;
            //        获取数据源
            DataSource dataSource = null;
            switch (benchmarkingType) {
                case GDPELECTRICITY:
                    dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_ELECTRICITY, "Ep_imp");
                    if (StringUtils.isNotNullAndEmpty(dataSource)) {
                        pointId = dataSource.getDataSource();
                    }
                    break;
                case GDPWATER:
                    dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_ELECTRICITY, "Ep_imp");
                    if (StringUtils.isNotNullAndEmpty(dataSource)) {
                        pointId = dataSource.getDataSource();
                    }
                    break;
                case GDPSTDCOAL:
                    dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STD_COAL, "Usage");
                    if (StringUtils.isNotNullAndEmpty(dataSource)) {
                        pointId = dataSource.getDataSource();
                    }
                    break;
                case ADDVALUESTDCOAL:
                    dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STD_COAL, "Usage");
                    if (StringUtils.isNotNullAndEmpty(dataSource)) {
                        pointId = dataSource.getDataSource();
                    }
                    break;
                default:
                    break;
            }
            BenchmarkingVO benchmarkingVO = new BenchmarkingVO();
            if (objType.equals(OBJ_TYPE_PARK)) {
                benchmarkingVO.setName(parkRepo.findByParkId(objId).getParkName());
                benchmarkingVO.setType(objType);
            } else {
                benchmarkingVO.setName(siteRepo.findBySiteId(objId).getSiteName());
                benchmarkingVO.setType(objType);
            }
            ArrayList<BenchmarkingVO> list1 = new ArrayList<>();
            ArrayList<BenchmarkingVO> list2 = new ArrayList<>();
            BenchmarkingRankingVO benchmarkingRankingVO = new BenchmarkingRankingVO();
            if (StringUtils.isNotNullAndEmpty(pointId)) {
                SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryDiff(pointId, start, end, 1, unit);
                if (sampleData4KairosResp.getValues().get(0) != null) {
                    Double dataSum;
                    GdpMonthly gdpMonthly = gdpMonthlyRepo.findByObjTypeAndObjIdAndYearAndMonth(objType, objId, year, month);
                    List<GdpMonthly> gdpMonthlyList = gdpMonthlyRepo.findByObjTypeAndObjIdAndYear(objType, objId, year);
                    if (benchmarkingType.equals(ADDVALUESTDCOAL)) {
                        if (month != 0) {
                            if (gdpMonthly != null) {
                                dataSum = gdpMonthly.getAddValue();
                            } else {
                                dataSum = null;
                            }
                        } else {
                            if (StringUtils.isNotNullAndEmpty(gdpMonthlyList)) {
                                dataSum = 0d;
                                for (GdpMonthly monthly : gdpMonthlyList) {
                                    Double addValue = monthly.getAddValue();
                                    dataSum += addValue;
                                }
                            } else {
                                dataSum = null;
                            }
                        }
                    } else {
                        if (month != 0) {
                            if (gdpMonthly != null) {
                                dataSum = gdpMonthly.getGdp();
                            } else {
                                dataSum = null;
                            }
                        } else {
                            if (StringUtils.isNotNullAndEmpty(gdpMonthlyList)) {
                                dataSum = 0d;
                                for (GdpMonthly monthly : gdpMonthlyList) {
                                    Double gdp = monthly.getGdp();
                                    dataSum += gdp;
                                }
                            } else {
                                dataSum = null;
                            }
                        }
                    }
                    if (dataSum == null || dataSum == 0d) {
                        benchmarkingVO.setData(null);
                    } else {
                        benchmarkingVO.setData(sampleData4KairosResp.getValues().get(0) / dataSum);
                    }
                } else {
                    benchmarkingVO.setData(null);
                }
            } else {
                benchmarkingVO.setData(null);
            }
            list1.add(benchmarkingVO);
            list2.add(benchmarkingVO);
            //  查询对标对象
            List<BenchmarkingObj> benchmarkingObjList = benchmarkingObjRepo.findByObjTypeAndObjIdOrderBySortId(objType, objId);
            List<BenchmarkingObj> domesticList = new ArrayList<>();
            List<BenchmarkingObj> internationalList = new ArrayList<>();
//        如果有国际和国内对象
            if (StringUtils.isNotNullAndEmpty(benchmarkingObjList)) {
                for (BenchmarkingObj benchmarkingObj : benchmarkingObjList) {
                    if (benchmarkingObj.getBenchmarkingObjType().equals("Domestic")) {
                        domesticList.add(benchmarkingObj);
                    } else {
                        internationalList.add(benchmarkingObj);
                    }
                }
            }
            //        没有国际和国内对象
            else {
                //            返回当前所选对象，就是导航栏所选对象。没有数据源，设置数据源为null
                benchmarkingRankingVO.setDomesticList(list1);
                benchmarkingRankingVO.setInternationalList(list2);
                Response ok = Response.ok(benchmarkingRankingVO);
                ok.setQueryPara(objType, objId, benchmarkingType);
                return ok;
            }
            //        以下为有国际和国内对象
            Map<String, Double> map = new HashMap<>();
//        获取对标对象指标数据
            List<BenchmarkingObjData> benchmarkingObjDataList = benchmarkingObjDataRepo.findAllByObjTypeAndObjIdAndYear(objType, objId, year);
//                如果对标对象指数数据不为空
            if (StringUtils.isNotNullAndEmpty(benchmarkingObjDataList)) {
                switch (benchmarkingType) {
                    case GDPELECTRICITY:
                        for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                            map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getGdpElectricity());
                        }
                        getData(map, domesticList, internationalList);
                        break;
                    case GDPWATER:
                        for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                            map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getGdpWater());
                        }
                        getData(map, domesticList, internationalList);
                        break;
                    case GDPSTDCOAL:
                        for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                            map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getGdpStdCoal());
                        }
                        getData(map, domesticList, internationalList);
                        break;
                    case ADDVALUESTDCOAL:
                        for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                            map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getAddValueStdCoal());
                        }
                        getData(map, domesticList, internationalList);
                        break;
                    default:
                        break;
                }
                for (BenchmarkingObj benchmarkingObj : domesticList) {
                    BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
                    benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
                    benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
                    benchmarkingVO1.setData(benchmarkingObj.getData());
                    list1.add(benchmarkingVO1);
                }
                for (BenchmarkingObj benchmarkingObj : internationalList) {
                    BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
                    benchmarkingVO1.setData(benchmarkingObj.getData());
                    benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
                    benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
                    list2.add(benchmarkingVO1);
                }
                benchmarkingRankingVO.setDomesticList(list1);
                benchmarkingRankingVO.setInternationalList(list2);
                Response ok = Response.ok(benchmarkingRankingVO);
                ok.setQueryPara(objType, objId, benchmarkingType, timestamp, timeUnit);
                return ok;
            } else {
                for (BenchmarkingObj benchmarkingObj : domesticList) {
                    BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
                    benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
                    benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
                    benchmarkingVO1.setData(null);
                    list1.add(benchmarkingVO1);
                }
                for (BenchmarkingObj benchmarkingObj : internationalList) {
                    BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
                    benchmarkingVO1.setData(null);
                    benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
                    benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
                    list2.add(benchmarkingVO1);
                }
                benchmarkingRankingVO.setDomesticList(list1);
                benchmarkingRankingVO.setInternationalList(list2);
                Response ok = Response.ok(benchmarkingRankingVO);
                ok.setQueryPara(objType, objId, benchmarkingType);
                return ok;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, benchmarkingType, timestamp, timeUnit);
            return error;
        }
    }

    @Override
    public void getData(Map<String, Double> map, List<BenchmarkingObj> domesticList, List<BenchmarkingObj> internationalList) {
        for (BenchmarkingObj benchmarkingObj : domesticList) {
            Double aDouble = map.get(benchmarkingObj.getBenchmarkingObjId());
            benchmarkingObj.setData(aDouble);
        }
        for (BenchmarkingObj benchmarkingObj : internationalList) {
            Double aDouble = map.get(benchmarkingObj.getBenchmarkingObjId());
            benchmarkingObj.setData(aDouble);
        }
    }
}
