package com.jet.cloud.deepmind.common.util;

import cn.hutool.core.io.FileUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.codec.Utf8;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yhy
 * @create 2019-10-10 09:59
 */
public class StringUtils {
    public static <T> boolean isNotNullAndEmpty(T... args) {
        return !isNullOrEmpty(args);
    }

    public static <T> boolean isNotNullAndEmpty(List<T> dataList) {
        return !isNullOrEmpty(dataList);
    }


    /**
     * 参数数组中 含 有空字符串 或 null 时都返回true
     *
     * @param args
     * @return
     */
    public static <T> boolean isNullOrEmpty(T... args) {
        if (args == null || args.length == 0) {
            return true;
        }
        for (T str : args) {
            if (null == str || Objects.equals("", str)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isNullOrEmpty(Collection<T> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return true;
        }
        int i = 0;
        for (T t : dataList) {
            boolean notNullAndEmpty = isNotNullAndEmpty(t);
            if (!notNullAndEmpty) {
                i++;
            }
        }
        if (i == dataList.size()) {
            return true;
        }
        return false;
    }

    /**
     * 将htweb的配置放在磁盘
     *
     * @param filePrefix
     * @param objType
     * @param objId
     * @param htImgId
     * @param content
     */
    public static String sendFromFile(String filePrefix, String objType, String objId, String htImgId, String content, String oldObjType, String oldObjId, String oldHtImgId, String menuId) {
        String fileOldName = menuId + oldObjType + "_" + oldObjId + "_" + oldHtImgId + ".json";
        File[] fileList = null;
        String fileName = menuId + objType + "_" + objId + "_" + htImgId + ".json";
        if (content == null) content = "";
        File file = new File(filePrefix);
        if (file.isDirectory()) {
            fileList = file.listFiles();
        }
        if (fileList == null || fileList.length == 0) {
            // 直接插入
            writeData(filePrefix, fileName, content);
        } else {
            for (File temp : fileList) {
                String tempName = temp.getName();
                if ((Objects.equals(fileName, tempName)) || (Objects.equals(fileOldName, tempName))) {
                    FileUtil.del(temp);
                }
            }
            writeData(filePrefix, fileName, content);
        }
        return filePrefix;
    }

    private static void writeData(String filePath, String fileName, String content) {
        File f = new File(filePath, fileName);
        try {
            f.createNewFile();
            FileOutputStream outStream = new FileOutputStream(f);//文件输出流用于将数据写入文件
            outStream.write(content.getBytes("utf8"));
            outStream.close();    //关
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePrefix, String objType, String objId, String htImgId, String menuId) {
        File[] fileList = null;
        String fileName = menuId + objType + "_" + objId + "_" + htImgId + ".json";
        File file = new File(filePrefix);
        if (file.isDirectory()) {
            fileList = file.listFiles();
        }
        if (fileList != null) {
            for (File temp : fileList) {
                String tempName = temp.getName();
                if ((Objects.equals(fileName, tempName))) {
                    FileUtil.del(temp);
                }
            }
        }
    }


    public static String readToString(String filePath) {
        String encoding = "UTF-8";
        File file = new File(filePath);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 图片转base64字符串
     *
     * @param imgFile 图片路径
     * @return
     */
    public static String imageToBase64Str(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            //e.printStackTrace();
            ;
        }
        // 加密
//        BASE64Encoder encoder = new BASE64Encoder();
        return encode(data);
    }

    /**
     * 解密
     *
     * @param bytes
     * @return
     */
    public static byte[] decode(final byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    /**
     * 加密
     * 二进制数据编码为BASE64字符串
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(final byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * 将prefix拼接到每个测点后面
     *
     * @param prefix     p
     * @param dataSource LYGSHCYJD.DGWSCL.M1-1
     * @return
     */
    public static String splicingFormula(String prefix, String dataSource) {
        if (dataSource == null) return null;
        StringBuilder stringBuilder = new StringBuilder("");
        if (dataSource.contains("#") && dataSource.contains("[")) {
            // A.prefix[P],B.prefix[P]#?+? ->A.prefix,B.prefix#?+?
            String[] opNameArray = dataSource.split("#")[0].split(",");
            String formula = dataSource.split("#")[1];
            for (String s : opNameArray) {
                String point = s.split("\\[")[0];
                stringBuilder.append(point).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("#").append(formula);
        } else if (dataSource.contains("#") && !dataSource.contains("[")) {
            String[] opNameArray = dataSource.split("#")[0].split(",");
            String s1 = dataSource.split("#")[1];
            for (int i = 0; i < opNameArray.length; i++) {
                stringBuilder.append(opNameArray[i]).append(".").append(prefix).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("#").append(s1);
        } else if (dataSource.contains("[")) {
            // A[P] ->A.prefix
            stringBuilder.append(dataSource.split("\\[")[0]).append(".").append(prefix);
        } else {
            stringBuilder.append(dataSource).append(".").append(prefix);
        }
        return String.valueOf(stringBuilder);
    }

    /**
     * 替换表达式中的?为实际值
     *
     * @param formula 公式字符串 如：?+?*0.2?
     * @param values  需要替换值
     * @return String 算术表达式字符串
     * @author zhuyicheng
     */
    public static String replaceEach(String formula, List<Double> values) {
        StringBuilder stringBuilder = new StringBuilder(formula);
        int index = 0;
        for (Double value : values) {
            for (int i = index; i < stringBuilder.length(); i++) {
                char c = stringBuilder.charAt(i);
                if (c == '?') {
                    stringBuilder.replace(i, i + 1, value.toString());
                    index = i;
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 解决微软IE或者Edge浏览器下导出文件文件名乱码
     *
     * @param fileName
     * @param userAgent
     * @return
     */
    public static String resolvingScrambling(String fileName, String userAgent) {
        try {
            if (userAgent.toUpperCase().contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Edge")) {/* IE 8 至 IE 10, IE 11, Edge 浏览器的乱码问题解决 */
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else { // 万能乱码问题解决
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static boolean isEmail(String value) {
        String emailPattern = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(emailPattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    public static boolean isMobilePhone(String value) {
        String mobilePhonePattern = "^((1[3-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(mobilePhonePattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public static void main(String[] args) {
        String token = StringUtils.encode("8b068514cf9eb:387f525908ad6f786a4dae5351d580b6".getBytes());
        System.out.println(token);
    }
}
