package com.cheeeese.global.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.cheeeese..application..*Service.*(..))")
    public void applicationLayer() {}

    @Around("applicationLayer()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("[▶️ Start] {}.{} | args = {}", className, methodName, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();

            logger.info("[✅ End] {}.{} | took = {}ms | return = {}", className, methodName, (end - start), result);

            return result;
        } catch (Throwable ex) {
            long end = System.currentTimeMillis();
            logger.error("[‼️ Exception] {}.{} | took = {}ms | message = {}", className, methodName, (end - start), ex.getMessage(), ex);

            throw ex;
        }
    }
}
