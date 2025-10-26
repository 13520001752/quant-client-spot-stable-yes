package com.magic.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author sevenmagicbeans
 * @date 2022/8/26
 */
public class MybatisplusApplication {
    //private static final String url = "jdbc:mysql://127.0.0.1:3306/quant1?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC";
    private static final String url = "jdbc:mysql://127.0.0.1:3306/quant2?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC";

    private static final String username = "root";
    private static final String password = "Aa@123";

    public static void main(String[] args) {
        List<String> tables = new ArrayList<>();
//        tables.add("t_api_key");
        tables.add("t_config");
//        tables.add("t_order");
//        tables.add("t_symbol_config");

        String path = "/Users/haiyangpan/code-wf2/quant/v2/br-v2-client-config";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("magic beans")               //作者
                            .outputDir(path + "//tmp//core//src//main//java")    //输出路径(写到java目录)
                            .enableSwagger()           //开启swagger
//                            .commentDate("yyyy-MM-dd")
                            .fileOverride();            //开启覆盖之前生成的文件

                })
                .packageConfig(builder -> {
                    builder.parent("com.magic")
                           // .moduleName("")
                            .entity("mybatisplus.entity")
                            .service("mybatisplus.service")
                            .serviceImpl("mybatisplus.service.impl")
//                            .service("service")
//                            .serviceImpl("service.impl")
                            .controller("mybatisplus.controller")
                            .mapper("mybatisplus.mapper")
                            .xml("mybatisplus.mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, path + "/tmp/core/src/main/resources/mapper"));
//                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/tmp/core/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)
                            //.addTablePrefix("p_")
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .entityBuilder()
                            .enableLombok()
                            .logicDeleteColumnName("deleted")
                            .enableTableFieldAnnotation()
                            .controllerBuilder()
                            .formatFileName("%sController")
                            .enableRestStyle()
                            .mapperBuilder()
                            .enableBaseResultMap()  //生成通用的resultMap
                            .superClass(BaseMapper.class)
                            .formatMapperFileName("%sMapper")
                            .enableMapperAnnotation()
                            .formatXmlFileName("%sMapper");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}
