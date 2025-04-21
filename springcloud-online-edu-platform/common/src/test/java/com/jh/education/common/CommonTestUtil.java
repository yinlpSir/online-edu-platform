package com.jh.education.common;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.github.yulichang.base.MPJBaseMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class CommonTestUtil {

    public static void generateCode(String modelName, String... tableName) {
        FastAutoGenerator.create("jdbc:mysql://172.20.10.4:3308/online_education_system", "root", "root")
                .globalConfig(builder -> {
                    builder
                            .disableOpenDir()
                            .author("jh")
                            .outputDir("src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.jh.education") // 设置父包名
                            .moduleName(modelName) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "src\\main\\resources\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder
                            .addInclude(tableName) // 设置需要生成的表名
                            .entityBuilder()
                            .disableSerialVersionUID()
                            .enableLombok()
                            .controllerBuilder()
                            .enableRestStyle()
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .mapperBuilder()
                            .superClass(MPJBaseMapper.class);
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

    @Test
    public void test() {
        generateCode("assignment", "assignment", "question", "options");
    }

}
