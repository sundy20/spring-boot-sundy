package com.sundy.boot.mapStruct;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author zeng.wang
 * @description mapStruct converter
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class BeanConverter {
    public abstract Target convert(Source source);
}
