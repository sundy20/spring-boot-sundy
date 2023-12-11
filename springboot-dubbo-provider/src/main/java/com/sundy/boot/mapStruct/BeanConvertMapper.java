package com.sundy.boot.mapStruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zeng.wang
 * @description mapStruct converter
 */
@Mapper
public interface BeanConvertMapper {

    BeanConvertMapper INSTANCE = Mappers.getMapper(BeanConvertMapper.class);

    @Mapping(source = "source.weightStr", target = "weight")
    Target source2target(Source source);
}
