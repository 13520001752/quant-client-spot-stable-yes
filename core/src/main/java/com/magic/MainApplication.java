package com.magic;

import cn.hutool.core.date.DateUtil;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * 启动的主入口
 */

@Slf4j
@ServletComponentScan(basePackages = "com.magic")
//@MapperScan("com.magic.mybatisplus.mapper")
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,

//        DataSourceAutoConfiguration.class,
//        DruidDataSourceAutoConfigure.class,
})
@EnableScheduling
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        log.info("Start Application Success ,time:{}", DateUtil.now());
    }
    // hy bn
    // apikey: UKx2Ye8zB8qoM69LBjYNgRvOlGMjgPT6tndiH0zQobBIVCiBsCGls7CZ4APFp76E
    // secret: lctaHPZFFuN4EO3O9ELTPdEGyTZ3bW53WI5eyqsXWvUDUXYiOk2Rn11PLB81BTCF

//    @PostConstruct
//    void setUpTimeZone() {
//        // 设置用户时区为 UTC 0
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//    }
}
