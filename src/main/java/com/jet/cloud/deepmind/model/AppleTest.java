package com.jet.cloud.deepmind.model;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/4 15:16
 * @desc
 */
public class AppleTest {

    public static Apple apple;
    public static List<Apple> apples;

    /**
     * 模拟数据
     */
    static {
        apple = new Apple();
        apple.setName("black");
        apple.setCreateAge("22");
        apple.setId("1");
        apples = new ArrayList<>(16);
        Apple apple1 = new Apple("1", "red", "21");
        Apple apple2 = new Apple("2", "blue", "22");
        Apple apple3 = new Apple("3", "yellow", "23");
        apples.add(apple1);
        apples.add(apple2);
        apples.add(apple3);
    }

    public static void main(String[] args) {
        ModelMapper modelMapper = new ModelMapper();
     /*   AppleVO appleVO = modelMapper.map(apple, AppleVO.class);
        List<AppleDTO> appleDTOS = modelMapper.map(apples, new TypeToken<List<AppleDTO>>() {
        }.getType());
        System.out.println(appleVO.toString());
        System.out.println(appleDTOS);*/

        modelMapper.getConfiguration()
                /**  LOOSE松散策略 */
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        AppleDTO appleDto2 = modelMapper.map(apple, AppleDTO.class);
        System.out.println(appleDto2.toString());
    }
}
