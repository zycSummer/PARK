package com.jet.cloud.deepmind.model;

import com.jet.cloud.deepmind.entity.HtImg;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/25 10:31
 * @desc HtImgVO
 */
@Data
public class HtImgVO implements Serializable {

    private static final long serialVersionUID = 3919899020776770948L;
    private Integer id;
    private String objType;
    private String objId;
    private String htImgId;
    private String htImgName;
    private String parentId;
    private String parentName;
    private String filePath;
    private String sortId;
    private String memo;
    private boolean spread;

    List<HtImgVO> children;

    public HtImgVO() {
    }

    public HtImgVO(HtImg htImg, String parentName) {
        this.id = htImg.getId();
        this.objType = htImg.getObjType();
        this.objId = htImg.getObjId();
        this.htImgId = htImg.getHtImgId();
        this.htImgName = htImg.getHtImgName();
        this.parentId = htImg.getParentId();
        this.filePath = htImg.getFilePath();
        this.sortId = htImg.getSortId();
        this.memo = htImg.getMemo();
        this.parentName = parentName;
    }
}
