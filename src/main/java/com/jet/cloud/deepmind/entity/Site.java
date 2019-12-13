package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhuyicheng
 * @create 2019/10/24 17:07
 * @desc 企业实体类
 */
@Data
@Entity
@Table(name = "tb_site")
public class Site extends BaseEntity {
    @Column(name = "site_id", unique = true, nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "企业标识只能输入字母和数字且不能大于20位")
    @NotNull(message = "企业标识不能为空")
    private String siteId;

    @Column(name = "site_name", nullable = false)
    @Size(min = 1, max = 30, message = "企业名称长度不超过30")
    @NotNull(message = "企业名称不能为空")
    private String siteName;

    @Column(name = "site_abbr_name", nullable = false)
    private String siteAbbrName;

    @Column(name = "addr")
    private String addr;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "is_online")
    @Convert(converter = String2BooleanConverter.class)
    private Boolean isOnline;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String sortId;

    @Column(name = "rtdb_project_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "实时库项目标识只能输入字母和数字且不能大于20位")
    private String rtdbProjectId;

    //企业图片后缀
    @JsonIgnore
    @Column(name = "img_suffix")
    private String imgSuffix;

    @Transient
    private String img;

    public Site(String siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
    }

    public Site() {
    }

    public Site(String siteId) {
        this.siteId = siteId;
    }
}
