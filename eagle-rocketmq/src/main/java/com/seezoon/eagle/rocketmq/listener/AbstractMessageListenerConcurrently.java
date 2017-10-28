package com.seezoon.eagle.rocketmq.listener;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 立即接收
 * @author hdf
 *
 */
public abstract class AbstractMessageListenerConcurrently extends AbstractListener implements MessageListenerConcurrently {
	/**
	 * 批量消息 回执不适用
	 */
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		ConsumeConcurrentlyStatus status = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		for (MessageExt messageExt : msgs) {
			super.preHandle(messageExt);
			boolean handleMessageReslut = this.handleMessage(messageExt);
			if (!handleMessageReslut) {
				status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
		}
		return status;
	}
}
