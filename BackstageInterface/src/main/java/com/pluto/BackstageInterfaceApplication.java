package com.pluto;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@MapperScan("com.pluto.mapper")
@EnableWebMvc
public class BackstageInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackstageInterfaceApplication.class, args);
	}

}
