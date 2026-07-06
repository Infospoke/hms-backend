package com.hms.service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import org.springframework.util.backoff.FixedBackOff;

import com.hms.service.dto.NotificationEvent;

@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	@Value("${hms.kafka.notification-topic}")
	private String notificationTopic;

	//============TOPIC CONFIGURATION=====================

	@Bean
	public NewTopic notificationTopic() {

		return TopicBuilder.name(notificationTopic)
				.partitions(3)
				.replicas(1)
				.build();
	}

    //producer config
	@Bean
	public ProducerFactory<String, NotificationEvent> producerFactory() {

		Map<String, Object> config = new HashMap<>();

		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, "all");
		config.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
		config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		config.put(ProducerConfig.LINGER_MS_CONFIG, 5);
		config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
		config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
		
		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, NotificationEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
	@Bean
	public ConsumerFactory<String, NotificationEvent> consumerFactory() {

		JsonDeserializer<NotificationEvent> deserializer = new JsonDeserializer<>(NotificationEvent.class);

		deserializer.addTrustedPackages("*");

		deserializer.setUseTypeMapperForKey(false);

		Map<String, Object> config = new HashMap<>();

		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

		config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

		config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);

		config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);

		return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
	}

	@Bean
	public DefaultErrorHandler errorHandler() {

		FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3);

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(

				(consumerRecord, exception) -> {

					System.out.println("Failed Message : " + consumerRecord.value());

					System.out.println("Exception : " + exception.getMessage());
				},

				fixedBackOff);

		errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory());

		factory.setConcurrency(3);

		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

		factory.setCommonErrorHandler(errorHandler());

		return factory;
	}
}