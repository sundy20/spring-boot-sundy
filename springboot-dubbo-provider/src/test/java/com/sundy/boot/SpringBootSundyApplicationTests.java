package com.sundy.boot;

import com.sundy.boot.mapStruct.BeanConvertMapper;
import com.sundy.boot.mapStruct.BeanConvertMapperSpring;
import com.sundy.boot.mapStruct.Source;
import com.sundy.boot.mapStruct.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootSundyApplication.class)
public class SpringBootSundyApplicationTests {

    @Autowired
    private BeanConvertMapperSpring beanConvertMapperSpring;

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

        Target target = BeanConvertMapper.INSTANCE.source2target(source);
        System.out.println(target);

        Target target1 = beanConvertMapperSpring.source2target(source);
        System.out.println(target1);
    }
}
