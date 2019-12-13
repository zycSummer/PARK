package com.jet.cloud.deepmind.model.htweb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListenerEntity {

    /**
     * 事件类型
     */
    private String event;

    /**
     * 文件路径
     */
    private String path;
}
