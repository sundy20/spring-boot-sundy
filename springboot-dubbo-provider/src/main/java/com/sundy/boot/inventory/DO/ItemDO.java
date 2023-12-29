package com.sundy.boot.inventory.DO;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "jettison_freq_item", autoResultMap = true)
public class ItemDO extends AbstractDO {

    @TableField(value = "name", insertStrategy = FieldStrategy.NOT_NULL)
    private String name;

    @TableField(value = "type", insertStrategy = FieldStrategy.NOT_NULL)
    private Integer type;

    @TableField(value = "creator_id", insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NEVER)
    private String createId;

}
