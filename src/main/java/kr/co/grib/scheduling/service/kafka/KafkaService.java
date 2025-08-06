package kr.co.grib.scheduling.service.kafka;

import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;

public interface KafkaService {
    public ResponseDto<Void> createTopic(ScheduleDto param);
    public ResponseDto<Void> deleteTopic(ScheduleDto param);
    public ResponseDto<Void> produceMessage(ScheduleDto param);
    public ResponseDto<Void> readTopic();
}