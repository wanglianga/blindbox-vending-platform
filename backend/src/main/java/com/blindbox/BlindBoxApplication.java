package com.blindbox;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.blindbox.mapper")
public class BlindBoxApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlindBoxApplication.class, args);
    }
}
