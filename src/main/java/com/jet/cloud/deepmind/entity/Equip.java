package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:21
 * @desc 对象设备信息表
 */
@Data
@Entity
@Table(name = "tb_obj_equip")
public class Equip extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "equip_id", nullable = false)
    @NotNull(message = "设备标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "只能输入字母和数字且不能大于20位")
    private String equipId;

    @Column(name = "equip_name", nullable = false)
    @NotNull(message = "设备名称不能为空")
    @Size(min = 1, max = 30, message = "设备名称不能超过30")
    private String equipName;

    @Column(name = "equip_sys_id", nullable = false)
    @NotNull(message = "设备系统标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "只能输入字母和数字且不能大于20位")
    private String equipSysId;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model")
    private String model;

    @Column(name = "location")
    private String location;

    @Column(name = "production_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    @Column(name = "first_use_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate firstUseDate;

    @Column(name = "img_suffix")
    private String imgSuffix;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String sortId;

    @Transient
    private String equipSysName;//所属系统

    @Transient
    private String imgBase64;

    public void setProductionDate(String productionDate) {
        if (StringUtils.isNotNullAndEmpty(productionDate)) {
            this.productionDate = DateUtil.dateToLocalDate(DateUtil.stringToDate(productionDate));
        } else {
            this.productionDate = null;
        }
    }

    public void setFirstUseDate(String firstUseDate) {
        if (StringUtils.isNotNullAndEmpty(firstUseDate)) {
            this.firstUseDate = DateUtil.dateToLocalDate(DateUtil.stringToDate(firstUseDate));
        } else {
            this.firstUseDate = null;
        }
    }
}
