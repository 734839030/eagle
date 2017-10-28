package com.seezoon.eagle.dubbo.filter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.seezoon.eagle.core.constants.Constants;
import com.seezoon.eagle.core.utils.NDCUtils;

/**
 * dubbo追踪filter
 * @author hdf
 * 添加自动激活  就不需要在配置文件中配置该filter,group 不指定测试无效
 */
@Activate(group = {"provider","consumer"})
public class TraceFilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public Result invoke(Invoker<?> paramInvoker, Invocation paramInvocation) throws RpcException {
		Map<String, String> attachments = paramInvocation.getAttachments();
		String threadId = paramInvocation.getAttachment(Constants.THREAD_ID, NDCUtils.get());
		if (null == attachments) {
			attachments =  new HashMap<String,String>();
		} 
		attachments.put(Constants.THREAD_ID, threadId);
		NDCUtils.push(threadId);
		return paramInvoker.invoke(paramInvocation);
	}
}
