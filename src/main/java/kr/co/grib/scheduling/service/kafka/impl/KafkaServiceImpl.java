package kr.co.grib.scheduling.service.kafka.impl;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.service.kafka.KafkaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Value("${spring.kafka.replication-factor}")
    private int replicationFactor;
  
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AdminClient adminClient;
    
    public KafkaServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, AdminClient adminClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = adminClient;
    }

    @Override
    @Transactional
    public ResponseDto<Void> createTopic(ScheduleDto param) {
      try {
        NewTopic newTopic = new NewTopic(param.getTopic(), 1, (short) replicationFactor);
        CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
        result.all().get();  // Blocking to ensure it is created
        log.info("Topic '" + param.getTopic() + "' created successfully.");
      }catch (NullPointerException e){
        e.printStackTrace();
      }catch (Exception e){
        e.printStackTrace();
      }
      return null;
    }

    @Override
    @Transactional
    public ResponseDto<Void> deleteTopic(ScheduleDto param) {
      try {
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singleton(param.getTopic()));
        KafkaFuture<Void> future = deleteTopicsResult.all();
        future.get(); // Blocking to ensure it is delete
        log.info("Topic '" + param.getTopic() + "' deleted successfully.");
      }catch (NullPointerException e){
        e.printStackTrace();
      }catch (Exception e){
        e.printStackTrace();
      }
      return null;
    }

    @Override
    @Transactional
    public ResponseDto<Void> produceMessage(ScheduleDto param) {
      kafkaTemplate.send(param.getTopic(), param);
      return null;
    }

    @Override
    @Transactional
    public ResponseDto<Void> readTopic() {
      try {
        Map<String, TopicListing> topics = adminClient.listTopics().namesToListings().get();
        System.out.println(topics);
        for (String topicName : topics.keySet()) {
          log.info("topicName = {},     topicListing = {}", topicName, topics.get(topicName));

          Map<String, TopicDescription> description = adminClient.describeTopics(Collections.singleton(topicName)).allTopicNames().get();
          log.info("topicName = {},     description = {}", topicName, description.get(topicName));
        }
      }catch (Exception e){
        e.printStackTrace();
      }
      return null;
    }
}