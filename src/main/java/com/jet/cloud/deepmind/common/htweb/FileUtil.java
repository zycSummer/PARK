package com.jet.cloud.deepmind.common.htweb;


import com.jet.cloud.deepmind.model.htweb.FileContent;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ArrayUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private static final int numOfEncAndDec = 0x99; //加密解密秘钥
    private static int dataOfFile = 0; //文件字节内容
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private static void filePathToJson(Map<String, Object> json, File file) {
        File[] children = file.listFiles();
        if (children != null) {
            Arrays.sort(children, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            for (File child : children) {
                if (child.isDirectory()) {
                    Map<String, Object> map = new TreeMap<>();
                    json.put(child.getName(), map);
                    filePathToJson(map, child);
                } else {
                    json.put(child.getName(), true);
                }
            }
        }
    }

    /**
     * @param type 'displays'|'symbols'|'components'|'assets'
     * @return
     */
    public static Map<String, Object> explore(String type) {
        String storagePath = PropertiesUtil.getProperties("storage");
        Map<String, Object> map = new HashMap<String, Object>();

        File file = new File(storagePath, type);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            filePathToJson(map, file);
        }
        return map;
    }

    /**
     * 新建文件夹
     *
     * @param dir
     * @throws FileExistsException
     */
    public static void mkdir(String dir) throws FileExistsException {
        String storagePath = PropertiesUtil.getProperties("storage");
        File file = new File(storagePath, dir);
        if (file.exists()) {
            throw new FileExistsException();
        }
        file.mkdirs();
    }

    ;

    /**
     * 文件上传
     *
     * @param fileInfo
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> upload(FileContent fileInfo) throws IOException {
        String storagePath = PropertiesUtil.getProperties("storage");
        String temp = PropertiesUtil.getProperties("compress.path");
        String path = fileInfo.getPath();
        String content = fileInfo.getContent();
        File file = new File(storagePath, fileInfo.getPath());
        if (CommUtil.isZip(path)) {
            String base64 = content.substring(content.indexOf(",") + 1);
            byte[] bytes = Base64.getDecoder().decode(base64);
            Map<String, Object> resultMap = unZipToTemp(storagePath, bytes);
            List<String> paths = (List<String>) resultMap.get("paths");
            if (paths.isEmpty()) {
                String tempPath = String.join("/", storagePath, temp, String.valueOf(resultMap.get("tempName")));
                File tempFile = new File(tempPath);
                moveSource(storagePath, tempFile, tempPath.length());
                FileUtils.deleteDirectory(tempFile);
            } else {
                return resultMap;
            }
        } else if (CommUtil.isImage(path) || CommUtil.isBase64(content)) {
            String base64 = content.substring(content.indexOf(",") + 1);
            FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(base64));
        } else {
            FileUtils.write(file, content, "UTF-8");
        }
        return null;
    }

    ;

    /**
     * 将临时文件转移至 storage 目录
     */
    public static void moveSource(String storagePath, File tempFile, Integer tempPathLen) {

        if (tempFile.exists()) {

            if (tempFile.isDirectory()) {
                for (File subFile : tempFile.listFiles()) {
                    moveSource(storagePath, subFile, tempPathLen);
                }
            } else {
                // 拼接资源移动路径
                String targetPath = String.join("/", storagePath, tempFile.getPath().substring(tempPathLen));
                File targetFile = new File(targetPath);
                File parentFile = targetFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }

                try {
                    // 目标文件存在， 删除目标文件
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    FileUtils.moveFile(tempFile, targetFile);
                } catch (IOException e) {
                    log.error("File move error :", e);
                }
            }
        }
    }

    /**
     * zip 解压到临时目录
     *
     * @param bytes
     */
    private static Map<String, Object> unZipToTemp(String storagePath, byte[] bytes) {
        String temp = PropertiesUtil.getProperties("compress.path");
        Long tempName = System.currentTimeMillis();
        List<String> paths = new ArrayList<>();
        String tempPath = String.join("/", storagePath, temp, tempName.toString());

        FileOutputStream fileOut = null;
        ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)));
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                if (fileName.indexOf("storage") > -1 && !CommUtil.isHtml(fileName)) {
                    // 非资源文件不处理

                    // 解压路径
                    File file = new File(tempPath, clearStorage("storage", fileName));
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    if (file.exists()) {
                        continue;
                    }
                    file.createNewFile();

                    // 判断资源是否存在
                    File sourceFile = new File(storagePath, clearStorage("storage", fileName));
                    if (sourceFile.exists() && sourceFile.isFile()) {
                        paths.add(sourceFile.getPath());
                    }
                    // 文件写入
                    try {
                        fileOut = new FileOutputStream(file);
                        byte[] content = new byte[1024];
                        int len;
                        while ((len = zipIn.read(content)) > 0) {
                            fileOut.write(content, 0, len);
                        }
                    } finally {
                        if (fileOut != null) {
                            fileOut.close();
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("tempName", tempName);
        resultMap.put("paths", paths);

        return resultMap;
    }

    /**
     * 文件重命名
     *
     * @param oldPath
     * @param newPath
     * @throws IOException
     */
    public static Boolean rename(String oldPath, String newPath) throws IOException {
        String storagePath = PropertiesUtil.getProperties("storage");
        File oldFile = new File(storagePath, oldPath);
        File newFile = new File(storagePath, newPath);
        if (!oldFile.exists()) {
            return false;
        }
        if (isPredefined(oldPath)) {
            return false;
        }
        if (oldFile.isFile()) {
            FileUtils.moveFile(oldFile, newFile);
        } else if (oldFile.isDirectory()) {
            FileUtils.moveDirectory(oldFile, newFile);
        }

        return true;
    }

    /**
     * 移除文件夹
     *
     * @param path
     * @return
     */
    public static Boolean remove(String path) {
        String storagePath = PropertiesUtil.getProperties("storage");
        File file = new File(storagePath, path);
        if (!file.exists()) {
            return true;
        }

        if (isPredefined(path)) {
            return false;
        }
        FileUtils.deleteQuietly(file);
        return true;
    }

    /**
     * 读取文件内容，图片返回base64信息
     *
     * @param path
     * @return
     */
    public static String readeFile(String path) {
        String storagePath = PropertiesUtil.getProperties("storage");
        File file = new File(storagePath, path);
        if (!file.exists()) {
            return "";
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] data = new byte[is.available()];

            is.read(data);

            if (Pattern.matches(".*\\.(png|jpg|gif|jpeg|bmp)", path)) {
                return new String(Base64.getEncoder().encode(data), "UTF-8");
            } else {
                return new String(data);
            }

        } catch (IOException e) {
            return "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断是否系统预设目录
     *
     * @param path
     * @return
     */
    public static Boolean isPredefined(String path) {
        String[] keywords = new String[]{"displays", "symbols", "assets", "comps"};
        path = path.replaceAll("\\/|\\s", "");

        return ArrayUtils.contains(keywords, path);
    }

    /**
     * 将文件路径中的storage去除
     *
     * @param storagePath
     * @param path
     * @return
     */
    public static String clearStorage(String storagePath, String path) {
        path = path.replace("\\", "/");
        storagePath = storagePath.replace("-", "\\-");

        int end;
        // 正规表达式
        String regPattern = "^" + storagePath + "/*+";
        Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
        // 去掉原始字符串开头位置的指定字符
        Matcher matcher = pattern.matcher(path);
        if (matcher.lookingAt()) {
            end = matcher.end();
            path = path.substring(end);
        }
        return path;
    }

    /**
     * 获取对应的预览地址
     */
    private static String getDisplayHtml(String filePath) {
        Pattern pattern = Pattern.compile("^displays\\/.*");
        Matcher matcher = pattern.matcher(filePath.toLowerCase());

        if (matcher.find())
            return "display-export.html";

        pattern = Pattern.compile("^symbols\\/.*");
        matcher = pattern.matcher(filePath.toLowerCase());
        if (matcher.find())
            return "symbol-export.html";

        return null;
    }

    /**
     * 导出
     *
     * @param dirs
     * @return
     */
    public static String export(String[] dirs, OutputStream os) {
        String storagePath = PropertiesUtil.getProperties("storage");
        String customPath = PropertiesUtil.getProperties("custom");

        String instanceDir = storagePath.substring(0, storagePath.lastIndexOf("storage"));
        String clientDir = "client";
        List<String> paths = new ArrayList<>();
        ZipOutputStream zos = null;

        try {
            zos = new ZipOutputStream(os);

            for (int i = 0; i < dirs.length; i++) {
                String dir = dirs[i];
                String displayJSON = dir;
                String fileName = new File(dir).getName();
                String displayPath = "storage/" + fileName.replace(".json", ".html");

                List<String> filePaths = new ArrayList<>();
                Boolean diaplayHtml = tidyFilePaths(storagePath, customPath, dir, filePaths, null, null);

                if (filePaths.isEmpty()) {
                    continue;
                }

                if (!diaplayHtml) {
                    String defalutHtml = getDisplayHtml(dir);

                    if (!StringUtils.isEmpty(defalutHtml)) {

                        List<String> tempPaths = new ArrayList<>();
                        tidyFilePaths(storagePath, customPath, defalutHtml, tempPaths, null, null);
                        filePaths.addAll(tempPaths);
                    }

                }

                filePaths = filePaths.stream().distinct().collect(Collectors.toList());

                for (int j = 0; j < filePaths.size(); j++) {
                    String filePath = filePaths.get(j);
                    if (paths.contains(filePath) && !CommUtil.isHtml(filePath)) {
                        // 预览模版页面不能跳过
                        continue;
                    }
                    paths.add(filePath);

                    String targetPath = filePath.replace("instance/", "");

                    if (CommUtil.isHtml(filePath)) {
                        targetPath = displayPath;
                    } else if (CommUtil.isJS(filePath)) {
                        targetPath = filePath;
                        if (filePath.indexOf("custom") == 0) {
                            filePath = instanceDir + File.separator + filePath;
                        } else {
                            filePath = clientDir + File.separator + filePath;
                        }
                    } else {
                        targetPath = filePath.substring(filePath.lastIndexOf("storage"));
                    }
                    writeZip(filePath, targetPath, zos, displayJSON);
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {

            }
        }

        return null;
    }

    /**
     * 将文件写入zip包
     *
     * @param sourcePath
     * @param zos
     */
    private static void writeZip(String sourcePath, String targetPath, ZipOutputStream zos, String displayJSON)
            throws IOException {
        File file = new File(sourcePath);
        if (file.exists()) {
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);
                ZipEntry ze = new ZipEntry(targetPath);
                zos.putNextEntry(ze);

                // byte[] content = FileUtils.readFileToByteArray(file);
                // int len = content.length;

                byte[] content = new byte[1024];
                int len;

                while ((len = fis.read(content)) > -1) {
                    if (CommUtil.isHtml(sourcePath)) {
                        // html 页面处理
                        content = tidyContent(content, displayJSON);
                        if (content.length > 1024) {
                            len = content.length;
                        }
                    }
                    zos.write(content, 0, len);
                    zos.flush();
                    content = new byte[1024];
                }

            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }
    }

    /**
     * HTML页面内容出来
     *
     * @param content
     * @return
     */
    private static byte[] tidyContent(byte[] content, String displayJSON) {
        String temp = new String(content);
        temp = temp.replaceAll("src=\"", "src='");
        temp = temp.replaceAll(".js\"", ".js'");

        temp = temp.replaceAll("'custom/", "'../custom/");
        temp = temp.replaceAll("'libs/", "'../libs/");
        temp = temp.replaceAll("GetQueryString('tag')", displayJSON);
        temp = temp.replaceAll("previews/display.json", displayJSON);
        temp = temp.replaceAll("previews/symbol.json", displayJSON);

        return temp.getBytes();
    }

    /**
     * 根据路径获取文件地址
     *
     * @param storageDir
     * @param customPath
     * @param filePath
     * @return
     */
    private static File getFileByPath(String storageDir, String customPath, String filePath) {
        File file = null;

        if (CommUtil.isHtml(filePath)) {
            file = FileUtils.getFile(customPath, "previews", filePath);
        } else {
            file = FileUtils.getFile(storageDir, filePath);
        }

        return file;
    }

    /**
     * @param storageDir
     * @param customPath
     * @param filePath       文件路径
     * @param paths          路径集合
     * @param parentFilePath 预览页面重命名用
     * @param prefix         模型中图片路径
     *                       <p>
     *                       return true 有预览页面
     */
    private static Boolean tidyFilePaths(String storageDir, String customPath, String filePath, List<String> paths,
                                         String parentFilePath, String prefix) {
        Boolean displayHtml = false;
        File file = getFileByPath(storageDir, customPath, filePath);
        if (file.exists() && file.isFile() && !paths.contains(file.getPath())) {
            String path = file.getPath();
            try {
                List<String> lines = FileUtils.readLines(file);

                // 将路径添加入集合
                paths.add(path);
                if (CommUtil.isJSON(path)) {
                    String imgPath = path.substring(0, path.lastIndexOf(".")) + ".png";
                    paths.add(imgPath);
                }

                if (CommUtil.isImage(path)) {
                    // 图片不用解析
                    return false;
                }

                if (CommUtil.isObj(path)) {
                    // obj 文件不解析
                    return false;
                }

                if (CommUtil.isHtml(filePath)) {
                    // 预览页面解析
                    lines.forEach(line -> {
                        String jsonStr = CommUtil.findJSON(line);

                        if (!StringUtils.isEmpty(jsonStr)) {
                            // json 文件
                            jsonStr = jsonStr.replaceAll("\"", "'"); // 字符统一
                            jsonStr = jsonStr.substring(jsonStr.lastIndexOf("'") + 1);
                            jsonStr = jsonStr.replace("./", "");
                            if (!CommUtil.isDefalutDisplay(jsonStr)) {
                                tidyFilePaths(storageDir, customPath, jsonStr, paths, null, null);
                            }
                        } else {
                            String jsStr = CommUtil.findJS(line);
                            if (!StringUtils.isEmpty(jsStr)) {
                                jsStr = jsStr.replaceAll("\"", "'"); // 字符统一
                                jsStr = jsStr.substring(jsStr.lastIndexOf("'") + 1);
                                if (!paths.contains(jsStr)) {
                                    paths.add(jsStr);
                                }
                            }
                        }

                    });
                    displayHtml = true;
                } else {
                    // 文件解析
                    boolean isMtl = CommUtil.isMtl(filePath);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (!StringUtils.isEmpty(line)) {
                            List<String> sources = new ArrayList<>();
                            if (!isMtl) {
                                String jsonPath = CommUtil.findSource(line);
                                if (!StringUtils.isEmpty(jsonPath)) {
                                    sources.add(jsonPath);
                                }
                            } else {
                                sources = CommUtil.findSources(line);
                            }
                            if (!sources.isEmpty()) {

                                for (String source : sources) {
                                    source = source.substring(source.lastIndexOf("\"") + 1, source.length());
                                    if (!isMtl && CommUtil.isMtl(source)) {
                                        // 解析3D模块文件时
                                        String content = FileUtils.readFileToString(file);
                                        prefix = CommUtil.getPrefix(content);
                                    }

                                    if (isMtl && !StringUtils.isEmpty(prefix)) {
                                        source = String.join(prefix, "/", source);
                                    }

                                    if (!paths.contains(source)) {
                                        if (tidyFilePaths(storageDir, customPath, source, paths, file.getPath(), prefix)) {
                                            // 是否有预览页面标识
                                            displayHtml = true;
                                        }
                                    }
                                }


                            }
                        }
                    }
                }

            } catch (IOException e) {
                log.error("Error Path: {}", file.getPath());
            }
        }
        return displayHtml;
    }

    /**
     * 获取资源信息
     *
     * @param file
     * @param encoding
     * @param prefix
     * @return
     */
    public static String source(File file, String encoding, String prefix) {

        String content = "";

        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            if (!StringUtils.isEmpty(encoding)) {
                if (encoding.equals("base64")) {
                    content = Base64.getEncoder().encodeToString(bytes);
                } else {
                    content = new String(bytes, encoding);
                }
            } else if (CommUtil.isImage(file.getPath())) {
                content = Base64.getEncoder().encodeToString(bytes);
            } else {
                content = new String(bytes);
            }

            if (!StringUtils.isEmpty(prefix)) {
                content = prefix + content;
            }
        } catch (IOException e) {
            log.error("Source Error: ", e);
        }

        return content;
    }

    private static void EncFile(File srcFile, File encFile) throws Exception {
        if (!srcFile.exists()) {
            log.error("source file not exixt");
            return;
        }
        if (!encFile.exists()) {
            log.error("encrypt file created");
            encFile.createNewFile();
        }
        InputStream fis = new FileInputStream(srcFile);
        OutputStream fos = new FileOutputStream(encFile);
        while ((dataOfFile = fis.read()) > -1) {
            fos.write(dataOfFile ^ numOfEncAndDec);
        }

        fis.close();
        fos.flush();
        fos.close();
    }

    private static void DecFile(File encFile, File decFile) throws Exception {
        if (!encFile.exists()) {
            log.error("encFile file not exixt");
            return;
        }
        if (!decFile.exists()) {
            log.error("decFile file created");
            encFile.createNewFile();
        }
        InputStream fis = new FileInputStream(encFile);
        OutputStream fos = new FileOutputStream(decFile);
        while ((dataOfFile = fis.read()) > -1) {
            fos.write(dataOfFile ^ numOfEncAndDec);
        }
        fis.close();
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) {

       /* File srcFile = new File("E:\\R.R"); //初始文件
        File encFile = new File("E:\\encFile1.tif"); //加密文件
        File decFile = new File("E:\\R1.R"); //解密文件
        try {
            long l = System.currentTimeMillis();
            System.out.println(l);
            EncFile(srcFile, encFile); //加密操作
            DecFile(encFile, decFile); //解密操作
            System.out.println("耗时:" + (System.currentTimeMillis() - l));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String res = "^\\s*data:([a-z]+\\/[a-z0-9-+.]+(;[a-z-]+=[a-z0-9-]+)?)?(;base64)?,";
        System.out.println(Pattern.matches(res, "data:image/svg+xml;base64,HIGHTOPO"));

        Pattern pattern = Pattern.compile("^\\s*data:([a-z]+\\/[a-z0-9-+.]+(;[a-z-]+=[a-z0-9-]+)?)?(;base64)?,");
        Matcher matcher = pattern.matcher("data:video/mp4;base64,HIGHTOPO");
        System.out.println(matcher.find());

        System.out.println(FileUtils.getFile("storage", "custom", "display.html"));

        File file = new File("dispaly/123.json");
        System.out.println(file.getName());
    }

}
