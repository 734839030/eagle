package com.seezoon.eagle.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerBean {
	
	private Properties props;
	private KafkaConsumer<String, String> consumer;
	private boolean close = false;
	public KafkaConsumerBean(Properties props,String... topics) {
		super();
		this.props = props;
	    consumer = new KafkaConsumer<>(props);
	    consumer.subscribe(Arrays.asList(topics));
	    consumer.wakeup();
	}
	/**
	 * 
	 * @param KafkaConsumerHandler
	 * @param pollNum 一次拉去多少条
	 */
	public void consumer(KafkaConsumerHandler KafkaConsumerHandler,long  pollNum){
		//kafka 线程不安全 不推荐多线程消费处理逻辑不阻塞即可
	    while (!close) {
	    	 //一次取100 条
	         ConsumerRecords<String, String> records = consumer.poll(pollNum);
	         KafkaConsumerHandler.onConsumer(records);
		}
	}
	public void close(){
		close = true;
		if (null != consumer) {
			 consumer.close();
		}
	}
}
