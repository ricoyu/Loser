package com.loserico.kafka;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loserico.commons.jackson.JacksonUtils;

public class AdminClientTest {

	public static final String SERVER_URL = "192.168.102.104:9092,192.168.102.106:9092,192.168.102.107:9092";
	public static final String TOPIC_NAME = "java-admin-topic";

	public static AdminClient adminClient;

	@BeforeClass
	public static void adminClient() {
		Properties properties = new Properties();
		properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_URL);
		adminClient = AdminClient.create(properties);
	}

	@Test
	public void createTopic() {
		NewTopic topic = new NewTopic(TOPIC_NAME, 2, (short) 3);
		CreateTopicsResult result = adminClient.createTopics(asList(topic));
		try {
			result.all().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void listTopics() {
		ListTopicsResult result = adminClient.listTopics();
		try {
			result.names().get().stream().forEach(System.out::println);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void listTopicWithInternal() {
		ListTopicsOptions options = new ListTopicsOptions();
		options.listInternal(true);
		ListTopicsResult result = adminClient.listTopics(options);
		try {
			result.names().get().stream().forEach(System.out::println);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void descTopicsOfConsumerOffset() {
		DescribeTopicsResult result = adminClient.describeTopics(asList("__consumer_offsets"));
		Map<String, TopicDescription> desc;
		try {
			desc = result.all().get();
			System.out.println(desc);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void descTopics() {
		DescribeTopicsResult result = adminClient.describeTopics(asList(TOPIC_NAME));
		Map<String, TopicDescription> desc;
		try {
			desc = result.all().get();
			System.out.println(JacksonUtils.toJson(desc));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void descConfig() {
		DescribeConfigsResult result = adminClient.describeConfigs(asList(new ConfigResource(Type.TOPIC, TOPIC_NAME)));
		try {
			Map<ConfigResource, Config> configMap = result.all().get();
			for (ConfigResource resource : configMap.keySet()) {
				System.out.println("Resource: " + resource.name() + "=" +
						resource.type());
				Config config = configMap.get(resource);
				Collection<ConfigEntry> entries = config.entries();
				for (ConfigEntry configEntry : entries) {
					System.out.println("Config: " +
							configEntry.name() + "=" + configEntry.value());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void alterConfig() {
		ConfigResource configResource = new ConfigResource(Type.TOPIC, TOPIC_NAME);
		Config config = new Config(asList(new ConfigEntry("preallocate", "true")));
		AlterConfigsResult result = adminClient.alterConfigs(Collections.singletonMap(configResource, config));
		try {
			result.all().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加Partition后可以通过kafka-topics.sh查看
	 * [root@fighting ~]# kafka-topics.sh --describe --zookeeper 192.168.102.104:2181/kafka --topic java-admin-topic                
	 * Topic:java-admin-topic  PartitionCount:4        ReplicationFactor:3     Configs:preallocate=true
	 * Topic: java-admin-topic Partition: 0    Leader: 104     Replicas: 106,107,104   Isr: 104,107,106
	 * Topic: java-admin-topic Partition: 1    Leader: 104     Replicas: 104,106,107   Isr: 104,107,106
	 * Topic: java-admin-topic Partition: 2    Leader: 104     Replicas: 104,107,106   Isr: 104,107,106
	 * Topic: java-admin-topic Partition: 3    Leader: 106     Replicas: 106,107,104   Isr: 106,107,104
	 * 
	 * 也可以通过上面的descTopic查看
	 * @on
	 */
	@Test
	public void testAddPartication() {
		CreatePartitionsResult result = adminClient.createPartitions(Collections.singletonMap(TOPIC_NAME,
				NewPartitions.increaseTo(4)));
		try {
			result.all().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteTopic() {
		DeleteTopicsResult result = adminClient.deleteTopics(asList("my-topic"));
//		DeleteTopicsResult result = adminClient.deleteTopics(asList(TOPIC_NAME));
		try {
			result.all().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDescCluster() {
		DescribeClusterResult result = adminClient.describeCluster();
		try {
			System.out.println("clusterId="+result.clusterId().get());
			System.out.println("controller="+result.controller().get());
			System.out.println("nodes="+result.nodes().get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
