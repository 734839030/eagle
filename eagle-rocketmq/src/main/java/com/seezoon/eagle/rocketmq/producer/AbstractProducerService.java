package com.seezoon.eagle.rocketmq.producer;

import org.apache.rocketmq.common.message.MessageExt;

import com.seezoon.eagle.core.constants.Constants;
import com.seezoon.eagle.core.service.BaseService;
import com.seezoon.eagle.core.utils.NDCUtils;

public abstract class AbstractProducerService extends BaseService{

	/**
	 * 放入ndc线程号
	 * @param messageExt
	 */
	protected void rebuildMessageExt(MessageExt messageExt){
		messageExt.putUserProperty(Constants.THREAD_ID, NDCUtils.push());
	}
	
}
