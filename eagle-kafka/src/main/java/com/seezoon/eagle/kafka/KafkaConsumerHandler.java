package com.seezoon.eagle.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface KafkaConsumerHandler {

	public void onConsumer(ConsumerRecords<String, String> records);
}
