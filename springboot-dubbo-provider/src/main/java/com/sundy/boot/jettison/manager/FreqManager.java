package com.sundy.boot.jettison.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zeng.wang
 * @description 库存频次管理
 */
@Slf4j
@Component
public class FreqManager {

    /**
     * check频次
     */
    public boolean checkFreq(Long userId, Set<String> freqKeys) {
        return false;
    }

    /**
     * 消耗频次
     */
    public boolean addFreq(Long userId, Set<String> freqKeys) {
        return false;
    }

    /**
     * 消耗频次
     */
    public boolean reduceFreq(Long userId, Set<String> freqKeys) {
        return false;
    }
}
