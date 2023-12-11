package com.sundy.boot.mapStruct;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Target {
    private Long id;
    private Long age;
    private String userNick;
    private String weight;
}
