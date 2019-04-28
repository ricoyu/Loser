package com.loserico.kafka;

import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProducerTest {
	
	private static final String SERVER_URL = "192.168.102.104:9092,192.168.102.106:9092,192.168.102.107:9092";

	/**
	 * 异步发送消息, 不阻塞
	 */
	@Test
	public void testProduceSimpleString() {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", SERVER_URL);
		/*
		 * The acks config controls the criteria under which requests are considered complete. The "all"
		 * setting we have specified will result in blocking on the full commit of the record, the
		 * slowest but most durable setting. 
		 * all表示需要所有的副本都应答写成功了才行
		 */
		properties.put("acks", "all");
		properties.put("retries", 0);
		properties.put("batch.size", 16484);
		properties.put("linger.ms", 5);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 100; i++) {
			producer.send(new ProducerRecord<String, String>("topic-partition3", Integer.toString(i), Integer.toString(i)));
//			producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i)));
		}
		producer.close();
	}
	
	@Test
	public void testProduceSimpleStringBlock() throws InterruptedException, ExecutionException {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", SERVER_URL);
		/*
		 * The acks config controls the criteria under which requests are considered complete. The "all"
		 * setting we have specified will result in blocking on the full commit of the record, the
		 * slowest but most durable setting. 
		 * all表示需要所有的副本都应答写成功了才行
		 */
		properties.put("acks", "all");
		properties.put("retries", 0);
		properties.put("batch.size", 16484);
		properties.put("linger.ms", 5);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		Producer<String, String> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 100; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i));
			RecordMetadata metadata = producer.send(record).get();
			System.out.println("RecordMetadata=" + metadata);
		}
		producer.close();
	}
	
	@Test
	public void testProduceSimpleStringCallback() throws InterruptedException, ExecutionException {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", SERVER_URL);
		/*
		 * The acks config controls the criteria under which requests are considered complete. The "all"
		 * setting we have specified will result in blocking on the full commit of the record, the
		 * slowest but most durable setting. 
		 * all表示需要所有的副本都应答写成功了才行
		 */
		properties.put("acks", "all");
		properties.put("retries", 0);
		properties.put("batch.size", 16484);
		properties.put("linger.ms", 5);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		/*
		 * callback回调能够保证顺序
		 * 先发送出去的消息的callback一定先于后发消息的callback执行
		 * @on
		 */
		Producer<String, String> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 100; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i));
			producer.send(record, (metadata, e) -> {
				log.info("offset={}", metadata.offset());
				log.info("partition={}", metadata.partition());
				log.info("metadata={}", metadata.topic());
			});
		}
		producer.close();
	}
	
	@Test
	public void testProduceSimpleStringCallbackOrdered() throws InterruptedException, ExecutionException {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", SERVER_URL);
		/*
		 * The acks config controls the criteria under which requests are considered complete. The "all"
		 * setting we have specified will result in blocking on the full commit of the record, the
		 * slowest but most durable setting. 
		 * all表示需要所有的副本都应答写成功了才行
		 */
		properties.put("acks", "all");
		properties.put("retries", 0);
		properties.put("batch.size", 16484);
		properties.put("linger.ms", 1);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		/*
		 * callback回调能够保证顺序
		 * 先发送出去的消息的callback一定先于后发消息的callback执行
		 * 
		 * 但是............ 有个前提
		 * 这个Topic只能有一个partition, 发送到同一个partition的消息才能保证顺序
		 * 如果Broker配置的num.partitions > 1
		 * 那么下面的111, 222就不一定发往同一个partition, 所以是不能保证顺序的
		 * Broker的num.partitions最好保留默认值1, 因为num.partitions只能增, 不能减
		 * @on
		 */
		Producer<String, String> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 100; i++) {
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("my-topic", "111", Integer.toString(i));
			producer.send(record, (metadata, e) -> {
				log.info("111-----offset={}, partition={}, metadata={}", metadata.offset(), metadata.partition(), metadata.topic());
			});
			ProducerRecord<String, String> record2 = new ProducerRecord<String, String>("my-topic", "222", Integer.toString(i));
			producer.send(record2, (metadata, e) -> {
				log.info("222--------offset={}, partition={}, metadata={}", metadata.offset(), metadata.partition(), metadata.topic());
			});
		}
		producer.close();
	}
	
	/**
	 * 实现自定义分区算法
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testCustomPartitioner() throws InterruptedException, ExecutionException {
		Properties adminProperties = new Properties();
		adminProperties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_URL);
		AdminClient adminClient = AdminClient.create(adminProperties);
		
		//1 先删掉my-topic3
		DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(asList("my-topic2"));
		try {
			deleteTopicsResult.all().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//2 重新创建3个分区的my-topic3
		NewTopic newTopic = new NewTopic("my-topic2", 3, (short)3);
		CreateTopicsResult createTopicsResult = adminClient.createTopics(asList(newTopic));
		createTopicsResult.all().get();
		
		//3 自定义分区算法
		Properties properties = new Properties();
		properties.put("bootstrap.servers", SERVER_URL);
		properties.put("acks", "all");
		properties.put("max.in.flight.requests.per.connection", 1);
		properties.put("batch.size", 16484);
		properties.put("linger.ms", 1);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
		properties.put("partitioner.class", "com.loserico.kafka.partition.MyTopicPartition");
		
		Producer<Integer, Integer> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 10; i++) {
			ProducerRecord<Integer, Integer> producerRecord = new ProducerRecord<Integer, Integer>("my-topic3", i, i);
			RecordMetadata recordMetadata = producer.send(producerRecord).get();
			log.info("i=={}, partition=={}", i, recordMetadata.partition());
		}
	}

	@Test
	public void testProduceTransactional() {
		Properties props = new Properties();
		props.put("bootstrap.servers", SERVER_URL);
		props.put("transactional.id", "my-transactional-id");
		Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());

		producer.initTransactions();

		try {
			producer.beginTransaction();
			for (int i = 0; i < 100; i++)
				producer.send(new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i)));
			producer.commitTransaction();
		} catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
			// We can't recover from these exceptions, so our only option is to close the producer and exit.
			producer.close();
		} catch (KafkaException e) {
			// For all other exceptions, just abort the transaction and try again.
			producer.abortTransaction();
		}
		producer.close();
	}
	

	@Test
	public void testProduceDate() {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", SERVER_URL);
		/*
		 * The acks config controls the criteria under which requests are considered complete. The "all"
		 * setting we have specified will result in blocking on the full commit of the record, the
		 * slowest but most durable setting. 
		 * all表示需要所有的副本都应答写成功了才行
		 */
		properties.put("acks", "all");
		properties.put("retries", 0);
		properties.put("batch.size", 16484);
		properties.put("linger.ms", 5);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, LocalDateTime> producer = new KafkaProducer<>(properties);
		for (int i = 0; i < 100; i++) {
			producer.send(new ProducerRecord<String, LocalDateTime>("my-topic3", Integer.toString(i), LocalDateTime.now()));
		}
		producer.close();
	}
	
}
