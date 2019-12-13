package com.jet.cloud.deepmind.rtdb.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-29 16:39
 */
@Data
public class AggregatorDataResponse implements Serializable {
    private static final long serialVersionUID = -6382880238708430729L;

    private List<Long> timestamps;

    private List<DataPointResult> values;

    public void setValues(DataPointResult result) {

        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        this.values.add(result);
    }
}
