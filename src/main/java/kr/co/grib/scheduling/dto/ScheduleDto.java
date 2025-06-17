package kr.co.grib.scheduling.dto;

import kr.co.grib.scheduling.domain.Schedule;
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
    private String apiBody;
    private String clientId;
    
    public ScheduleDto(Schedule schedule){
        this.scheduleId = schedule.getScheduleId();
        this.cronExpression = schedule.getCronExpression();
        this.apiBody = schedule.getApiBody();
        this.clientId = schedule.getClientId();
    }
}
