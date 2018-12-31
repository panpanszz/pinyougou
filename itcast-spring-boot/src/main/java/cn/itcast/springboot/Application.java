package cn.itcast.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 如果是spring boot 的引导类,需要添加@SpringBootApplication注解
 * 默认将扫描该引导类及其子包里面的spring注解
 * */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
