package com.example.demo.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
	private static final Logger LOGGER=LoggerFactory.getLogger(KafkaConsumer.class);
	
	@Value("${spring.kafka.topic.name}")
	private String topicname;
	
	@KafkaListener(topics="${spring.kafka.topic.name}",groupId="mygroup")
 public void consume(String message) {
	 LOGGER.info(String.format("Message recieved sucessfullf %s ",message));
 }
}
