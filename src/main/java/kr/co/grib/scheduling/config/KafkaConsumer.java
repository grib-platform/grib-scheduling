package kr.co.grib.scheduling.config;

import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    // @KafkaListener(topics = "test11", groupId = "my-test-group")
    // public void listen(ScheduleDto message) {
    //     System.out.println("수신된 메시지 ScheduleId: " + message.getScheduleId());
    //     System.out.println("수신된 메시지 Topic: " + message.getTopic());
    //     System.out.println("수신된 메시지 Message: " + message.getMessage());
    //     System.out.println("수신된 메시지 CronExpression: " + message.getCronExpression());
    // }
}
