package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.AppConfig;
import com.jet.cloud.deepmind.entity.FileMgr;
import com.jet.cloud.deepmind.entity.QFileMgr;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.DocumentManagementRepo;
import com.jet.cloud.deepmind.service.DocumentManagementService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author maohandong
 * @create 2019/10/30 10:34
 * @desc 文档管理
 */
@Service
public class DocumentManagementServiceImpl implements DocumentManagementService {

    @Autowired
    private DocumentManagementRepo documentManagementRepo;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private AppConfig appConfig;

    @Override
    public Response query(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        QFileMgr obj = QFileMgr.fileMgr;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        String objType = key.getString("objType");
        String objId = key.getString("objId");
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        String fileName = key.getString("fileName");
        if (isNotNullAndEmpty(fileName)) {
            pre = ExpressionUtils.and(pre, obj.fileName.containsIgnoreCase(fileName));
        }
        Page<FileMgr> fileMgrPage = documentManagementRepo.findAll(pre, pageable);
        Response ok = Response.ok(fileMgrPage.getContent(), fileMgrPage.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    @Transactional
    public ServiceData add(MultipartFile file, String fileDesc, String objId, String objType, String memo) {
        BufferedOutputStream bos = null;
        try {
            if (StringUtils.isNullOrEmpty(file, fileDesc)) {
                return ServiceData.error("文件或文件描述不能为空", currentUser);
            }
            // 查看文件所属目录是否存在，没有则创建新目录
            File f = new File(appConfig.getFilePath());
            if (!f.exists()) {
                f.mkdirs();
            }
            // 获得上传的文件名
            String filename = file.getOriginalFilename();
            // 获得服务器保存文件的文件路径
            String path = appConfig.getFilePath() + File.separator + filename;
            File newFile = new File(path);
            if (!newFile.exists()) {
                newFile.createNewFile();
            } else {
                return ServiceData.error("上传失败,文件已存在！", currentUser);
            }
            FileOutputStream fos = new FileOutputStream(newFile);
            bos = new BufferedOutputStream(fos);
            bos.write(file.getBytes());
            bos.flush();
            // 关闭缓冲流
            bos.close();
            // 关闭缓冲流
            fos.close();
            // 将上传的文件信息保存至表tb_obj_file_mgr中
            FileMgr fileMgr = new FileMgr();
            fileMgr.setObjId(objId);
            fileMgr.setObjType(objType);
            fileMgr.setFileName(filename);
            fileMgr.setFileDesc(fileDesc);
            fileMgr.setMemo(memo);
            fileMgr.setCreateNow();
            fileMgr.setCreateUserId(currentUser.userId());
            documentManagementRepo.save(fileMgr);
            return ServiceData.success("文件上传成功", currentUser);
        } catch (IOException e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
    }

    @Override
    @Transactional
    public ServiceData edit(Integer id, String fileDesc, String memo) {
        FileMgr old = documentManagementRepo.findById(id).get();
        if (old == null) {
            return ServiceData.error("此文件不存在", currentUser);
        }
        if (StringUtils.isNotNullAndEmpty(fileDesc)) {
            old.setFileDesc(fileDesc);
        }
        old.setMemo(memo);
        old.setUpdateNow();
        old.setUpdateUserId(currentUser.userId());
        documentManagementRepo.save(old);
        return ServiceData.success("修改文件成功", currentUser);
    }

    @Override
    public ServiceData delete(Integer id) {
        if (StringUtils.isNullOrEmpty(id)) {
            return ServiceData.error("未选择删除的文件", currentUser);
        }
        FileMgr fileMgr = documentManagementRepo.findById(id).get();
        documentManagementRepo.deleteById(id);
        try {
            String path = appConfig.getFilePath() + File.separator + fileMgr.getFileName();
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
    }

    @Override
    public void download(Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isNullOrEmpty(id)) {
            throw new RuntimeException("未选择下载的文件");
        }
        FileMgr fileMgr = documentManagementRepo.findById(id).get();
        String fileName = fileMgr.getFileName();
        if (StringUtils.isNullOrEmpty(appConfig.getFilePath(), fileName)) {
            throw new RuntimeException("文件名或文件路径不能为空");
        }
        // 获得文件路径
        String path = appConfig.getFilePath() + File.separator + fileName;
        // 获得下载文件
        File downloadFile = new File(path);
        // 判断文件是否存在
        if (downloadFile.exists()) {
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            String userAgent = request.getHeader("User-Agent");
            response.addHeader("Content-Disposition", "attachment;filename=" + StringUtils.resolvingScrambling(fileName, userAgent));
            response.addHeader("Content-Length", "" + downloadFile.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        }
     /*   if (downloadFile.exists()) {
            ServletContext context = request.getServletContext();
            String mimeType = context.getMimeType(path);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setContentLengthLong(downloadFile.length());
            String headerKey = "Content-Disposition";
            fileName = new String(fileName.getBytes(), "ISO-8859-1");
            String headerValue = String.format("attachment; filename=\"%s\"", fileName);
            response.setHeader(headerKey, headerValue);
            InputStream in = new FileInputStream(path);
            FileCopyUtils.copy(in, response.getOutputStream());*/
//            response.flushBuffer();
        else {
            throw new FileNotFoundException("文件不存在");
        }
    }
}


