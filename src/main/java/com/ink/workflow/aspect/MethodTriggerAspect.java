package com.ink.workflow.aspect;

import com.google.gson.Gson;
import com.ink.workflow.annotation.MethodTrigger;
import com.ink.workflow.util.MethodMonitorUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class MethodTriggerAspect {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodTriggerAspect.class);
    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    private MethodMonitorUtil monitorUtil;

    @Pointcut("@annotation(com.ink.workflow.annotation.MethodTrigger)")
    public void methodTrigger() {
    }

    @Before("methodTrigger()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String methodDescription = getAspectLogDescription(joinPoint);

        // 打印请求相关参数
        LOGGER.info("========================================== Start ==========================================");
        // 打印请求 url
        LOGGER.info("URL            : {}", request.getRequestURL().toString());
        // 打印描述信息
        LOGGER.info("Description    : {}", methodDescription);
        // 打印 Http method
        LOGGER.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        LOGGER.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        LOGGER.info("IP             : {}", request.getRemoteAddr());
        // 打印请求入参
        LOGGER.info("Request Args   : {}", new Gson().toJson(joinPoint.getArgs()));
    }

    /**
     * 在切点之后织入
     */
    @After("methodTrigger()")
    public void doAfter() {
        LOGGER.info("=========================================== End ===========================================" + LINE_SEPARATOR);

    }

    /**
     * 环绕
     */
    @Around("methodTrigger()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LOGGER.info("========================================== Around Start ==========================================");
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Map<String, Object> requestParaMap = new HashMap<>();
        Object[] args = proceedingJoinPoint.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            requestParaMap.put(parameterNames[i], args[i]);
        }
        String classMethod = proceedingJoinPoint.getSignature().getDeclaringTypeName() + "." + proceedingJoinPoint.getSignature().getName();

        //执行方法监听
        monitorUtil.methodMonitor(classMethod, requestParaMap, result);

        // 打印请求入参
        LOGGER.info("Request Args   : {}", new Gson().toJson(requestParaMap));
        // 打印出参
        LOGGER.info("Response Args  : {}", new Gson().toJson(result));
        // 执行耗时
        LOGGER.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        LOGGER.info("=========================================== Around End ===========================================" + LINE_SEPARATOR);
        return result;
    }

    /**
     * 获取切面注解的描述
     */
    private String getAspectLogDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class<?> targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] clazzArr = method.getParameterTypes();
                if (clazzArr.length == arguments.length) {
                    description.append(method.getAnnotation(MethodTrigger.class).description());
                    break;
                }
            }
        }
        return description.toString();
    }
}
