package com.seezoon.eagle.core.utils;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

@Deprecated
public class MDCUtils {

	private static String THREADID_KEY = "threadId";
	private static String HOST = "host";
	
	public static void putHost(String host){
		put(HOST,host);
	}
	public static void put(){
		put(null);
		putHost(NetUtils.getLocalHost());
	}
	public static void put(String value){
		put(THREADID_KEY,value);
	}
	/**
	 * 放入
	 * @param key
	 * @param value
	 */
	public static void put(String key,String value){
		if (StringUtils.isEmpty(value)) {
			value = generateId();
		}
		MDC.put(key, value);
	}
	public static String get(){
		return get(THREADID_KEY);
	}
	public static String get(String key){
		String value =  MDC.get(key);
		if (StringUtils.isEmpty(value)) {
			value = generateId();
		}
		return value;
	}
	private static String generateId(){
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		return uuid;
	}
	public static void main(String[] args) {
		System.out.println(generateId());
	}
}
