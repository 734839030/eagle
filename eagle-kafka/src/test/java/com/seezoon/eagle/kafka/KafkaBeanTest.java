package com.seezoon.eagle.kafka;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaBeanTest {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(KafkaBeanTest.class);
	@Test
	public void send() throws IOException, InterruptedException{
		Properties props = new Properties();
		// 此处配置的是kafka的broker地址:端口列表 ，多个逗号分隔,需要修改config/server.properties 默认的hostname
		props.put("bootstrap.servers", "192.168.221.133:9092");
		//客户端将等待请求的响应的最大时间,如果在这个时间内没有收到响应，客户端将重发请求;超过重试次数将抛异常
		props.put("request.timeout.ms", "10000");
		// producer需要server接收到数据之后发出的确认接收的信号，此项配置就是指procuder需要多少个这样的确认信号。此配置实际上代表了数据备份的可用性。以下设置为常用选项：（1）acks=0：
		// 设置为0表示producer不需要等待任何确认收到的信息。副本将立即加到socket
		// buffer并认为已经发送。没有任何保障可以保证此种情况下server已经成功接收数据，同时重试配置不会发生作用（因为客户端不知道是否失败）回馈的offset会总是设置为-1；（2）acks=1：
		// 这意味着至少要等待leader已经成功将数据写入本地log，但是并没有等待所有follower是否成功写入。这种情况下，如果follower没有成功备份数据，而此时leader又挂掉，则消息会丢失。（3）acks=all：
		// 这意味着leader需要等待所有备份都成功写入日志，这种策略会保证只要有一个备份存活就不会丢失数据。这是最强的保证。，props.put("acks",
		// "all");
		props.put("acks", "all");
		//props.put("retries", 1);
		// 批处理字节数 默认就是这个
		// props.put("batch.size", 16384);
		// 批处理等待时间，不够数也发送
		// props.put("linger.ms", 1);
		// producer可以用来缓存数据的内存大小。如果数据产生速度大于向broker发送的速度，producer会阻塞或者抛出异常，以“block.on.buffer.full”来表明。这项设置将和producer能够使用的总内存相关，但并不是一个硬性的限制，因为不是producer使用的所有内存都是用于缓存。一些额外的内存会用于压缩（如果引入压缩机制），同样还有一些用于维护请求。
		// props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		KafkaProducerBean producer = new KafkaProducerBean(props);
		
		for (int i = 0;i<1000000000;i++) {
			//Thread.sleep(5000);
			producer.send("my-topic", "hello kafka" + i);
			logger.debug("send 第 {} 条",i);
		}
		System.in.read();
		producer.close();
	}
	@Test
	public void consumer() throws IOException{
		 Properties props = new Properties();
		 //多个逗号分隔
	     props.put("bootstrap.servers", "192.168.221.133:9092");
	     //消费者指定组后组内一个能消费到
	     props.put("group.id", "test");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("session.timeout.ms", "30000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     KafkaConsumerBean consumerBean = new KafkaConsumerBean(props, new String[]{"my-topic"});
	     consumerBean.consumer(new KafkaConsumerHandler() {
			@Override
			public void onConsumer(ConsumerRecords<String, String> records) {
				for (ConsumerRecord<String, String> consumerRecord : records) {
					//如果需要线程号传递 可以利用consumerRecord header，发送时候放就可以
					logger.debug("topic:{},value:{}",consumerRecord.topic(),consumerRecord.value());
				}
			}
		},100);
	    System.in.read();
	    consumerBean.close();
	}
}
