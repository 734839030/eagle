package com.seezoon.eagle.dubbo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;

@Activate(group = {"provider"})
public class DubboExporterListener implements ExporterListener {
	
	private static Logger logger = LoggerFactory.getLogger(DubboExporterListener.class);

	@Override
	public void exported(Exporter<?> paramExporter) throws RpcException {
		Invoker<?> invoker = paramExporter.getInvoker();
		URL url = invoker.getUrl();
		String protocol = url.getProtocol();
		if (!"dubbo".equals(protocol)) {//inJvm 的去掉
			return;
		}
		String serviceInterfaceName = url.getServiceInterface();
		String version = url.getParameter("version");
		logger.debug("export service name :{},version:{}",serviceInterfaceName,version);
	}

	@Override
	public void unexported(Exporter<?> paramExporter) {
		Invoker<?> invoker = paramExporter.getInvoker();
		URL url = invoker.getUrl();
		String protocol = url.getProtocol();
		if (!"dubbo".equals(protocol)) {//inJvm 的去掉
			return;
		}
		String serviceInterfaceName = url.getServiceInterface();
		String version = url.getParameter("version");
		logger.debug("unexport service name :{},version:{}",serviceInterfaceName,version);
	}

}
