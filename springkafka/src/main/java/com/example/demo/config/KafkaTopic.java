package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
	@Value("${spring.kafka.topic.name}")
	private String topicname;
	
	@Value("${spring.kafka.topic1.name}")
	private String topicname1;
	
@Bean	
public NewTopic newtopic() {
	return TopicBuilder.name(topicname)
			.build();
}
@Bean	
public NewTopic newtopic1() {
	return TopicBuilder.name(topicname1)
			.build();
}
}
