package com.seezoon.eagle.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class MainLauncher {
	/**
	 * 日志对象
	 */
	protected  static Logger logger = LoggerFactory.getLogger(MainLauncher.class);
	
	public static void main(String[] args) {
		// 加载当前路径下和lib/jar
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath*:spring-context.xml",
				"classpath*:lib/*.jar!/spring-context.xml"});
		
		//kill pid 执行
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				synchronized (MainLauncher.class) {
					MainLauncher.class.notify();
				}
			}
		});
		logger.info("========================= MainLauncher start =========================");
		synchronized (MainLauncher.class) {
			try {
				MainLauncher.class.wait();
			} catch (InterruptedException e) {
				
			}
		}
		logger.info("========================= MainLauncher shutdow =========================");
	}
}
