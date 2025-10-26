package com.magic.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * redis main test
 *
 * @author sevenmagicbeans
 * @date 2022/8/12
 */
public class GuavaCacheMainTest {

    public static void main(String[] args) {
        // 通过CacheBuilder构建一个缓存实例
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100) // 设置缓存的最大容量
                .expireAfterWrite(1, TimeUnit.SECONDS) // 设置缓存在写入XX时间后失效
                .concurrencyLevel(10) // 设置并发级别为10
                .recordStats() // 开启缓存统计
                .build();
        // 放入缓存
        cache.put("key", "value");
        // 获取缓存
        String value = cache.getIfPresent("key");


        System.out.println("start:"+value);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String value2 = cache.getIfPresent("key");

        System.out.println("later:"+value2);



    }
}
