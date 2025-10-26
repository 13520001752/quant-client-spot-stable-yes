package com.magic.cache.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

/**
 * redis test
 *
 * @author sevenmagicbeans
 * @date 2022/8/24
 */
@SpringBootTest
@ComponentScan(basePackages = "ap")
public class RedisTest {

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    @Test
    void redisTest() {
        redisCacheUtils.set("r_test2","test");

        Object ob = redisCacheUtils.get("r_test2");

        System.out.println("redis value get:"+(String) ob);


    }

}
