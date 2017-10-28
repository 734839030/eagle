package com.seezoon.eagle.web.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.seezoon.eagle.core.service.BaseService;

/**
 * 参数校验
 * @author hdf
 *
 */
//@Aspect
public class ParamsValidateInterceptor extends BaseService{
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)") 
	private void anyMethod(){}//定义一个切入点
	
	@Around("anyMethod()")
	public void around(ProceedingJoinPoint pjp) throws Throwable{
		
		Object proceed = pjp.proceed(pjp.getArgs());
	}
	
}
