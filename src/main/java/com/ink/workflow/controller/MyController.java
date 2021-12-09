package com.ink.workflow.controller;

import com.ink.workflow.annotation.MyAnno;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MyController {

    @MyAnno(description = "测试")
    @GetMapping("/test")
    public String test() {
        System.out.println("123123");
        return "ok";
    }
}
