package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.AppConfig;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.BenchmarkingRankingVO;
import com.jet.cloud.deepmind.model.BenchmarkingVO;
import com.jet.cloud.deepmind.model.EnergyConsumptionVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.BenchmarkingRankingService;
import com.jet.cloud.deepmind.service.ComprehensiveSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jet.cloud.deepmind.common.Constants.*;

/**
 * @author maohandong
 * @create 2019/12/27 14:40
 */
@Service
public class ComprehensiveSummaryServiceImpl implements ComprehensiveSummaryService {
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
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private BenchmarkingRankingService benchmarkingRankingService;

    private final String GDPELECTRICITY = "gdpElectricity";
    private final String GDPWATER = "gdpWater";
    private final String GDPSTDCOAL = "gdpStdCoal";
    private final String ADDVALUESTDCOAL = "addValueStdCoal";

    @Override
    public Response query(String objType, String objId, String benchmarkingType) {
        try {
            LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            int year = now.getYear();
            LocalDateTime start = now.withDayOfYear(1);
            LocalDateTime end = start.plusYears(1);
            String pointId = null;
//        获取数据源
            if (benchmarkingType.equals(GDPELECTRICITY)) {
                DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_ELECTRICITY, "Ep_imp");
                if (StringUtils.isNotNullAndEmpty(dataSource)) {
                    pointId = dataSource.getDataSource();
                }
            } else if (benchmarkingType.equals(GDPWATER)) {
                DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_WATER, "Totalflow");
                if (StringUtils.isNotNullAndEmpty(dataSource)) {
                    pointId = dataSource.getDataSource();
                }
            } else if (benchmarkingType.equals(GDPSTDCOAL)) {
                DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STD_COAL, "Usage");
                if (StringUtils.isNotNullAndEmpty(dataSource)) {
                    pointId = dataSource.getDataSource();
                }
            } else if (benchmarkingType.equals(ADDVALUESTDCOAL)) {
                DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STD_COAL, "Usage");
                if (StringUtils.isNotNullAndEmpty(dataSource)) {
                    pointId = dataSource.getDataSource();
                }
            } else {
                throw new Exception("没有此万元GDP能源" + "{" + benchmarkingType + "}");
            }
            BenchmarkingVO benchmarkingVO = new BenchmarkingVO();
            if (objType.equals(OBJ_TYPE_PARK)) {
                Park park = parkRepo.findByParkId(objId);
                benchmarkingVO.setName(park.getParkName());
                benchmarkingVO.setAbbrName(park.getParkAbbrName());
                benchmarkingVO.setType(objType);
            } else {
                Site site = siteRepo.findBySiteId(objId);
                benchmarkingVO.setName(site.getSiteName());
                benchmarkingVO.setAbbrName(site.getSiteAbbrName());
                benchmarkingVO.setType(objType);
            }
            ArrayList<BenchmarkingVO> list1 = new ArrayList<>();
            ArrayList<BenchmarkingVO> list2 = new ArrayList<>();
            BenchmarkingRankingVO benchmarkingRankingVO = new BenchmarkingRankingVO();
            if (StringUtils.isNotNullAndEmpty(pointId)) {
                SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryDiff(pointId, start, end, 1, TimeUnit.YEARS);
                if (sampleData4KairosResp.getValues().get(0) != null) {
                    Double dataSum;
//            根据年获取gdp对象
                    List<GdpMonthly> gdpMonthlyList = gdpMonthlyRepo.findByObjTypeAndObjIdAndYear(objType, objId, year);
//            gdp对象不为null
                    if (StringUtils.isNotNullAndEmpty(gdpMonthlyList)) {
                        dataSum = 0d;
                        if (benchmarkingType.equals(ADDVALUESTDCOAL)) {
                            for (GdpMonthly gdpMonthly : gdpMonthlyList) {
                                Double addValue = gdpMonthly.getAddValue();
                                dataSum += addValue;
                            }
                        } else {
                            for (GdpMonthly monthly : gdpMonthlyList) {
//                    monthly.getGdp() 是不为空的设置
                                Double addValue = monthly.getGdp();
                                dataSum += addValue;
                            }
                        }
                    } else {
                        dataSum = null;
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
                    if (benchmarkingObj.getBenchmarkingObjType().equals("International")) {
                        internationalList.add(benchmarkingObj);
                    } else {
                        domesticList.add(benchmarkingObj);
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
                if (benchmarkingType.equals(GDPELECTRICITY)) {
                    for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                        map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getGdpElectricity());
                    }
                    benchmarkingRankingService.getData(map, domesticList, internationalList);
                    setDataList(domesticList, internationalList, list1, list2);
                    benchmarkingRankingVO.setDomesticList(list1);
                    benchmarkingRankingVO.setInternationalList(list2);
                    Response ok = Response.ok(benchmarkingRankingVO);
                    ok.setQueryPara(objType, objId, benchmarkingType);
                    return ok;
                } else if (benchmarkingType.equals(GDPWATER)) {
                    for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                        map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getGdpWater());
                    }
                    benchmarkingRankingService.getData(map, domesticList, internationalList);
                } else if (benchmarkingType.equals(GDPSTDCOAL)) {
                    for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                        map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getGdpStdCoal());
                    }
                    benchmarkingRankingService.getData(map, domesticList, internationalList);
                } else {
                    for (BenchmarkingObjData benchmarkingObjData : benchmarkingObjDataList) {
                        map.put(benchmarkingObjData.getBenchmarkingObjId(), benchmarkingObjData.getAddValueStdCoal());
                    }
                    benchmarkingRankingService.getData(map, domesticList, internationalList);
                }
                setDataList(domesticList, internationalList, list1, list2);
                List<BenchmarkingVO> collect1 = list1.stream().sorted(Comparator.comparing(BenchmarkingVO::getData, Comparator.nullsFirst(Double::compareTo))).collect(Collectors.toList());
                List<BenchmarkingVO> collect2 = list2.stream().sorted(Comparator.comparing(BenchmarkingVO::getData, Comparator.nullsFirst(Double::compareTo))).collect(Collectors.toList());
                for (int i = 0; i < collect1.size(); i++) {
                    collect1.get(i).setRanking(i + 1);
                }
                for (int i = 0; i < collect2.size(); i++) {
                    collect2.get(i).setRanking(i + 1);
                }
                benchmarkingRankingVO.setDomesticList(collect1);
                benchmarkingRankingVO.setInternationalList(collect2);
                Response ok = Response.ok(benchmarkingRankingVO);
                ok.setQueryPara(objType, objId, benchmarkingType);
                return ok;
            } else {
                for (BenchmarkingObj benchmarkingObj : domesticList) {
                    BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
                    benchmarkingVO1.setAbbrName(benchmarkingObj.getBenchmarkingObjAbbrName());
                    benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
                    benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
                    benchmarkingVO1.setData(null);
                    list1.add(benchmarkingVO1);
                }
                for (BenchmarkingObj benchmarkingObj : internationalList) {
                    BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
                    benchmarkingVO1.setData(null);
                    benchmarkingVO1.setAbbrName(benchmarkingObj.getBenchmarkingObjAbbrName());
                    benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
                    benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
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
            error.setQueryPara(objType, objId, benchmarkingType);
            return error;
        }

    }

    @Override
    public Response queryPosition(String objType, String objId) {
        ArrayList<Object> list = new ArrayList<>();
        if (objType.equals(OBJ_TYPE_PARK)) {
            Park park = parkRepo.findByParkId(objId);
            list.add(park);
        } else {
            Site site = siteRepo.findBySiteId(objId);
            list.add(site);
        }
        List<BenchmarkingObj> benchmarkingObjList = benchmarkingObjRepo.findByObjTypeAndObjIdOrderBySortId(objType, objId);
//        如果有国际和国内对象
        if (StringUtils.isNotNullAndEmpty(benchmarkingObjList)) {
            list.add(benchmarkingObjList);
        }
        Response ok = Response.ok(list);
        ok.setQueryPara(objType, objId);
        return ok;
    }

    @Override
    public Response queryAllEnergy(String objType, String objId) {
        try {
            int year = LocalDateTime.now().getYear();
            //                测点不为null,则获取年的gdp
            Double dataSum = 0d;
//            根据年获取gdp对象
            List<GdpMonthly> gdpMonthlyList = gdpMonthlyRepo.findByObjTypeAndObjIdAndYear(objType, objId, year);
//            gdp对象不为null
            if (StringUtils.isNotNullAndEmpty(gdpMonthlyList)) {
                for (GdpMonthly monthly : gdpMonthlyList) {
                    //                    monthly.getGdp() 是不为空的设置
                    Double addValue = monthly.getGdp();
                    dataSum += addValue;
                }
            } else {
                dataSum = null;
            }
            ArrayList<EnergyConsumptionVO> energyConsumptionVOS = new ArrayList<>();
//        万元gdp电耗
            DataSource gdpElectricity = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_ELECTRICITY, "Ep_imp");
            if (StringUtils.isNotNullAndEmpty(gdpElectricity)) {
                //            数据源不为null
                String gdpElectricityDataSource = gdpElectricity.getDataSource();
                EnergyConsumptionVO energyConsumptionVO = new EnergyConsumptionVO();
                energyConsumptionVO.setEneryName("gdpElectricity");
                getValue(gdpElectricityDataSource, energyConsumptionVO, dataSum);
                energyConsumptionVOS.add(energyConsumptionVO);
            }
            //        万元gdp水耗
            DataSource gdpWater = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_WATER, "Totalflow");
            if (StringUtils.isNotNullAndEmpty(gdpWater)) {
                String gdpWaterDataSource = gdpWater.getDataSource();
                EnergyConsumptionVO energyConsumptionVO = new EnergyConsumptionVO();
                energyConsumptionVO.setEneryName("gdpWater");
                getValue(gdpWaterDataSource, energyConsumptionVO, dataSum);
                energyConsumptionVOS.add(energyConsumptionVO);
            }
