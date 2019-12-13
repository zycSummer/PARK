package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.util.List;

/**
 * @author maohandong
 * @create 2019/12/11 15:30
 */
@Data
public class BenchmarkingRankingVO {
    List<BenchmarkingVO> domesticList;
    List<BenchmarkingVO> internationalList;
}
