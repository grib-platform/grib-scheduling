package kr.co.grib.scheduling.service.kafka.impl;

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
  
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public KafkaServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public ResponseDto<Void> produceMessage(ScheduleDto param) {
      kafkaTemplate.send("test", param);
      return null;
    }
}