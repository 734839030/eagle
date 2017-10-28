package com.seezoon.eagle.rocketmq.listener;

import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seezoon.eagle.core.constants.Constants;
import com.seezoon.eagle.core.utils.NDCUtils;

public abstract class AbstractListener {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 是否回执成功
	 * @param messageExt
	 * @return
	 */
	public abstract boolean handleMessage(MessageExt messageExt);
	
	protected void preHandle(MessageExt messageExt) {
		String threadId = messageExt.getUserProperty(Constants.THREAD_ID);
		NDCUtils.push(threadId);
	}
}
