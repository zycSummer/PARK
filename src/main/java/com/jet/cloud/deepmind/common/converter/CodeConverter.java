//package com.jet.cloud.deepmind.common.converter;
//
//import com.jet.cloud.deepmind.model.GroupCodeVO;
//
//import javax.persistence.AttributeConverter;
//
//import static com.jet.cloud.deepmind.common.Constants.SYMBOL_SPLIT;
//
//
///**
// * @author yhy
// * @create 2019-10-18 11:37
// */
//public class CodeConverter implements AttributeConverter<GroupCodeVO, String> {
//    @Override
//    public String convertToDatabaseColumn(GroupCodeVO attribute) {
//        return attribute.getGroupCode() + SYMBOL_SPLIT + attribute.getCode();
//    }
//
//    @Override
//    public GroupCodeVO convertToEntityAttribute(String dbData) {
//        if (dbData.contains(SYMBOL_SPLIT)) {
//            String[] split = dbData.split("\\|"); //要转义
//            return new GroupCodeVO(split[0], split[1]);
//        }
//        return null;
//    }
//}
