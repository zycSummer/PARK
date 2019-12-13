package com.jet.cloud.deepmind.common.htweb;


import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公用类
 */
public class CommUtil {

    private static Map<String, Long> watchMap = new HashMap<>();

    private static Map<String, ByteArrayOutputStream> fileMap = new HashMap<>();

    /**
     * 文件变更版本保存
     *
     * @param filePath
     * @param version
     */
    public static void watchMapPut(String filePath, Long version) {
        watchMap.put(filePath, version);
    }

    /**
     * 根据版本号获取变化的文件列表
     *
     * @param version
     * @return
     */
    public static Map<String, Long> getChangeFileByVersion(Long version) {
        Map<String, Long> result = new HashMap<>();
        watchMap.forEach((key, value) -> {
            if (value > version) {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * 文件临时缓存
     *
     * @param key
     * @param ops
     */
    public static void fileMapPut(String key, ByteArrayOutputStream ops) {
        fileMap.put(key, ops);
    }

    /**
     * 获取文件流
     *
     * @param key
     */
    public static ByteArrayOutputStream getFileOs(String key) {
        return fileMap.get(key);
    }

    /**
     * 根据过期时间清除缓存中保存的文件流
     *
     * @param time
     */
    public static void clearFileMap(Long time) {
        Iterator<String> iter = fileMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String[] arr = key.split("/");
            Long createTime = Long.valueOf(arr[0]);
            if (System.currentTimeMillis() - createTime > time) {
                iter.remove();
            }
        }
    }

    /**
     * 根据KEY 上传缓存中的文件流
     *
     * @param fileKey
     */
    public static void removeFile(String fileKey) {
        Iterator<String> iter = fileMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (fileKey.equals(key)) {
                iter.remove();
            }
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 是否3D模型文件
     * @date: 2018年10月23日 下午5:31:57
     * @author yesb
     */
    public static Boolean isObj(String str) {
        Pattern pattern = Pattern.compile("\\.(obj)$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 判断是否为 mtl 材质文件
     * @date: 2018年10月23日 下午5:43:22
     * @author yesb
     */
    public static Boolean isMtl(String str) {
        Pattern pattern = Pattern.compile("\\.(mtl)$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * 是否图片
     *
     * @param str
     * @return
     */
    public static Boolean isImage(String str) {
        Pattern pattern = Pattern.compile("\\.(png|jpg|gif|jpeg|bmp)$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * 是否base64格式内容
     *
     * @return
     */
    public static Boolean isBase64(String str) {
        Pattern pattern = Pattern.compile("^\\s*data:([a-z]+\\/[a-z0-9-+.]+(;[a-z-]+=[a-z0-9-]+)?)?(;base64)?,");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 是否HTML
     *
     * @param str
     * @return
     */
    public static Boolean isHtml(String str) {
        Pattern pattern = Pattern.compile(".*\\.html$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * 是否js
     *
     * @param str
     * @return
     */
    public static Boolean isJS(String str) {
        Pattern pattern = Pattern.compile(".*\\.js$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * 是否json
     *
     * @param str
     * @return
     */
    public static Boolean isJSON(String str) {
        Pattern pattern = Pattern.compile(".*\\.json$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * 是否zip
     *
     * @param str
     * @return
     */
    public static Boolean isZip(String str) {
        Pattern pattern = Pattern.compile(".*\\.zip$");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        return matcher.find();
    }

    /**
     * 提取 json 文件路径
     *
     * @param str
     * @return
     */
    public static String findJSON(String str) {
        Pattern pattern = Pattern.compile("(\"|').*\\.json");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

    /**
     * 提取 js 文件路径
     *
     * @param str
     * @return
     */
    public static String findJS(String str) {
        Pattern pattern = Pattern.compile("(\"|').*\\.js");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

    /**
     * 验证是否默认预览图纸
     *
     * @param path
     * @return
     */
    public static Boolean isDefalutDisplay(String path) {

        return "previews/display.json".equals(path);
    }

    /**
     * 提取资源
     *
     * @param str
     * @return
     */
    public static String findSource(String str) {
        Pattern pattern = Pattern.compile(".*\\.(png|jpg|gif|jpeg|bmp|json|html|obj|mtl)");
        Matcher matcher = pattern.matcher(str.toLowerCase());
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 获取
     * @date: 2018年10月28日 下午10:24:42
     * @author yesb
     */
    public static List<String> findSources(String line) {
        List<String> results = new ArrayList<>();
        String[] strs = line.split(" ");
        Pattern pattern = Pattern.compile(".*\\.(png|jpg|gif|jpeg|bmp|json|html|obj|mtl)");
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            Matcher matcher = pattern.matcher(str.toLowerCase());
            if (matcher.find()) {
                results.add(matcher.group(0));
            }
        }
        return results;
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 提取 json 文件中的 prefix 路径
     * @date: 2018年10月28日 下午11:30:00
     * @author yesb
     */
    public static String getPrefix(String content) {
        Pattern pattern = Pattern.compile("prefix\":\\s\".*");
        Matcher matcher = pattern.matcher(content.toLowerCase());
        if (matcher.find()) {
            String r = matcher.group(0);
            return r.substring(10, r.length() - 1);
        }

        return "";
    }
}

