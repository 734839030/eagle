package com.seezoon.eagle.dubbo.container;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.container.Container;
import com.seezoon.eagle.core.utils.NDCUtils;
import com.seezoon.eagle.dubbo.filter.WebFilter;
import com.seezoon.eagle.dubbo.utils.PropertiesLoader;

/**
 * 重写jetty contain java -jar 启动时候传入spring jetty1 不传默认只使用spring
 * 
 * @author hdf
 *
 */
public class JettyContainer implements Container {

	private static Logger logger = LoggerFactory.getLogger(JettyContainer.class);
	private static int port;
	private static int maxThread;
	private static String contextPath;
	Connector connector = null;

	@Override
	public void start() {
		boolean config = getConfig();
		if (!config) {
			return;
		}
		Server server = new Server();// 创建jetty web容器
		server.setStopAtShutdown(true);// 在退出程序是关闭服务
		// 创建连接器，每个连接器都是由IP地址和端口号组成，连接到连接器的连接将会被jetty处理,属于优化项
		connector = new SelectChannelConnector();// 创建一个连接器
		// connector.setHost("127.0.0.1");// ip地址
		connector.setPort(port);// 连接的端口号
		server.addConnector(connector);// 添加连接
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(maxThread);
		server.setThreadPool(threadPool);

		Context root = new Context(server, contextPath, Context.NO_SESSIONS);
		ContextLoaderListener listener = new ContextLoaderListener();
		/** 设置spring配置文件位置 */
		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("contextConfigLocation", "classpath*:META-INF/spring/root.spring.xml,classpath*:spring-context*.xml");
		root.setInitParams(initParams);
		root.addEventListener(listener);
		//dubbo优雅关机
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ProtocolConfig.destroyAll();
			}
		});
        /**spring control*/
        ServletHolder holder = new ServletHolder(new DispatcherServlet());
        holder.setInitParameter("contextConfigLocation", "classpath*:spring-mvc*.xml");
        root.addServlet(holder, "/*");
        /**线程过滤**/
        FilterHolder filterHolder1 = new FilterHolder(WebFilter.class);
        root.addFilter(filterHolder1, "/*", Handler.DEFAULT);
        /**字符过滤器**/
        FilterHolder filterHolder2 = new FilterHolder(CharacterEncodingFilter.class);
        filterHolder2.setInitParameter("encoding", "UTF-8");
        filterHolder2.setInitParameter("forceEncoding", "true");
        root.addFilter(filterHolder2, "/*", Handler.DEFAULT);
        
		try {
			server.start();
			logger.info("jetty service started port :{},maxThread:{},contextPath:{}", port, maxThread, contextPath);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to start jetty server on " + NetUtils.getLocalHost() + ":" + port
					+ ", cause: " + e.getMessage(), e);
		}
	}

	private static boolean getConfig() {
		PropertiesLoader loader = new PropertiesLoader("classpath:application.properties");
		Boolean http = loader.getBoolean("dubbo.http");
		if (http) {
			port = loader.getInteger("dubbo.http.port", 8080);
			maxThread = loader.getInteger("dubbo.http.maxthreads", 100);
			contextPath = loader.getProperty("dubbo.http.context", "/");
		}
		return http;
	}

	@Override
	public void stop() {
		try {
			if (this.connector != null) {
				this.connector.close();
				this.connector = null;
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
}
