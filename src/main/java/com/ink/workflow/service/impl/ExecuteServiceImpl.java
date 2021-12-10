package com.ink.workflow.service.impl;

import com.ink.workflow.service.ExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecuteServiceImpl implements ExecuteService {
    @Override
    public String test1() {
        log.info("test1执行了");
        return "test1";
    }
}
