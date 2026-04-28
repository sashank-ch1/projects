package com.example.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.payload.User;

@Service
public class JsonKafkaConsumer {
	
private static Logger LOGGER=LoggerFactory.getLogger(JsonKafkaConsumer.class);

@Value("${spring.kafka.topic1.name}")
private String topicname1;

@KafkaListener(topics="${spring.kafka.topic1.name}",groupId="mygroup")
public void Consume(User user) {
	LOGGER.info(String.format("Json Message recieved Sucessfully %s", user.toString()));
}
}
