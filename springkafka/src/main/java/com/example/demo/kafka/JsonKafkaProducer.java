package com.example.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.example.demo.payload.User;

@Service
public class JsonKafkaProducer {
private static final Logger LOGGER=LoggerFactory.getLogger(JsonKafkaProducer.class);

@Value("${spring.kafka.topic1.name}")
private String topicname1;

@Autowired
private KafkaTemplate<String,User> kafkatemplate;
public void sendmessage(User user) {
	LOGGER.info(String.format("User data added %s", user.toString()));
	Message<User> message=MessageBuilder
			             .withPayload(user)
			             .setHeader(KafkaHeaders.TOPIC, topicname1)
			             .build();
	kafkatemplate.send(message);
}

}
