package com.seezoon.httpclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.junit.Test;

import com.seezoon.eagle.httpclient.HttpClientServiceBean;


/**
 * Unit test for simple App.
 */
public class AppTest{

	@Test
	public void t1() throws MalformedURLException{
		URL url = new URL("http://www.baidu.com/1");
		System.out.println(url.getProtocol());
	}
	@Test
	public void t2() throws ParseException, IOException{
		HttpClientServiceBean bean = new HttpClientServiceBean();
		Map<String,String> map = new HashMap<String,String>();
		map.put("a", "a");
		String httpGet = bean.httpGet("http://localhost:8080/seezoon-admin/sys/dict/list", map, "UTF-8");
		System.out.println(httpGet);
	}
}
