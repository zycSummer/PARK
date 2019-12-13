package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.AppConfig;
import com.jet.cloud.deepmind.entity.QSite;
import com.jet.cloud.deepmind.entity.Site;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.service.SiteService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/20 13:25
 */
@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private CurrentUser currentUser;

    // 支持的文件类型
    private final List<String> suffixes = Arrays.asList("image/png", "image/jpg", "image/jpeg");

    @Autowired
    private AppConfig appConfig;

    @Override
    public Response querySite(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        QSite obj = QSite.site;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        if (key != null) {
            String siteId = key.getString("siteId");
            if (StringUtils.isNotNullAndEmpty(siteId)) {
                pre = ExpressionUtils.and(pre, obj.siteId.containsIgnoreCase(siteId));
            }
            String siteName = key.getString("siteName");
            if (StringUtils.isNotNullAndEmpty(siteName)) {
                pre = ExpressionUtils.and(pre, obj.siteName.containsIgnoreCase(siteName));
            }
            Boolean isOnline = key.getBoolean("isOnline");
            if (isOnline != null) {
                pre = ExpressionUtils.and(pre, obj.isOnline.eq(isOnline));
            }
        }
        Page<Site> sitePage = siteRepo.findAll(pre, pageable);
        Response ok = Response.ok(sitePage.getContent(), sitePage.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public Response querySiteById(Integer id) {
        Site site = siteRepo.findById(id).get();
        if (StringUtils.isNotNullAndEmpty(site.getImgSuffix())) {
            try {
                site.setImg(StringUtils.imageToBase64Str(appConfig.getImagePath() + site.getSiteId() + site.getImgSuffix()));
            } catch (Exception e) {
                ;
            }
        }
        return Response.ok("根据id查找企业成功", site);
    }

    @Transactional
    @Override
    public ServiceData addOrEditSite(MultipartFile file, @Valid Site site) {

        try {
            String siteId = site.getSiteId();
            Site old = siteRepo.findBySiteId(siteId);
            String fileName = null;
            String suffix = null;
            if (file != null) {
                if (StringUtils.isNotNullAndEmpty(file.getOriginalFilename())) {
                    suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    fileName = siteId + suffix;
                }
                uploadImage(file, fileName, appConfig.getImagePath());
            }
            if (site.getId() == null) {
                if (old != null) {
                    return ServiceData.error("企业标识重复", currentUser);
                }

                site.setImgSuffix(suffix);

                //site.setFilePath(filePath);
                site.setCreateNow();
                site.setCreateUserId(currentUser.userId());
                siteRepo.save(site);
            } else {
                //修改企业
                old.setImgSuffix(suffix);
                old.setSiteName(site.getSiteName());
                old.setSiteAbbrName(site.getSiteAbbrName());
                old.setAddr(site.getAddr());
                old.setLongitude(site.getLongitude());
                old.setLatitude(site.getLatitude());
                old.setIsOnline(site.getIsOnline());
                old.setSortId(site.getSortId());
                old.setRtdbProjectId(site.getRtdbProjectId());
                old.setMemo(site.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                siteRepo.save(old);
            }
            return ServiceData.success("新增或更新成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Override
    public ServiceData delete(String siteId) {
        try {
            siteRepo.deleteBySiteId(siteId);
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除失败", e, currentUser);
        }
    }

    @Override
    public void uploadImage(MultipartFile file, String fileName, String path) {
        String type = file.getContentType();
        try {
            if (!suffixes.contains(type)) {
                throw new Exception("图片格式有误");
            }
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new Exception("请选择上传图片");
            }

            File dir = new File(path);

            if (!dir.exists()) {
                dir.mkdir();
            }
            file.transferTo(new File(dir, fileName));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
