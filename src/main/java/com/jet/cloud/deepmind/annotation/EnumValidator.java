package com.jet.cloud.deepmind.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author yhy
 * @create 2019-11-12 14:15
 */
public class EnumValidator implements ConstraintValidator<EnumValid, String> {
    /**
     * 枚举类
     */
    Class<?>[] clazz;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        clazz = constraintAnnotation.target();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (clazz.length > 0) {
            try {
                for (Class<?> clz : clazz) {
                    if (clz.isEnum()) {
                        // 枚举类验证
                        Object[] objs = clz.getEnumConstants();
                        for (Object obj : objs) {
                            Class<?> enumClz = obj.getClass();
                            Field[] fields = enumClz.getDeclaredFields();
                            for (Field field : fields) {
                                // 访问私有成员属性开关
                                field.setAccessible(true);
                                EnumValid enumValid = field.getAnnotation(EnumValid.class);
                                if (Objects.nonNull(enumValid)) {
                                    // 获取成员属性的值
                                    Object enumValue = field.get(obj);
                                    if (value == null || enumValue == null || value.equals(enumValue.toString())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            return true;
        }
        return false;
    }
}
