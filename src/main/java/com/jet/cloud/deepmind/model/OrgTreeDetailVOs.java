package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/20 16:22
 * @desc
 */
@Data
public class OrgTreeDetailVOs implements Serializable {
    private static final long serialVersionUID = -6268090014031872325L;
    private Integer id;
    private String objType;
    private String objId;
    private String orgTreeId;
    private String nodeId;
    private String nodeName;
    private String parentId;
    private String parentName;
    private String sortId;
    private String dataSource;
    private List<OrgTreeDetailVOs> children;
    private Integer deep;
    private String createUserId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateUserId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String memo;

    public OrgTreeDetailVOs() {

    }

    public OrgTreeDetailVOs(OrgTreeDetail orgTreeDetail, Integer deep) {
        this.objType = orgTreeDetail.getObjType();
        this.objId = orgTreeDetail.getObjId();
        this.orgTreeId = orgTreeDetail.getOrgTreeId();
        this.nodeId = orgTreeDetail.getNodeId();
        this.nodeName = orgTreeDetail.getNodeName();
        this.parentId = orgTreeDetail.getParentId();
        this.sortId = orgTreeDetail.getSortId();
        this.dataSource = orgTreeDetail.getDataSource();
        this.createUserId = orgTreeDetail.getCreateUserId();
        this.createTime = orgTreeDetail.getCreateTime();
        this.updateTime = orgTreeDetail.getUpdateTime();
        this.updateUserId = orgTreeDetail.getUpdateUserId();
        this.memo = orgTreeDetail.getMemo();
        this.deep = deep;
        this.id = orgTreeDetail.getId();
        this.parentName = orgTreeDetail.getParentName();
    }

    public OrgTreeDetailVOs(OrgTreeDetailVOs orgTreeDetailVOs) {
        this.id = orgTreeDetailVOs.getId();
        this.objType = orgTreeDetailVOs.getObjType();
        this.objId = orgTreeDetailVOs.getObjId();
        this.orgTreeId = orgTreeDetailVOs.getOrgTreeId();
        this.nodeId = orgTreeDetailVOs.getNodeId();
        this.nodeName = orgTreeDetailVOs.getNodeName();
        this.parentId = orgTreeDetailVOs.getParentId();
        this.sortId = orgTreeDetailVOs.getSortId();
        this.dataSource = orgTreeDetailVOs.getDataSource();
        this.deep = orgTreeDetailVOs.getDeep();
        this.createUserId = orgTreeDetailVOs.getCreateUserId();
        this.createTime = orgTreeDetailVOs.getCreateTime();
        this.updateTime = orgTreeDetailVOs.getUpdateTime();
        this.updateUserId = orgTreeDetailVOs.getUpdateUserId();
        this.memo = orgTreeDetailVOs.getMemo();
    }
}
