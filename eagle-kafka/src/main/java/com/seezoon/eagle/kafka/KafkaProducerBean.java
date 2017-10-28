package com.seezoon.eagle.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducerBean {

	private Properties properties;
	protected Producer<String, String> producer;

	public KafkaProducerBean(Properties properties) {
		super();
		this.properties = properties;
		this.producer = new KafkaProducer<>(this.properties);
	}
	
	public void send(String topic,String content){
		producer.send(new ProducerRecord<String, String>(topic, content));
	}
	public void close(){
		if (null != producer) {
			producer.close();
		}
	}
}
