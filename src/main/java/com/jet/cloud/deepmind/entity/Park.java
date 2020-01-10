package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author maohandong
 * @create 2019/10/28 15:30
 */
@Data
@Entity
@Table(name = "tb_park")
public class Park extends BaseEntity {
    @Column(name = "park_id", unique = true, nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "园区标识只能输入字母、数字、下划线组合且不能大于20位")
    @NotNull(message = "园区标识不能为空")
    private String parkId;

    @Column(name = "park_name", nullable = false)
    @Size(min = 1, max = 30, message = "园区名称长度不超过30")
    @NotNull(message = "园区名称不能为空")
    private String parkName;

    @Column(name = "park_abbr_name", nullable = false)
    @Size(min = 1, max = 15, message = "园区简称长度不超过15")
    @NotNull(message = "园区简称不能为空")
    private String parkAbbrName;

    @Column(name = "rtdb_tenant_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "实时库租户标识只能输入字母、数字、下划线组合且不能大于20位")
    private String rtdbTenantId;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "scale")
    private Integer scale;

    @Column(name = "world_longitude")
    private Double worldLongitude;

    @Column(name = "world_latitude")
    private Double worldLatitude;

    @Column(name = "profile")
    @Size(min = 1, max = 1000, message = "园区简介长度不超过1000")
    private String profile;

    @Column(name = "img_suffix")
    private String imgSuffix;

    @Transient
    private String img;

    public Park() {
    }

    public Park(String parkId, String parkName) {
        this.parkId = parkId;
        this.parkName = parkName;
    }
}
