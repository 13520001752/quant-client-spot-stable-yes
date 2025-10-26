package com.magic.configuration;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author sevenmagicbeans
 * @date 2022/8/26
 */
@Configuration
@EnableOpenApi
@EnableKnife4j
@Profile("!prod")
@Import(BeanValidatorPluginsConfiguration.class)//导入其他的配置类 让配置生效
public class SwaggerConfiguration {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.OAS_30)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
//        swagger的API扫描提供了四种方式，分别如下：
//        1、RequestHandlerSelectors.any() 匹配任何controller的接口
//        2、RequestHandlerSelectors.withClassAnnotation() 扫描含有类注解的
//        3、RequestHandlerSelectors.withMethodAnnotation() 扫描含有方法注解的
//        3、RequestHandlerSelectors.basePackage() 扫描指定包路径

//                .apis(RequestHandlerSelectors.basePackage("com.magic.controller"))
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .paths(PathSelectors.any())
    }


    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("springboot脚手架 API 文档(注意新模块修改本title)")
                .description("本文档描述了各个微服务接口定义")
                .version("1.0")
                .contact(new Contact("magic beans", "http://localhost",
                        "magic"))
                .build();
    }

}
