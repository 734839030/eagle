package com.seezoon.eagle.amq.sender;

import java.io.Serializable;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.Assert;

import com.seezoon.eagle.core.constants.Constants;
import com.seezoon.eagle.core.utils.NDCUtils;

public abstract class AbstractSender implements Sender{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public abstract JmsTemplate getJmsTemplate();
	@Override
	public void sendTextMessage(String destinationName, final String textMessage) {
		Assert.isTrue(!StringUtils.isEmpty(textMessage), "textMessage not allow empty or null");
		getJmsTemplate().send(destinationName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				logger.debug("send text msg: {}",textMessage);
				return AbstractSender.this.messageWrap(session.createTextMessage(textMessage));
			}
		});
	}

	@Override
	public <V> void sendMapMessage(String destinationName, final Map<String, V> map) {
		Assert.notNull(map,"map is null");
		getJmsTemplate().send(destinationName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				for (Map.Entry<String, V>  entry :map.entrySet()) {
					mapMessage.setObject(entry.getKey(), entry.getValue());
				}
				logger.debug("send map msg: {}",map);
				return AbstractSender.this.messageWrap(mapMessage);
			}
		});
	}

	@Override
	public  void sendObjectMessage(String destinationName, final Serializable objMessage) {
		Assert.notNull(objMessage,"object is null");
		getJmsTemplate().send(destinationName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				logger.debug("send object msg: {}",objMessage);
				return AbstractSender.this.messageWrap(session.createObjectMessage(objMessage));
			}
		});
	}

	@Override
	public void sendByteMessage(String destinationName,final byte[] bytes) {
		Assert.notNull(bytes,"bytesMessage is null");
		getJmsTemplate().send(destinationName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				logger.debug("send bytes msg: {}",bytes);
				BytesMessage bytesMessage = session.createBytesMessage();
				bytesMessage.writeBytes(bytes);
				return AbstractSender.this.messageWrap(bytesMessage);
			}
		});
	}
	private Message messageWrap(Message msg) {
		try {
			msg.setStringProperty(Constants.THREAD_ID,NDCUtils.peek());
		} catch (JMSException e) {
			logger.error("messageWrap error",e);
			e.printStackTrace();
		}
		return msg;
	}
}