package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.Equip;
import com.jet.cloud.deepmind.entity.EquipSys;
import com.jet.cloud.deepmind.model.EquipVO;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:35
 * @desc 设备管理Service
 */
public interface EquipManagerService {
    Response queryEquipSys(String objType, String objId);

    Response queryEquipSysById(Integer id);

    @Transactional
    ServiceData insertOrUpdateEquipSys(EquipSys equipSys);

    @Transactional
    ServiceData deleteEquipSys(String objType, String objId, String equipSysId);

    Response queryEquip(QueryVO vo);

    @Transactional
    ServiceData addOrEditEquip(MultipartFile file, Equip equip);

    Response queryImage(String objType, String objId, String equipId);

    @Transactional
    ServiceData deleteEquip(List<EquipVO> equipVOS);

    void exportExcel(String objType, String objId, String equipSysId, String equipId, String equipName, HttpServletResponse response, HttpServletRequest request);

    @Transactional
    ServiceData importExcel(MultipartFile file, String objType, String objId);

    void download(HttpServletResponse response);
}
