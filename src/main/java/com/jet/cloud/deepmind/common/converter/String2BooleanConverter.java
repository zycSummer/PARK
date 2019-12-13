package com.jet.cloud.deepmind.common.converter;

import javax.persistence.AttributeConverter;
import java.util.Objects;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNullOrEmpty;

/**
 * Y,N 转换为Boolean
 *
 * @author yhy
 * @create 2019-10-25 10:19
 */
public class String2BooleanConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (isNullOrEmpty(attribute) || !attribute) {
            return "N";
        } else {
            return "Y";
        }
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (Objects.equals(dbData, "Y")) return true;
        return false;
    }
}
