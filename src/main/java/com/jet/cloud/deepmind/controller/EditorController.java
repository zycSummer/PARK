package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.htweb.CommUtil;
import com.jet.cloud.deepmind.common.htweb.FileUtil;
import com.jet.cloud.deepmind.common.htweb.PropertiesUtil;
import com.jet.cloud.deepmind.model.htweb.FileContent;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@RequestMapping("/htwebPre/")
public class EditorController {

    private static final Logger log = LoggerFactory.getLogger(EditorController.class);

    /**
     * @param type 'displays'|'symbols'|'components'|'assets'
     * @return
     */
    @GetMapping("/explore/{type}")
    public Map<String, Object> explore(@PathVariable String type) {
        log.debug("explore: {}", type);
        Map<String, Object> result = FileUtil.explore(type);
        return result;
    }

    @PostMapping("/mkdir")
    public Boolean mkdir(@RequestBody String path) {
        log.debug("mkdir: {}", path);
        try {
            JSONObject jsonObject = JSONObject.parseObject(path);
            String url = jsonObject.getString("path");
            FileUtil.mkdir(url);
            return true;
        } catch (FileExistsException e) {
            return false;
        }
    }

    /**
     * 上传保存文本内容
     *
     * @param
     * @return
     * @throws IOException
     * @paramB
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(FileContent record) throws IOException {
        if (record != null && record.getPath().startsWith("displays/")) {
            return null;
        }
        return FileUtil.upload(record);
    }

    @PostMapping("/rename")
    public Boolean rename(@RequestBody String string) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(string);
        String oldPath = jsonObject.getString("oldPath");
        String newPath = jsonObject.getString("newPath");
        log.debug("rename oldPath: {} newPath: {}", oldPath, newPath);
        String storagePath = PropertiesUtil.getProperties("storage");
        File oldFile = new File(storagePath, oldPath);
        File newFile = new File(storagePath, newPath);
        if (oldFile.isFile()) {
            FileUtils.moveFile(oldFile, newFile);
        } else if (oldFile.isDirectory()) {
            FileUtils.moveDirectory(oldFile, newFile);
        }
        return true;
    }

    @PostMapping("/remove")
    public Boolean remove(@RequestBody String path) throws IOException {
        log.debug("remove: {}", path);
        JSONObject jsonObject = JSONObject.parseObject(path);
        String url = jsonObject.getString("path");
        FileUtil.remove(url);
        return true;
    }

    /**
     * 获取资源
     *
     * @return
     */
    @PostMapping("source")
    public String source(@RequestBody String string) {
        JSONObject jsonObject = JSONObject.parseObject(string);
        String url = jsonObject.getString("url");
        String encoding = jsonObject.getString("encoding");
        String prefix = jsonObject.getString("prefix");
        String storagePath = PropertiesUtil.getProperties("storage");
        url = String.join("/", storagePath, url);
        File file = new File(url);
        if (!file.exists()) {
            return "";
        }
        return FileUtil.source(file, encoding, prefix);
    }

    @GetMapping("locate")
    public Boolean locate() {
        return false;
    }

    @GetMapping("open")
    public Boolean open() {
        return false;
    }

    /**
     * 导出
     *
     * @param req
     * @param res
     * @param dir
     * @throws Exception
     */
    @PostMapping("export")
    public void export(HttpServletRequest req, HttpServletResponse res, String[] dir) throws Exception {
        String fileName = "package";
        if (dir != null && dir.length > 0) {
            fileName = new File(dir[0]).getName();
            fileName = fileName.substring(0, fileName.indexOf("."));
            if (dir.length > 1) {
                fileName += "(+" + dir.length + ")";
            }
        }
        res.setContentType("application/zip");
        res.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8") + ".zip");
        FileUtil.export(dir, res.getOutputStream());
    }

    /**
     * 根据版本号获取文件变化信息
     *
     * @param version
     */
    @GetMapping("fileVersion/{version}")
    public Map<String, Long> getFileByVersion(@PathVariable Long version) {

        return CommUtil.getChangeFileByVersion(version);
    }

    /**
     * 资源是否覆盖确认
     *
     * @param string
     * @throws IOException
     */
    @PostMapping("import")
    public void importFile(@RequestBody String string) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(string);
        String path = jsonObject.getString("path");
        Boolean move = jsonObject.getBoolean("move");
        String storagePath = PropertiesUtil.getProperties("storage");
        String temp = PropertiesUtil.getProperties("compress.path");
        String tempPath = String.join("/", storagePath, temp, path);
        File tempFile = new File(tempPath);
        if (move) {
            FileUtil.moveSource(storagePath, tempFile, tempPath.length());
        }
        FileUtils.deleteDirectory(tempFile);
    }

    /**
     * websocket 导出下载链接
     *
     * @param res
     * @param dir
     * @param filePath
     * @throws Exception
     */
    @GetMapping("download/{dir}/{filePath}")
    public void downZip(HttpServletResponse res, @PathVariable String dir, @PathVariable String filePath)
            throws Exception {
        log.debug("dir: {}, filePath: {}", dir, filePath);
        res.setContentType("application/zip");
        res.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filePath, "utf-8") + ".zip");

        String key = String.join("/", dir, filePath);
        ByteArrayOutputStream os = CommUtil.getFileOs(key);
        if (os != null) {
            OutputStream ops = res.getOutputStream();
            ops.write(os.toByteArray());
            CommUtil.removeFile(key);
        }
    }
}
