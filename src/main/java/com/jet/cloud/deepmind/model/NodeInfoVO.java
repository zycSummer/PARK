package com.jet.cloud.deepmind.model;

import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/10/29 11:30
 * @desc NodeInfoVO
 */
@Data
public class NodeInfoVO {
    private String nodeId;
    private String objType;
    private String objId;
    private String objTreeId;
    private String nodeName;
    private String dataSource;

    public NodeInfoVO() {
    }

    public NodeInfoVO(String nodeId, String objType, String objId, String objTreeId, String nodeName, String dataSource) {
        this.nodeId = nodeId;
        this.objType = objType;
        this.objId = objId;
        this.objTreeId = objTreeId;
        this.nodeName = nodeName;
        this.dataSource = dataSource;
    }
}