//        万元gdp热耗
            DataSource totalflowWork = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STEAM, "Totalflow_work");
            if (StringUtils.isNotNullAndEmpty(totalflowWork)) {
                String totalflowWorkDataSource = totalflowWork.getDataSource();
                EnergyConsumptionVO energyConsumptionVO = new EnergyConsumptionVO();
                energyConsumptionVO.setEneryName("gdpSteam");
                getValue(totalflowWorkDataSource, energyConsumptionVO, dataSum);
                energyConsumptionVOS.add(energyConsumptionVO);
            }
            //        万元gdp能耗
            DataSource gdpStdCoal = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STD_COAL, "Usage");
            if (StringUtils.isNotNullAndEmpty(gdpStdCoal)) {
                String gdpStdCoalDataSource = gdpStdCoal.getDataSource();
                EnergyConsumptionVO energyConsumptionVO = new EnergyConsumptionVO();
                energyConsumptionVO.setEneryName("gdpStdCoal");
                getValue(gdpStdCoalDataSource, energyConsumptionVO, dataSum);
                energyConsumptionVOS.add(energyConsumptionVO);
            }
            //        万元工业增加值能耗
            DataSource addValueStdCoal = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, ENERGY_TYPE_STD_COAL, "Usage");
            if (StringUtils.isNotNullAndEmpty(addValueStdCoal)) {
                //            数据源不为null
                String addValueStdCoalDataSource = addValueStdCoal.getDataSource();
                EnergyConsumptionVO energyConsumptionVO = new EnergyConsumptionVO();
                energyConsumptionVO.setEneryName("addValueStdCoal");
                Double sum = 0d;
                if (StringUtils.isNotNullAndEmpty(gdpMonthlyList)) {
                    for (GdpMonthly monthly : gdpMonthlyList) {
                        //                    monthly.getGdp() 是不为空的设置
                        Double addValue = monthly.getAddValue();
                        sum += addValue;
                    }
                } else {
                    sum = null;
                }
                getValue(addValueStdCoalDataSource, energyConsumptionVO, sum);
                energyConsumptionVOS.add(energyConsumptionVO);
            }
            Response ok = Response.ok(energyConsumptionVOS);
            ok.setQueryPara(objType, objId);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId);
            return error;
        }
    }

    @Override
    public Response queryInfo(String objType, String objId) {
        if (objType.equals(OBJ_TYPE_PARK)) {
            Park park = parkRepo.findByParkId(objId);
            if (StringUtils.isNotNullAndEmpty(park.getImgSuffix())) {
                try {
                    park.setImg(StringUtils.imageToBase64Str(appConfig.getImagePath() + "PARK_" + park.getParkId() + park.getImgSuffix()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Response.ok("查询成功", park);
        } else {
            Site site = siteRepo.findBySiteId(objId);
            if (StringUtils.isNotNullAndEmpty(site.getImgSuffix())) {
                try {
                    site.setImg(StringUtils.imageToBase64Str(appConfig.getImagePath() + "SITE_" + site.getSiteId() + site.getImgSuffix()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Response ok = Response.ok("查询成功", site);
            ok.setQueryPara(objType, objId);
            return ok;
        }
    }

    private void getValue(String dataSource, EnergyConsumptionVO energyConsumptionVO, Double dataSum) {
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime start = now.withDayOfYear(1);
        LocalDateTime end = start.plusYears(1);
        if (dataSource != null) {
            SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryDiff(dataSource, start, end, 1, TimeUnit.YEARS);
            if (sampleData4KairosResp.getValues().get(0) != null) {
                if (dataSum == null || dataSum == 0d) {
                    energyConsumptionVO.setEneryValue(null);
                } else {
                    energyConsumptionVO.setEneryValue(sampleData4KairosResp.getValues().get(0) / dataSum);
                }
            } else {
                energyConsumptionVO.setEneryValue(null);
            }
        } else {
            energyConsumptionVO.setEneryValue(null);
        }

    }

    private void setDataList(List<BenchmarkingObj> domesticList, List<BenchmarkingObj> internationalList, ArrayList<BenchmarkingVO> list1, ArrayList<BenchmarkingVO> list2) {
        for (BenchmarkingObj benchmarkingObj : domesticList) {
            BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
            benchmarkingVO1.setAbbrName(benchmarkingObj.getBenchmarkingObjAbbrName());
            benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
            benchmarkingVO1.setData(benchmarkingObj.getData());
            benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
            list1.add(benchmarkingVO1);
        }
        for (BenchmarkingObj benchmarkingObj : internationalList) {
            BenchmarkingVO benchmarkingVO1 = new BenchmarkingVO();
            benchmarkingVO1.setAbbrName(benchmarkingObj.getBenchmarkingObjAbbrName());
            benchmarkingVO1.setData(benchmarkingObj.getData());
            benchmarkingVO1.setType(benchmarkingObj.getBenchmarkingObjType());
            benchmarkingVO1.setName(benchmarkingObj.getBenchmarkingObjName());
            list2.add(benchmarkingVO1);
        }
    }
}
