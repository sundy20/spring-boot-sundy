package com.sundy.boot;

import com.sundy.boot.mapStruct.BeanConverter;
import com.sundy.boot.mapStruct.Source;
import com.sundy.boot.mapStruct.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootSundyApplication.class)
public class SpringBootSundyApplicationTests {

    @Resource
    private BeanConverter beanConverter;

    @Test
    public void contextLoads() {

    }

    @Test
    public void testMapStruct() {
        final Source source = Source.builder()
                .id(1L)
                .age(18L)
                .userNick("nick")
                .build();
        final Target result = beanConverter.convert(source);
        System.out.println(result);
    }
}
