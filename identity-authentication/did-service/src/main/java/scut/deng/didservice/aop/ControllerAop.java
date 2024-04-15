package scut.deng.didservice.aop;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import scut.deng.didservice.annotation.ControllerLogs;
import scut.deng.didservice.util.JsonUtil;
import scut.deng.didservice.util.SnowflakeUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerAop {
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(scut.deng.didservice.annotation.ControllerLogs)")
    public void controllerLogs(){}

    @Before("controllerLogs()")
    public void before(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        log.info("Request URL  :{}", request.getRequestURI());
        log.info("HTTP Method  :{}", request.getMethod());
        log.info("User-Agent   :{}", request.getHeader("User-Agent"));
        log.info("Description  :{}", getDescription(joinPoint));
        log.info("Class Method :{}.{} begin,Request Args :{}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                JsonUtil.objectToString(Arrays.stream(joinPoint.getArgs()).
                        map(o -> {if (o instanceof MultipartFile) {
                            return String.format("{file}, fileName:%s, contentType:%s, fileSize:%d", ((MultipartFile)o).getOriginalFilename(), ((MultipartFile)o).getContentType(), ((MultipartFile)o).getSize());
                        } else if  (o instanceof HttpServletResponse) {
                            return "{HttpServletResponse}";
                        } else {
                            return o;
                        }})
                        .toArray()));
    }


    /**
     * 获取注解描述详情
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    public static String getDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Class<?> targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] classes = method.getParameterTypes();
                if (classes.length == args.length) {
                    description.append(method.getAnnotation(ControllerLogs.class).description());
                    break;
                }
            }
        }
        return description.toString();
    }

}
