package com.space.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("com.space.springboot.mapper")
public class SpaceSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceSpringbootApplication.class, args);
    }

}
