package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/28 15:11
 * @desc OrgTreeDetailVO
 */
@Data
public class OrgTreeDetailVO implements Serializable {
    private String objType;
    private String objId;
    private String orgTreeId;
    private String nodeId;
    private String nodeName;
    private String parentId;
    private String sortId;
    private String dataSource;
    private Object val;

    private String icon;

    private String iconPath;
    //百分比
    private String percent;
    // 根据 dataSource 是否为空 确定其值
    @JsonIgnore
    private boolean needCalc = false;

    private List<OrgTreeDetailVO> children;
    private boolean withChild = false;

    /**
     * 节点是否是虚拟的
     */
    private boolean isFake = false;

    @JsonIgnore
    private List<String> nodeIdList;


    public OrgTreeDetailVO(OrgTreeDetail orgTreeDetail, String icon) {
        this.objType = orgTreeDetail.getObjType();
        this.objId = orgTreeDetail.getObjId();
        this.orgTreeId = orgTreeDetail.getOrgTreeId();
        this.nodeId = StringUtils.isNullOrEmpty(orgTreeDetail.getNodeId()) ? null : orgTreeDetail.getNodeId();
        this.nodeName = orgTreeDetail.getNodeName();
        this.parentId = orgTreeDetail.getParentId();
        this.sortId = orgTreeDetail.getSortId();
        this.dataSource = orgTreeDetail.getDataSource();
        if (StringUtils.isNullOrEmpty(this.dataSource)) this.needCalc = true;
        this.val = orgTreeDetail.getVal();
        this.icon = icon;
    }


    public OrgTreeDetailVO(OrgTreeDetail orgTreeDetail) {
        this.objType = orgTreeDetail.getObjType();
        this.objId = orgTreeDetail.getObjId();
        this.orgTreeId = orgTreeDetail.getOrgTreeId();
        this.nodeId = StringUtils.isNullOrEmpty(orgTreeDetail.getNodeId()) ? null : orgTreeDetail.getNodeId();
        this.nodeName = orgTreeDetail.getNodeName();
        this.parentId = orgTreeDetail.getParentId();
        this.sortId = orgTreeDetail.getSortId();
        this.dataSource = orgTreeDetail.getDataSource();
        if (StringUtils.isNullOrEmpty(this.dataSource)) this.needCalc = true;
        this.val = orgTreeDetail.getVal();
    }

    public OrgTreeDetailVO(String nodeId, String nodeName, String parentId, Object val) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.parentId = parentId;
        this.val = val;
    }

    public OrgTreeDetailVO(String nodeId, String nodeName, String parentId) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.parentId = parentId;
    }

    public static OrgTreeDetailVO fakeNode(String nodeName) {
        return new OrgTreeDetailVO(nodeName);
    }

    public OrgTreeDetailVO(String nodeName) {
        this.nodeName = nodeName;
        this.needCalc = true;
        this.withChild = true;
        this.isFake = true;
    }

    public void setChildren(List<OrgTreeDetailVO> children) {
        if (children != null && children.size() > 0 && StringUtils.isNullOrEmpty(this.dataSource)) {
            this.needCalc = true;
            this.withChild = true;
        }
        this.children = children;
    }

    public void setNodeIdList(List<OrgTreeDetailVO> children) {
        if (nodeIdList == null) {
            this.nodeIdList = new ArrayList<>();
        }
        if (children == null || children.size() == 0) return;
        for (OrgTreeDetailVO vo : children) {
            nodeIdList.add(vo.getNodeId());
            setNodeIdList(vo.getChildren());
        }

    }

    public void setIconPathByDataSource(String dataSource) {
        if (StringUtils.isNullOrEmpty(dataSource)) {
            this.iconPath = "symbols/JET/balance/isNull.png";
            return;
        }
        if (dataSource.contains("#")) {
            this.iconPath = "symbols/JET/balance/formula.png";
        } else {
            this.iconPath = "symbols/JET/balance/singleMeter.png";
        }
    }
}
