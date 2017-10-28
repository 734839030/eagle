package com.seezoon.eagle.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 请勿频繁实例化
 * 
 * @author hdf 2017年10月21日 doc @see
 *         http://hc.apache.org/httpcomponents-client-4.5.x/tutorial/pdf/httpclient-tutorial.pdf
 *         http://hc.apache.org/httpcomponents-client-4.5.x/examples.html
 */
public class HttpClientServiceBean {
	private static String default_chartSet = "UTF-8";
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(HttpClientServiceBean.class);

	private HttpClientConfig httpClientConfig;
	private IdleConnectionMonitorThread idleConnectionMonitorThread;
	private CloseableHttpClient httpClient;

	public HttpClientServiceBean() {
		httpClientConfig = HttpClientConfig.custom().build();
		this.init();
	}

	public HttpClientServiceBean(HttpClientConfig httpClientConfig) {
		this.httpClientConfig = httpClientConfig;
		this.init();
	}

	private void init() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout())// 获取连接等待时间
				.setConnectTimeout(httpClientConfig.getConnectTimeout())// 连接超时
				.setSocketTimeout(httpClientConfig.getSocketTimeout())// 获取数据超时
				.build();
		// https http 支持
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", plainsf).register("https", sslsf).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 最大连接数
		cm.setMaxTotal(httpClientConfig.getMaxTotal());
		// 单个站点最大连接数
		cm.setDefaultMaxPerRoute(httpClientConfig.getMaxPerRoute());
		// 长连接
		cm.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).build());
		// 连接不活跃多久检查毫秒 并不是100 % 可信
		cm.setValidateAfterInactivity(httpClientConfig.getValidateAfterInactivity());
		httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig)
				.setConnectionTimeToLive(httpClientConfig.getConnTimeToLive(), TimeUnit.MILLISECONDS)// 连接最大存活时间
				.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientConfig.getRetyTimes(), true))// 重试次数
																											// 0为不重试
				.build();
		idleConnectionMonitorThread = new IdleConnectionMonitorThread(cm,httpClientConfig);
		idleConnectionMonitorThread.setDaemon(true);
		idleConnectionMonitorThread.start();
	}

	private String execute(HttpRequestBase request, String chartSet) throws ParseException, IOException {
		CloseableHttpResponse respone;
		respone = httpClient.execute(request);
		try {
			
		} finally {
			respone.close();
		}
		if (respone != null) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("request:{},respone:{}", JSON.toJSONString(request), JSON.toJSONString(respone));
				}
				int statusCode = respone.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {// 成功
					HttpEntity entity = respone.getEntity();
					if (entity != null) {
						return EntityUtils.toString(entity, chartSet);
					}
				} 
			} finally {
				respone.close();
			}
		}
		return null;
	}

	private UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, String> params, String chartSet)
			throws UnsupportedEncodingException {
		UrlEncodedFormEntity entity = null;
		if (null != params && !params.isEmpty()) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : params.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			entity = new UrlEncodedFormEntity(list, chartSet);
		}
		return entity;
	}

	// get
	public String httpGet(String url, Map<String, String> params, String chartSet) throws ParseException, IOException {
		HttpGet httpGet = new HttpGet(url);
		UrlEncodedFormEntity urlEncodedFormEntity = this.getUrlEncodedFormEntity(params, chartSet);
		if (urlEncodedFormEntity != null) {
			String string = EntityUtils.toString(urlEncodedFormEntity);
			URL urlO = new URL(url);
			String query = urlO.getQuery();
			if (StringUtils.isNotEmpty(query)) {
				httpGet.setURI(URI.create(url + "&" + string));
			} else {
				httpGet.setURI(URI.create(url + "?" + string));
			}
		}
		return this.execute(httpGet, chartSet);
	}
	public <T> T httpPost(String url, Map<String, String> params, String chartSet,Class<T> clazz) throws ParseException, IOException{
		String content = this.httpPost(url, params, chartSet);
		if (StringUtils.isNotEmpty(content)) {
			return JSON.parseObject(content, clazz);
		}
		return null;
		
	}
	public <T> T httpGet(String url, Map<String, String> params, String chartSet,Class<T> clazz) throws ParseException, IOException{
		String content = this.httpGet(url, params, chartSet);
		if (StringUtils.isNotEmpty(content)) {
			return JSON.parseObject(content, clazz);
		}
		return null;
		
	}
	// post
	public String httpPost(String url, Map<String, String> params, String chartSet)
			throws ParseException, IOException {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(this.getUrlEncodedFormEntity(params, chartSet));
		return this.execute(httpPost, chartSet);
	}

	public void shutdown() {
		try {
			idleConnectionMonitorThread.shutdown();
			// 关闭所有连接，一般是在jvm 停止时候调用
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// 监控http connection的空闲线程
class IdleConnectionMonitorThread extends Thread {
	private final PoolingHttpClientConnectionManager connMgr;
	private final HttpClientConfig httpClientConfig;
	private volatile boolean shutdown;

	public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr,HttpClientConfig httpClientConfig) {
		super("Connection Manager");
		this.connMgr = connMgr;
		this.httpClientConfig = httpClientConfig;

	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					wait(5000);
					connMgr.closeExpiredConnections();
					connMgr.closeIdleConnections(httpClientConfig.getIdleTimeToDead(), TimeUnit.MILLISECONDS);
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

	public void shutdown() {
		shutdown = true;
		synchronized (this) {
			notifyAll(); // 让run方法不再wait
		}
	}
}