package com.jet.cloud.deepmind.common.converter;

import com.jet.cloud.deepmind.common.util.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author zhuyicheng
 * @create 2019/11/15 16:16
 * @desc 手机号码转换
 */
public class MobileConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (StringUtils.isNullOrEmpty(attribute)) return null;
        StringJoiner stringJoiner = new StringJoiner(";");
        for (String str : attribute) {
            boolean mobilePhone = StringUtils.isMobilePhone(str);
            if (mobilePhone) {
                stringJoiner.add(str);
            }
        }
        return stringJoiner.toString();
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNullOrEmpty(dbData)) return list;
        for (String str : dbData.split(";")) {
            list.add(str);
        }
        return list;
    }
}
