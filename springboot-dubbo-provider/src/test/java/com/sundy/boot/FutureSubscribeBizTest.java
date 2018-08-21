package com.sundy.boot;

import com.sundy.boot.biz.FuturesSubscribeBiz;
import com.sundy.boot.domain.FuturesSubscribe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootSundyApplication.class)
public class FutureSubscribeBizTest {

    private static final Logger log = LoggerFactory.getLogger(FutureSubscribeBizTest.class);

    @Autowired
    private FuturesSubscribeBiz futuresSubscribeBiz;

    /**
     * 测试批量删除 事务
     */
    @Test
    public void testDeleteSubscribeFutures() {

        try {

            List<FuturesSubscribe> futuresSubscribeList = new ArrayList<>();

            FuturesSubscribe futuresSubscribe = new FuturesSubscribe();

            futuresSubscribe.setContractId(9L);

            futuresSubscribe.setSort(1);

            futuresSubscribe.setUserId(1L);

            futuresSubscribe.setDeleted("N");

            futuresSubscribeList.add(futuresSubscribe);

            FuturesSubscribe futuresSubscribe1 = new FuturesSubscribe();

            futuresSubscribe1.setContractId(8L);

            futuresSubscribe1.setSort(0);

            futuresSubscribe1.setUserId(1L);

            futuresSubscribe1.setDeleted("N");

            futuresSubscribeList.add(futuresSubscribe1);

            int batchUpdateByUserIdCount = futuresSubscribeBiz.batchUpdateByUserId(1L, futuresSubscribeList);

            System.out.println("批量更新数量：" + batchUpdateByUserIdCount);

        } catch (Exception e) {

            log.error("FutureSubscribeBizTest.testDeleteSubscribeFutures error", e);
        }
    }

    /**
     * 测试批量更新
     */
    @Test
    public void testBatchUpdate() throws Exception {

        List<FuturesSubscribe> futuresSubscribeList = new ArrayList<>();

        FuturesSubscribe futuresSubscribe = new FuturesSubscribe();

        futuresSubscribe.setId(857L);

        futuresSubscribe.setContractId(9L);

        futuresSubscribe.setSort(0);

        futuresSubscribe.setUserId(1L);

        futuresSubscribe.setUpdateAt(new Date());

        futuresSubscribeList.add(futuresSubscribe);

        FuturesSubscribe futuresSubscribe1 = new FuturesSubscribe();

        futuresSubscribe1.setId(858L);

        futuresSubscribe1.setContractId(12L);

        futuresSubscribe1.setSort(0);

        futuresSubscribe1.setUserId(1L);

        futuresSubscribe1.setUpdateAt(new Date());

        futuresSubscribeList.add(futuresSubscribe1);

        futuresSubscribeBiz.batchUpdate(futuresSubscribeList);
    }

    /**
     * 测试分页获取
     */
    @Test
    public void testFindPage() throws Exception {

//        Map<String, Object> stringObjectMap = new HashMap<>();
//
//        stringObjectMap.put("userId", 1);
//
//        PageInfo<FuturesSubscribe> pageInfo = futuresSubscribeBiz.findPage(2, 2, stringObjectMap);
//
//        System.out.println("批量更新数量： " + JSON.toJSONString(pageInfo, SerializerFeature.WriteMapNullValue));

        futuresSubscribeBiz.findByUserId(1L);

//        futuresSubscribeBiz.get(1L);
//
//        FuturesSubscribe futuresSubscribe = new FuturesSubscribe();
//
//        futuresSubscribe.setUserId(1L);
//
//        futuresSubscribeBiz.findByExample(futuresSubscribe);

    }
}
