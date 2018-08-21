package com.sundy.boot.protostuff;

import lombok.Builder;
import lombok.Data;

/**
 * @author plus.wang
 * @description
 * @date 2018/5/18
 */
@Data
@Builder
public class Group {

    private String id;

    private String name;

    private User user;

}