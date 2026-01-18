package com.cheeeese.global.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTimeMs = System.currentTimeMillis() - start;

            MDC.put("executionTimeMs", String.valueOf(executionTimeMs));
            MDC.put("methodStatus", "SUCCESS");

            logger.info("[✅ End] {}.{} | took = {}ms",
                    className, methodName, executionTimeMs);

            return result;
        } catch (Throwable ex) {
            long executionTimeMs = System.currentTimeMillis() - start;

            MDC.put("executionTimeMs", String.valueOf(executionTimeMs));
            MDC.put("methodStatus", "EXCEPTION");
            MDC.put("exceptionType", ex.getClass().getSimpleName());

            logger.error("[‼️ Exception] {}.{} | took = {}ms | message = {}",
                    className, methodName, executionTimeMs, ex.getMessage());

            throw ex;
        } finally {
            MDC.remove("executionTimeMs");
            MDC.remove("methodStatus");
            MDC.remove("exceptionType");
        }
    }
}
