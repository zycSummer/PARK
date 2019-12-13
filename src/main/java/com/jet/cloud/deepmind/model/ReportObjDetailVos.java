package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jet.cloud.deepmind.entity.ReportObjDetail;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/27 9:15
 */
@Data
public class ReportObjDetailVos implements Serializable {
    private Integer id;
    private String objType;
    private String objId;
    private String reportId;
    private String nodeId;
    private String nodeName;
    private String parentId;
    private String sortId;
    private String dataSource;
    private String memo;
    private String parentName;
    private String createUserId;
    private String updateUserId;
    private Integer deep;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime updateTime;


    List<ReportObjDetailVos> children;

    public ReportObjDetailVos() {
    }

    public ReportObjDetailVos(ReportObjDetail reportObjDetail, Integer deep) {
        this.id = reportObjDetail.getId();
        this.objType = reportObjDetail.getObjType();
        this.objId = reportObjDetail.getObjId();
        this.reportId = reportObjDetail.getReportId();
        this.nodeId = reportObjDetail.getNodeId();
        this.nodeName = reportObjDetail.getNodeName();
        this.parentId = reportObjDetail.getParentId();
        this.parentName = reportObjDetail.getParentName();
        this.sortId = reportObjDetail.getSortId();
        this.dataSource = reportObjDetail.getDataSource();
        this.memo = reportObjDetail.getMemo();
        this.updateTime = reportObjDetail.getUpdateTime();
        this.updateUserId = reportObjDetail.getUpdateUserId();
        this.createTime = reportObjDetail.getCreateTime();
        this.createUserId = reportObjDetail.getCreateUserId();
        this.deep = deep;
    }
    public ReportObjDetailVos(ReportObjDetailVos reportObjDetailVos) {
        this.id = reportObjDetailVos.getId();
        this.objType = reportObjDetailVos.getObjType();
        this.objId = reportObjDetailVos.getObjId();
        this.reportId = reportObjDetailVos.getReportId();
        this.nodeId = reportObjDetailVos.getNodeId();
        this.nodeName = reportObjDetailVos.getNodeName();
        this.parentId = reportObjDetailVos.getParentId();
        this.parentName = reportObjDetailVos.getParentName();
        this.sortId = reportObjDetailVos.getSortId();
        this.dataSource = reportObjDetailVos.getDataSource();
        this.memo = reportObjDetailVos.getMemo();
        this.updateTime = reportObjDetailVos.getUpdateTime();
        this.updateUserId = reportObjDetailVos.getUpdateUserId();
        this.createTime = reportObjDetailVos.getCreateTime();
        this.createUserId = reportObjDetailVos.getCreateUserId();
        this.deep = reportObjDetailVos.getDeep();
    }
}
