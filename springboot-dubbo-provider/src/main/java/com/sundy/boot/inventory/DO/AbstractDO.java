package com.sundy.boot.inventory.DO;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public abstract class AbstractDO implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    /**
     * 无业务含义的主键
     */
    @TableField(value = "nid", updateStrategy = FieldStrategy.NEVER)
    protected Long nid;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    protected Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    protected Date gmtModified;

    /**
     * 其他扩展属性
     */
    @TableField(value = "attribute", typeHandler = MapObjectTypeHandler.class)
    protected Map<String, Object> attribute;
}
