package kr.co.grib.scheduling.service.schedule.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.co.grib.scheduling.domain.Schedule;
import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.common.PageResponse;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.repository.ScheduleRepository;
import kr.co.grib.scheduling.service.kafka.KafkaService;
import kr.co.grib.scheduling.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final KafkaService kafkaService;
    private final ScheduleRepository scheduleRepository;
    private static final Map<String, ThreadPoolTaskScheduler> scheduledMap = new HashMap<>();

    @Override
    @Transactional
    public ResponseDto<Void> startSchedule(ScheduleDto param) {
      try{
        Optional<Schedule> schedule = scheduleRepository.findById(param.getScheduleId());
        if(!schedule.isPresent()){
          Schedule newSchedule = Schedule.builder()
                                      .scheduleId(param.getScheduleId())
                                      .cronExpression(param.getCronExpression())
                                      .message(param.getMessage())
                                      .topic(param.getTopic())
                                      .build();
          scheduleRepository.save(newSchedule);
          ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
          scheduler.initialize();
          scheduler.schedule(getRunnable(param), new CronTrigger(param.getCronExpression()));
          scheduledMap.put(param.getScheduleId(), scheduler);
          return ResponseDto.data(null);
        }else{
          return ResponseDto.error("FAIL", "schedule is already exist", null);
        }
      }catch (NullPointerException e){
        e.printStackTrace();
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }catch(Exception e){
        e.printStackTrace();
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }
    }
    
    @Override
    @Transactional
    public ResponseDto<Void> startScheduleInit(ScheduleDto param) {
      try{
        Schedule newSchedule = Schedule.builder()
                              .scheduleId(param.getScheduleId())
                              .cronExpression(param.getCronExpression())
                              .message(param.getMessage())
                              .topic(param.getTopic())
                              .build();
        scheduleRepository.save(newSchedule);
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(param), new CronTrigger(param.getCronExpression()));
        scheduledMap.put(param.getScheduleId(), scheduler);
        return ResponseDto.data(null);
      }catch (NullPointerException e){
        e.printStackTrace();
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }catch(Exception e){
        e.printStackTrace();
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }
    }

    private Runnable getRunnable(ScheduleDto param) {
      return () -> {
        System.out.println(param.getScheduleId());
        System.out.println(param.getScheduleId() +" : "+ param.getCronExpression());
        System.out.println(param.getScheduleId() +" : "+ param.getTopic());
        System.out.println(param.getScheduleId() +" : "+ param.getMessage());
        kafkaService.produceMessage(param);
      };
    }

    @Override
    @Transactional
    public ResponseDto<Void> stopSchedule(ScheduleDto param) {
      try{
        Optional<Schedule> schedule = scheduleRepository.findById(param.getScheduleId());
        scheduleRepository.delete(schedule.get());
        scheduledMap.get(param.getScheduleId()).shutdown();
        if(scheduleRepository.findAllByTopic(param.getTopic()).size() == 1){
          kafkaService.deleteTopic(param);
        }
        return ResponseDto.data(null);
      }catch (NullPointerException e){
        e.printStackTrace();
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }catch(Exception e){
        e.printStackTrace();
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }
    }

    @Override
    public ResponseDto<List<ScheduleDto>> readScheduleList(int pageNumber, int pageSize, String orderBy, String order, ScheduleDto param) {
      Sort sortSchedule = Sort.by(orderBy);
      if(order.equals("desc")){
          sortSchedule = sortSchedule.descending();
      }else{
          sortSchedule = sortSchedule.ascending();
      }
      Pageable pageable = PageRequest.of(pageNumber, pageSize, sortSchedule);
      List<ScheduleDto> scheduleDtoList = null;
      Page<Schedule> scheduleList = null;
      if(param.getScheduleId() != null){
          scheduleList = scheduleRepository.findAllByScheduleIdContains(pageable, param.getScheduleId());
          scheduleDtoList = scheduleList.stream().map(schedule -> {
              return new ScheduleDto(schedule);
          }).toList();
      }else if(param.getTopic() != null){
          scheduleList = scheduleRepository.findAllByTopicContains(pageable, param.getTopic());
          scheduleDtoList = scheduleList.stream().map(schedule -> {
              return new ScheduleDto(schedule);
          }).toList();
      }else{
          scheduleList = scheduleRepository.findAll(pageable);
          scheduleDtoList = scheduleList.stream().map(schedule -> {
              return new ScheduleDto(schedule);
          }).toList();
      }
      return ResponseDto.data(scheduleDtoList, PageResponse.of(pageNumber, pageSize, scheduleList.getTotalElements()));
    }
}