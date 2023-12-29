package com.sundy.boot.inventory.configSync;

import com.sundy.boot.inventory.DO.ItemDO;
import com.sundy.boot.inventory.dao.ItemDAO;
import com.sundy.boot.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FreqConfigSyncJob {

    public String process() {
        ItemDAO itemDAO = ApplicationContextUtil.getBean(ItemDAO.class);
        FreqSync freqSync = ApplicationContextUtil.getBean(FreqSync.class);
        log.info("FreqConfigSyncJob start");
        List<ItemDO> itemDOList = itemDAO.getAllAvailFreqItems();
        freqSync.sync(itemDOList.stream().map(ItemDO::getNid).collect(Collectors.toSet()));
        log.info("FreqConfigSyncJob end");
        return "";
    }
}
