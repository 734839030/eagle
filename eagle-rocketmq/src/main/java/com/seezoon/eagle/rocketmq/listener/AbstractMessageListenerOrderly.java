package com.seezoon.eagle.rocketmq.listener;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 严格有序消息，无负载均衡
 * @author hdf
 *
 */
public abstract class AbstractMessageListenerOrderly extends AbstractListener implements MessageListenerOrderly {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
		context.setAutoCommit(false);
		ConsumeOrderlyStatus status = ConsumeOrderlyStatus.SUCCESS;
		for (MessageExt messageExt : msgs) {
			super.preHandle(messageExt);
			boolean handleMessageReslut = this.handleMessage(messageExt);
			if (!handleMessageReslut) {
				status = ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
			}
		}
		return status;
	}

}
