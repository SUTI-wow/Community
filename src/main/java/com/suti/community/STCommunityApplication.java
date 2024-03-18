package com.suti.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//一般程序的入口采用这个注解
@SpringBootApplication
public class STCommunityApplication {

	public static void main(String[] args) {
		//Spring容器会被自动创建
		SpringApplication.run(STCommunityApplication.class, args);
	}

}
