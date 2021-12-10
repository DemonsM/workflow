package com.ink.workflow.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AsyncTaskUtil {
    /**
     * 异步任务
     *
     * @param obj        已初始化对象（因为方法invoke的时候用此对象调用）
     * @param methodName 方法名
     * @param args       方法参数
     * @return
     */
    public Object taskCustomized(Object obj, String methodName, Object... args) {
        Class<?> clazz = obj.getClass();
        try {
            Class<?>[] classes = null;
            if (args != null && args.length != 0) {
                List<Object> parameters = Arrays.asList(args);
                classes = new Class[parameters.size()];
                for (int i = 0; i < parameters.size(); i++) {
                    classes[i] = parameters.get(i).getClass();
                }
            }
            Method method = clazz.getMethod(methodName, classes);
            return method.invoke(obj, args);
        } catch (Exception e) {
            log.error("ASYNC_REFLECT:出错了...", e.getCause());
        }
        return null;
    }

}
