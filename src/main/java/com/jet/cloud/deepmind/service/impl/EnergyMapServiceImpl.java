package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.AppConfig;
import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.entity.GdpMonthly;
import com.jet.cloud.deepmind.entity.QDataSource;
import com.jet.cloud.deepmind.entity.Site;
import com.jet.cloud.deepmind.model.ComprehensiveShowVO;
import com.jet.cloud.deepmind.model.MapDetailVO;
import com.jet.cloud.deepmind.model.OKResponse;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.CommonRepo;
import com.jet.cloud.deepmind.repository.DataSourceRepo;
import com.jet.cloud.deepmind.repository.GdpMonthlyRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.SampleDataResponse;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.DocumentManagementService;
import com.jet.cloud.deepmind.service.EnergyMapService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static com.jet.cloud.deepmind.common.Constants.*;
import static com.jet.cloud.deepmind.common.util.MathUtil.double2String;

/**
 * @author yhy
 * @create 2019-10-25 14:03
 */
@Service
public class EnergyMapServiceImpl implements EnergyMapService {
    @Autowired
    private CommonRepo commonRepo;
    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private DataSourceRepo dataSourceRepo;
    @Autowired
    private CommonService commonService;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private DocumentManagementService documentManagementService;
    @Autowired
    private GdpMonthlyRepo gdpMonthlyRepo;
    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private AppConfig appConfig;

    @Override
    public Response getSiteList() {
        List<ComprehensiveShowVO> details = commonRepo.querySiteDetails(currentUser.userGroupId());
        return Response.ok(details);
    }

    @Override
    public Response getDetail(String siteId) {


        QDataSource obj = QDataSource.dataSource1;
        Predicate pre = obj.isNotNull()
                .and(obj.objType.eq(OBJ_TYPE_SITE)).and(obj.objId.eq(siteId));

        Predicate p1 = obj.energyTypeId.eq(ENERGY_TYPE_ELECTRICITY).and(obj.energyParaId.eq("P"));
        Predicate p2 = obj.energyTypeId.eq(ENERGY_TYPE_WATER).and(obj.energyParaId.eq("Flowrate"));
        Predicate p3 = obj.energyTypeId.eq(ENERGY_TYPE_STEAM).and(obj.energyParaId.eq("Flowrate_work"));
        Predicate p4 = obj.energyTypeId.eq(ENERGY_TYPE_STD_COAL).and(obj.energyParaId.eq("Usage"));

        Predicate or1 = ExpressionUtils.or(p1, p2);
        Predicate or2 = ExpressionUtils.or(p3, p4);
        Predicate or = ExpressionUtils.or(or1, or2);

        pre = ExpressionUtils.and(pre, or);
        Iterable<DataSource> dataSources = dataSourceRepo.findAll(pre);
        List<String> pointIdList = new ArrayList<>();
        Map<String, DataSource> valueMap = new HashMap<>();
        String stdCoPoint = null;
        for (DataSource dataSource : dataSources) {
            if (Objects.equals(ENERGY_TYPE_STD_COAL, dataSource.getEnergyTypeId())) {
                stdCoPoint = dataSource.getDataSource();
            } else {
                pointIdList.add(dataSource.getDataSource());
            }
            valueMap.put(dataSource.getDataSource(), dataSource);

        }

        //获取超时时间
        Long timeOutValue = commonService.getTimeOutValue();


        List<SampleDataResponse> queryLast = kairosdbClient.queryLast(pointIdList, timeOutValue);

        MapDetailVO vo = new MapDetailVO();
        LocalDateTime nowStart = LocalDateTime.now().withHour(0).withMinute(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime nowEnd = LocalDateTime.now();
        Double stdCoal = null;
        if (StringUtils.isNotNullAndEmpty(stdCoPoint)) {

            Long start = DateUtil.localDateTimeToLong(nowStart.withDayOfYear(1));
            Long end = DateUtil.localDateTimeToLong(nowEnd);

            Long lastStart = DateUtil.localDateTimeToLong(nowStart.withDayOfYear(1).minusYears(1));
            Long lastEnd = DateUtil.localDateTimeToLong(nowEnd.minusYears(1));
            SampleData4KairosResp nowData = kairosdbClient.queryDiff(stdCoPoint, start, end, 1, TimeUnit.YEARS);
            SampleData4KairosResp lastData = kairosdbClient.queryDiff(stdCoPoint, lastStart, lastEnd, 1, TimeUnit.YEARS);
            Double v = null;
            if (nowData != null && nowData.getValues() != null && nowData.getValues().size() > 0) {
                v = nowData.getValues().get(0);
                stdCoal = v;
                vo.setStdCoal(double2String(v));
            }
            if (lastData != null && v != null && lastData.getValues() != null && lastData.getValues().size() > 0) {
                Double last = lastData.getValues().get(0);
                Double result = null;
                if (last != null) {
                    result = (v - last) / last * 100;
                }
                vo.setLastYearCompareRate(double2String(result));
            }
        }

        for (SampleDataResponse response : queryLast) {
            valueMap.get(response.getPoint()).setLastVal(response.getValue());
        }

        for (DataSource source : valueMap.values()) {
            String val = double2String(source.getLastVal());
            switch (source.getEnergyTypeId()) {
                case ENERGY_TYPE_ELECTRICITY:
                    vo.setElectric(val);
                    break;
                case ENERGY_TYPE_WATER:
                    vo.setWater(val);
                    break;
                case ENERGY_TYPE_STEAM:
                    vo.setSteam(val);
                    break;
                default:
                    break;
            }
        }
        Site site = siteRepo.findBySiteId(siteId);
        try {
            if (StringUtils.isNotNullAndEmpty(site.getImgSuffix())) {
                try {
                    vo.setIcon(StringUtils.imageToBase64Str(appConfig.getImagePath() + siteId + site.getImgSuffix()));
                } catch (Exception e) {
                    ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //gdp
        List<GdpMonthly> monthlyList = gdpMonthlyRepo.findByObjTypeAndObjIdAndYear(OBJ_TYPE_SITE, siteId, nowStart.getYear());
        Double gdpSum = null;
        for (GdpMonthly gdpMonthly : monthlyList) {
            if (gdpSum == null) gdpSum = 0d;
            if (gdpMonthly.getGdp() != null) {
                gdpSum += gdpMonthly.getGdp();
            }
        }

        if (gdpSum != null && stdCoal != null) {
            double v = stdCoal / gdpSum;
            vo.setGdp(double2String(v));
        }
        vo.setAddr(site.getAddr());
        Response ok = new OKResponse(vo);
        ok.setQueryPara(siteId);
        return ok;
    }


}
