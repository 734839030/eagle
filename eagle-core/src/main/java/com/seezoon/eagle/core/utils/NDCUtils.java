package com.seezoon.eagle.core.utils;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.NDC;

public class NDCUtils {

	public static void push(String value){
		if (StringUtils.isEmpty(value)) {
			value = generateId();
		}
		NDC.clear();
		NDC.push(value);
	}
	public static String push(){
		String value = pop();
		clear();
		NDC.push(value);
		return value;
	}
	public static String peek(){
		String value =  NDC.peek();
		if (StringUtils.isEmpty(value)) {
			value = generateId();
		}
		return value;
	}
	public static String pop(){
		String value =  NDC.pop();
		if (StringUtils.isEmpty(value)) {
			value = generateId();
		}
		return value;
	}
	public static void clear(){
		NDC.clear();
	}
	public static String  get(){
		return generateId();
	}
	private static String generateId(){
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		return uuid;
	}
}
