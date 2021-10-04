package com.space.core.aop;

import com.space.core.FieldInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xulinglin
 */
@Aspect
@Component
public class ExecutedBindAop {

    @Around("@annotation(com.space.core.annotation.ExecutedBind)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal = pjp.proceed();
        if(retVal instanceof List){
            return FieldInterceptor.setFieldValue((List) retVal);
        }
        return retVal;
    }
}
