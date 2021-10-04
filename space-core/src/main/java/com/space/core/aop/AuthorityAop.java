package com.space.core.aop;

import com.space.core.bean.Tools;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author xulinglin
 */
@Aspect
@Component
public class AuthorityAop {

    protected static ThreadLocal<Integer> authority = new ThreadLocal<>();

    private static final int NORMAL_PERMISSIONS = 0;//普通权限
    private static final int ADMIN_PERMISSIONS = 1;//管理员权限,跳过执行

    public static Integer get() {
        return authority.get();
    }

    public static void setAdmin() {
         authority.set(ADMIN_PERMISSIONS);
    }

    public static void setNormal() {
        authority.set(NORMAL_PERMISSIONS);
    }

    public static boolean isAdmin() {
        return null != get() && ADMIN_PERMISSIONS == get();
    }

    @Around("@annotation(com.space.core.annotation.Authority)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal =null;
        try {
            setNormal();
            retVal = pjp.proceed();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            authority.remove();
        }
        return retVal;
    }
}
