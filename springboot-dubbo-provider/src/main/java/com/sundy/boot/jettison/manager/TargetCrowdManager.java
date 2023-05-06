package com.sundy.boot.jettison.manager;

import com.google.common.collect.Lists;
import com.sundy.boot.jettison.config.ResConfigDTO;
import com.sundy.boot.jettison.config.ShelfConfigDTO;
import com.sundy.share.flowApi.UserParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zeng.wang
 * @description 人群定向管理
 */
@Slf4j
@Component
public class TargetCrowdManager {

    /**
     * 人群匹配的货架
     */
    public List<ShelfConfigDTO> matchedTargetShelfs(UserParam userParam, ResConfigDTO resConfigDTO) {
        if (Objects.isNull(resConfigDTO)) {
            return Collections.emptyList();
        }
        List<ShelfConfigDTO> shelfConfigs = Lists.newArrayList();
        List<ShelfConfigDTO> shelfs = resConfigDTO.getShelfConfigs();
        shelfs.forEach(shelfDTO -> {
            Map<String, Object> target = shelfDTO.getTarget();
            Map<String, Object> channel = shelfDTO.getChannel();
            if (CollectionUtils.isEmpty(target) && CollectionUtils.isEmpty(channel)) {
                shelfConfigs.add(shelfDTO);
            }

            //定向check：用户颗粒度和城市配置交集；渠道check 最终得到命中的货架列表 TODO


        });
        return shelfConfigs;
    }
}
