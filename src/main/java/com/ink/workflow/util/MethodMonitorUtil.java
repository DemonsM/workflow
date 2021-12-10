package com.ink.workflow.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.ink.workflow.bean.MethodExecute;
import com.ink.workflow.bean.MethodMonitor;
import com.ink.workflow.bean.MonitorExecute;
import com.ink.workflow.mapper.MethodExecuteMapper;
import com.ink.workflow.mapper.MethodMonitorMapper;
import com.ink.workflow.mapper.MonitorExecuteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class MethodMonitorUtil {
    @Autowired
    private MethodMonitorMapper methodMonitorMapper;
    @Autowired
    private MonitorExecuteMapper monitorExecuteMapper;
    @Autowired
    private MethodExecuteMapper methodExecuteMapper;

    @Autowired
    private AsyncTaskUtil asyncTaskUtil;

    @Async("taskExecutor")
    public void methodMonitor(String classMethod, Map<String, Object> requestArgs, Object responseArgs) throws ClassNotFoundException {
        MethodMonitor methodMonitor = methodMonitorMapper.selectOne(new LambdaQueryWrapper<MethodMonitor>().eq(MethodMonitor::getClassMethod, classMethod));
        if (Objects.nonNull(methodMonitor)) {
            if (Objects.equals(new Gson().toJson(requestArgs), JsonParser.parseString(methodMonitor.getRequestArgs()).toString())
                    && Objects.equals(new Gson().toJson(responseArgs), JsonParser.parseString(methodMonitor.getResponseArgs()).toString())) {
                MonitorExecute monitorExecute = monitorExecuteMapper.selectOne(new LambdaQueryWrapper<MonitorExecute>().eq(MonitorExecute::getMonitorId, methodMonitor.getId()));
                if (Objects.nonNull(monitorExecute)) {
                    Long executeId = monitorExecute.getExecuteId();
                    MethodExecute methodExecute = methodExecuteMapper.selectById(executeId);
                    String methodName = methodExecute.getClassMethod().split("\\.")[methodExecute.getClassMethod().split("\\.").length - 1];
                    String className = methodExecute.getClassMethod().substring(0, methodExecute.getClassMethod().lastIndexOf("."));
                    Object bean = SpringContextUtil.getBean(Class.forName(className));
                    Object res = asyncTaskUtil.taskCustomized(bean, methodName);
                    log.info("[{}]执行结果为：[{}]", methodExecute.getClassMethod(), res);
                    if (Objects.equals(methodExecute.getResponseArgs(), new Gson().toJson(res))) {
                        log.info("[{}]符合执行结果：[{}]", methodExecute.getClassMethod(), methodExecute.getResponseArgs());
                    }
                }
            }
        }
    }
}
