package com.space.core.aop;

import com.space.core.FieldInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author xulinglin
 */
@Aspect
@Component
public class ExecutedBindAop {

    public static Logger log = Logger.getLogger(FieldInterceptor.class.getName());

    @Around("@annotation(com.space.core.annotation.ExecutedBind)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal = pjp.proceed();
        if(retVal instanceof List){
            return FieldInterceptor.setFieldValue((List) retVal);
        }
        return retVal;
    }
}
