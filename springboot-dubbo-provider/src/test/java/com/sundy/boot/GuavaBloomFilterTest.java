package com.sundy.boot;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zeng.wang
 * @description guava 布隆过滤器测试
 * @date 2019-07-02
 */
public class GuavaBloomFilterTest {

    /**
     * 构造方法中有两个比较重要的参数，一个是预计存放多少数据，一个是可以接受的误报率。
     * 测试demo分别是 1000W 以及 0.01
     */
    @Test
    public void guavaTest() {
        long star = System.currentTimeMillis();
        BloomFilter<Integer> filter = BloomFilter.create(
                Funnels.integerFunnel(),
                10000000,
                0.01);
        for (int i = 0; i < 10000000; i++) {
            filter.put(i);
        }
        Assert.assertTrue(filter.mightContain(1));
        Assert.assertTrue(filter.mightContain(2));
        Assert.assertTrue(filter.mightContain(3));
        Assert.assertFalse(filter.mightContain(10000000));
        long end = System.currentTimeMillis();
        System.out.println("执行时间：" + (end - star));
    }
}
