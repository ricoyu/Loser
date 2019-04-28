package com.loserico.kafka.partition;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

public class MyTopicPartition implements Partitioner {

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		int intKey = (int)key;
		//return intKey % 3;
		return 3;
	}

	@Override
	public void close() {
	}

}
