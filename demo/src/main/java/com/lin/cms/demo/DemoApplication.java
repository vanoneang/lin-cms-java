package com.lin.cms.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 资源文件路径，可以是数据多个文件地址
 * 可以是classpath地址如：
 * "classpath:/com/myco/app.properties"
 * 也可以是对应的文件系统地址如：
 * "file:/path/to/file"
 */

@SpringBootApplication
@MapperScan(basePackages = {"com.lin.cms.demo.mapper"})
@PropertySources({
        @PropertySource("classpath:com/lin/cms/demo/plugins/poem/plugin.properties")
})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}