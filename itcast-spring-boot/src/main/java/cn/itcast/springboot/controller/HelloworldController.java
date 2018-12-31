package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloworldController {
    @Autowired
    private Environment environment;

    @GetMapping("info")
    private String Info(){
       return   "Hellor Spring boot url=" + environment.getProperty("url");
    }
}
