package com.jet.cloud.deepmind.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 验证枚举类,在枚举类型的字段进行校验
 *
 * @author yhy
 * @create 2019-11-12 14:13
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidator.class})
@Documented
public @interface EnumValid {

    /**
     * 提示消息
     *
     * @return
     */
    String message() default "";

    /**
     * 对应的枚举类
     *
     * @return
     */
    Class<?>[] target() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
