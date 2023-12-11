package com.sundy.boot.mapStruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author zeng.wang
 * @description mapStruct converter
 */
@Mapper(componentModel = "spring")
public interface BeanConvertMapperSpring {

    @Mapping(source = "source.weightStr", target = "weight")
    Target source2target(Source source);
}
