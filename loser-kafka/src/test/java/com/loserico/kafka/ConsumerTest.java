package com.loserico.kafka;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerTest {
	private static final String SERVER_URL = "192.168.102.104:9092,192.168.102.106:9092,192.168.102.107:9092";

	@Test
	public void testAutomaticOffsetCommitting() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", SERVER_URL);
		props.setProperty("group.id", "1");
		props.setProperty("enable.auto.commit", "true");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		@Cleanup KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("my-topic"));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records)
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		}
	}

	@Test
	public void testManuallyOffsetCommit() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", SERVER_URL);
		props.setProperty("group.id", "2");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("my-topic"));

		List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
		int batchSize = 100;

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				buffer.add(record);
			}

			if (buffer.size() >= batchSize) {
				// 执行业务逻辑, 批量加入DB
				System.out.println("Now add data to DB");

				// 手工来提交, 告诉broker已经收到的消息都处理成功了, 可以标记为"已提交"
				consumer.commitSync();
				buffer.clear();
			}
		}
	}

	/**
	 * 也是手工提交, 分区进行精细化控制, offset
	 */
	@Test
	public void testManuallyOffsetCommitPartition() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", SERVER_URL);
		props.setProperty("group.id", "2");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("topic-partition3"));

		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				Set<TopicPartition> partitions = records.partitions();

				// 开始处理每个分区的消息
				for (TopicPartition topicPartition : partitions) {
					List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
					for (ConsumerRecord<String, String> consumerRecord : partitionRecords) {
						log.info("topic={}, key={}, offset={}, partition={}, value={}", consumerRecord.topic(),
								consumerRecord.key(), consumerRecord.offset(), consumerRecord.partition(),
								consumerRecord.value());
					}

					// 告诉kafka新的offset
					long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
					// 注意要+1
					consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(lastOffset + 1)));
				}

			}
		} catch (Exception e) {
			consumer.close();
		}
	}
	
	/**
	 * 只处理指定的某个partition的消息
	 */
	@Test
	public void testSpecifiedPartition() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", SERVER_URL);
		props.setProperty("group.id", "2");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		TopicPartition p0 = new TopicPartition("topic-partition3", 0);
		TopicPartition p1 = new TopicPartition("topic-partition3", 1);
		TopicPartition p2 = new TopicPartition("topic-partition3", 2);
		consumer.assign(asList(p0, p1, p2));
		
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				Set<TopicPartition> partitions = records.partitions();
				
				// 开始处理每个分区的消息
				for (TopicPartition topicPartition : partitions) {
					List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
					for (ConsumerRecord<String, String> consumerRecord : partitionRecords) {
						log.info("topic={}, key={}, offset={}, partition={}, value={}", consumerRecord.topic(),
								consumerRecord.key(), consumerRecord.offset(), consumerRecord.partition(),
								consumerRecord.value());
					}
					
					// 告诉kafka新的offset
					long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
					// 注意要+1
					consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(lastOffset + 1)));
				}
				
			}
		} catch (Exception e) {
			consumer.close();
		}
	}
	
	
	/**
	 * Offset存储在业务数据里面，或是其它地方
	 * 控制消费的位置，也就是初始消费的Offset
	 */
	@Test
	public void testStoreOffsetOutsideLikeInDB() {
		Properties props = new Properties();
		props.put("bootstrap.servers", SERVER_URL);
		props.put("group.id", "g1");
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		
		TopicPartition p0 = new TopicPartition("topic-partition3", 0);
		TopicPartition p1 = new TopicPartition("topic-partition3", 1);
		TopicPartition p2 = new TopicPartition("topic-partition3", 2);
		
		consumer.assign(Arrays.asList(p0));
		
		try {
			consumer.seek(p0, 5);
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

				for (TopicPartition partition : records.partitions()) {
					List<ConsumerRecord<String, String>> prs = records.records(partition);
					// 处理每个分区的消息
					for (ConsumerRecord<String, String> pr : prs) {
						System.out.println("partition=" + pr.partition() + " , key==" + pr.key() + ", offset=="
								+ pr.offset() + ", value=" + pr.value());
					}

					// 返回去告诉kafka新的offset
					long lastOffset = prs.get(prs.size() - 1).offset();
					// 注意加1
					consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
				}

			}
		} finally {
			consumer.close();
		}
	}
}
