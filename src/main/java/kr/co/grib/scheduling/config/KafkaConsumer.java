package kr.co.grib.scheduling.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import kr.co.grib.scheduling.dto.ScheduleDto;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "test", groupId = "my-test-group")
    public void listen(ScheduleDto message) {
        System.out.println("수신된 메시지 ScheduleId: " + message.getScheduleId());
        System.out.println("수신된 메시지 ClientId: " + message.getClientId());
        System.out.println("수신된 메시지 ApiBody: " + message.getApiBody());
        System.out.println("수신된 메시지 CronExpression: " + message.getCronExpression());
    }
}
