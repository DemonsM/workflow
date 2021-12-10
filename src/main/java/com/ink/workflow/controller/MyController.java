package com.ink.workflow.controller;

import com.ink.workflow.annotation.MethodTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MyController {

    @MethodTrigger(description = "测试")
    @GetMapping("/test")
    public String test(@RequestParam String id, @RequestParam String username) {
        log.info("id:[{}]", id);
        log.info("username:[{}]", username);
        return "ok";
    }
}
