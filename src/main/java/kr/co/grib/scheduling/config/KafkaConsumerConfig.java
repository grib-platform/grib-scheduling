package kr.co.grib.scheduling.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import kr.co.grib.scheduling.dto.ScheduleDto;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, ScheduleDto> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://huring.grib-iot.com:9513");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "my-test-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(ScheduleDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ScheduleDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ScheduleDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
