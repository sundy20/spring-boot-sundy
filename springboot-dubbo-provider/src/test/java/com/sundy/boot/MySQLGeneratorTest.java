package com.sundy.boot;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.junit.jupiter.api.Test;

/**
 * MySQL 代码生成
 *
 * @since 3.5.3
 */
public class MySQLGeneratorTest extends BaseGeneratorTest {

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://xxxx:3306/user?serverTimezone=Asia/Shanghai", "root", "123456")
            .schema("user")
            .build();

    @Test
    public void testSimple() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.execute();
    }
}