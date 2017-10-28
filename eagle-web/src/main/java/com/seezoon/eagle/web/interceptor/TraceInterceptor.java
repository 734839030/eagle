package com.seezoon.eagle.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.seezoon.eagle.core.service.BaseService;
import com.seezoon.eagle.core.utils.NDCUtils;

/**
 * 线程计时
 * @author hdf
 *
 */
public class TraceInterceptor extends BaseService implements HandlerInterceptor {
	
	ThreadLocal<StopWatch> threadLocal = new ThreadLocal<>();
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		NDCUtils.push();
		StopWatch watch = new StopWatch();
		watch.start();
		threadLocal.set(watch);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		StopWatch stopWatch = threadLocal.get();
		stopWatch.stop();
		String requestURI = request.getRequestURI();
		logger.info("{} comleted use {} ms",requestURI,stopWatch.getTotalTimeMillis());
		threadLocal.remove();
		NDCUtils.clear();
	}

	
}
