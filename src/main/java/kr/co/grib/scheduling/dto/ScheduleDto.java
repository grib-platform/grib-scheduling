package kr.co.grib.scheduling.dto;

import java.time.LocalDateTime;

import kr.co.grib.scheduling.domain.scheduling.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleDto {
    private String scheduleId;
    private String cronExpression;
    private String message;
    private String topic;
    private LocalDateTime createdAt;
    
    public ScheduleDto(Schedule schedule){
        this.scheduleId = schedule.getScheduleId();
        this.cronExpression = schedule.getCronExpression();
        this.message = schedule.getMessage();
        this.topic = schedule.getTopic();
        this.createdAt = schedule.getCreatedAt();
    }
}
