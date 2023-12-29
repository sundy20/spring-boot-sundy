package com.sundy.boot.inventory.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sundy.boot.exception.BizException;
import com.sundy.boot.inventory.DO.ItemDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component("itemDAO")
public class ItemDAO {

    @Resource
    private ItemIService itemIService;

    public ItemDO add(ItemDO freqItemDO) {
        try {
            if (freqItemDO.getNid() == null) {
                //Long nid = sequence.nextValue();
                //freqItemDO.setNid(nid);
            }
            if (itemIService.save(freqItemDO)) {
                return freqItemDO;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    public boolean update(ItemDO freqItemDO) {
        try {
            QueryWrapper<ItemDO> q = new QueryWrapper<>();
            q.eq("nid", freqItemDO.getNid());
            q.last(" LIMIT 1");
            ItemDO one = itemIService.getOne(q);
            boolean ret;
            if (one == null) {
                ret = add(freqItemDO) != null;
            } else {
                QueryWrapper<ItemDO> qm = new QueryWrapper<>();
                qm.eq("nid", freqItemDO.getNid());
                ret = itemIService.update(freqItemDO, qm);
            }
            return ret;
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    public boolean delete(Long nid) {
        throw new UnsupportedOperationException();
    }

    public ItemDO detail(Long nid) {
        QueryWrapper<ItemDO> q = new QueryWrapper<>();
        q.eq("nid", nid);
        q.last(" LIMIT 1");
        return itemIService.getOne(q);
    }

    public IPage<ItemDO> list(IPage<ItemDO> page, QueryWrapper<ItemDO> queryWrapper) {
        return itemIService.page(page, queryWrapper);
    }

    public List<ItemDO> getItemDOs(Set<Long> ids) {
        QueryWrapper<ItemDO> q = new QueryWrapper<>();
        q.in("nid", ids);
        return Optional.ofNullable(itemIService.list(q)).orElse(Collections.emptyList());
    }

    public List<ItemDO> getAllAvailFreqItems() {
        List<ItemDO> itemDOList = itemIService.list();
        return itemDOList != null ? itemDOList : Collections.emptyList();
    }

    public ItemDO getOne(Long nid) {
        QueryWrapper<ItemDO> q = new QueryWrapper<>();
        q.eq("nid", nid);
        q.last(" LIMIT 1");
        return itemIService.getOne(q);
    }
}
