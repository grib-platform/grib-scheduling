package kr.co.grib.scheduling.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import kr.co.grib.scheduling.domain.Schedule;
import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.repository.ScheduleRepository;
import kr.co.grib.scheduling.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitScheduler{

    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;

    @PostConstruct
    @Transactional
    public void init() throws Exception {
        try{
            List<Schedule> scheduleList = scheduleRepository.findAll();
            for(Schedule schedule : scheduleList){
                ScheduleDto scheduleDto = new ScheduleDto(schedule);
                scheduleService.startScheduleInit(scheduleDto);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
