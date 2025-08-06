package kr.co.grib.scheduling.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.service.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/kafka")
public class KafkaController {
    private final KafkaService kafkaService;

    @PostMapping("/produce/message")
    public void testKafka(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            kafkaService.produceMessage(param);
        }
    }

    @PostMapping("/create/topic")
    public void testCreateKafka(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            kafkaService.createTopic(param);
        }
    }

    @PostMapping("/delete/topic")
    public void testDeleteKafka(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            kafkaService.deleteTopic(param);
        }
    }

    @PostMapping("/read/topic")
    public void testReadTopic(HttpServletRequest request) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            kafkaService.readTopic();
        }
    }
}