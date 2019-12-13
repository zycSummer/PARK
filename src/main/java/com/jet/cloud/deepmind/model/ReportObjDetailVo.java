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
 * @create 2019/11/7 16:42
 */
@Data
public class ReportObjDetailVo implements Serializable{
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime updateTime;

    List<ReportObjDetailVo> children;

    public ReportObjDetailVo(ReportObjDetail reportObjDetail) {
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
    }
}
