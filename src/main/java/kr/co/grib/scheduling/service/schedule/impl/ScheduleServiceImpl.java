package kr.co.grib.scheduling.service.schedule.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import kr.co.grib.scheduling.dto.SearchDto;
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
    public ResponseDto<Void> createSchedule(ScheduleDto param) {
      try{
        Optional<Schedule> schedule = scheduleRepository.findById(param.getScheduleId());
        if(!schedule.isPresent()){
          Schedule newSchedule = Schedule.builder()
                                      .scheduleId(param.getScheduleId())
                                      .cronExpression(param.getCronExpression())
                                      .message(param.getMessage())
                                      .topic(param.getTopic())
                                      .createdAt(LocalDateTime.now())
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
    public ResponseDto<List<ScheduleDto>> readScheduleList(int pageNumber, int pageSize, String orderBy, String order, SearchDto param) {
      Sort sortSchedule = Sort.by(orderBy);
      if(order.equals("desc")){
          sortSchedule = sortSchedule.descending();
      }else{
          sortSchedule = sortSchedule.ascending();
      }

      String periodFromString = param.getPeriodFrom();
      String periodToString = param.getPeriodTo();
      if(periodFromString == null || periodFromString.isBlank()){
          periodFromString = "1900.01.01";
      }
      if(periodToString == null || periodToString.isBlank()){
          periodToString = "2999.01.01";
      }
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
      LocalDateTime periodFrom = LocalDate.parse(periodFromString, formatter).atStartOfDay();
      LocalDateTime periodTo = LocalDate.parse(periodToString, formatter).atStartOfDay();

      Pageable pageable = PageRequest.of(pageNumber, pageSize, sortSchedule);
      List<ScheduleDto> scheduleDtoList = null;
      Page<Schedule> scheduleList = null;
      if(param.getKeywordColumn().equals("scheduleId")){
        if(param.getPeriodColumns().equals("createdAt")){
          scheduleList = scheduleRepository.findAllByScheduleIdContainsAndCreatedAtBetween(param.getKeyword(), periodFrom, periodTo, pageable);
        }else{
          scheduleList = scheduleRepository.findAllByScheduleIdContains(pageable, param.getKeyword());
        }
      }else if(param.getKeywordColumn().equals("cronExpression")){
        if(param.getPeriodColumns().equals("createdAt")){
          scheduleList = scheduleRepository.findAllByCronExpressionContainsAndCreatedAtBetween(param.getKeyword(), periodFrom, periodTo, pageable);
        }else{
          scheduleList = scheduleRepository.findAllByCronExpressionContains(pageable, param.getKeyword());
        }
      }else if(param.getKeywordColumn().equals("message")){
        if(param.getPeriodColumns().equals("createdAt")){
          scheduleList = scheduleRepository.findAllByMessageContainsAndCreatedAtBetween(param.getKeyword(), periodFrom, periodTo, pageable);
        }else{
          scheduleList = scheduleRepository.findAllByMessageContains(pageable, param.getKeyword());
        }
      }else if(param.getKeywordColumn().equals("topic")){
        if(param.getPeriodColumns().equals("createdAt")){
          scheduleList = scheduleRepository.findAllByTopicContainsAndCreatedAtBetween(param.getKeyword(), periodFrom, periodTo, pageable);
        }else{
          scheduleList = scheduleRepository.findAllByTopicContains(pageable, param.getKeyword());
        }
      }else{
        if(param.getPeriodColumns().equals("createdAt")){
          scheduleList = scheduleRepository.findByCreatedAtBetween(pageable, periodFrom, periodTo);
        }else{
          scheduleList = scheduleRepository.findAll(pageable);
        }
      }
      scheduleDtoList = scheduleList.stream().map(schedule -> {
          return new ScheduleDto(schedule);
      }).toList();
      return ResponseDto.data(scheduleDtoList, PageResponse.of(pageNumber, pageSize, scheduleList.getTotalElements()));
    }

    @Override
    @Transactional
    public ResponseDto<Void> updateSchedule(ScheduleDto param) {
        try{
            Schedule schedule = scheduleRepository.findById(param.getScheduleId()).orElseThrow(IllegalArgumentException::new);
            //스케쥴 중지
            scheduledMap.get(param.getScheduleId()).shutdown();
            //스케쥴 제거
            scheduledMap.remove(param.getScheduleId());
            //스케쥴 수정
            if(param.getCronExpression() != null){
              schedule.setCronExpression(schedule.getCronExpression());
            }
            if(param.getTopic() != null){
              schedule.setTopic(schedule.getTopic());
              //토픽 수정시 실제 카프카 토픽 관리
              if(!schedule.getTopic().equals(param.getTopic())){//기존 토픽과 수정될 토픽이 다르다면
                if(scheduleRepository.findAllByTopic(schedule.getTopic()).size() == 1){//기존 토픽을 사용하는 하나 남은 마지막 스케쥴러라면
                  kafkaService.deleteTopic(new ScheduleDto(schedule));//기존에 사용중인 토픽 삭제
                }
                if(scheduleRepository.findAllByTopic(param.getTopic()).size() == 0){//수정하려는 토픽을 사용중인 스케쥴러가 없다면
                  kafkaService.createTopic(param);//수정된 토픽 새로 생성
                }
              }
            }
            if(param.getMessage() != null){
              schedule.setMessage(schedule.getMessage());
            }
            System.out.println(schedule.getScheduleId());
            System.out.println(schedule.getCronExpression());
            System.out.println(schedule.getTopic());
            System.out.println(schedule.getMessage());
            System.out.println(schedule.getCreatedAt());
            scheduleRepository.save(schedule);
            //수정된 스케쥴 적용
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.initialize();
            scheduler.schedule(getRunnable(param), new CronTrigger(param.getCronExpression()));
            scheduledMap.put(param.getScheduleId(), scheduler);
            return ResponseDto.data(null);
        }catch(NullPointerException e) {
            return ResponseDto.error("FAIL", e.getMessage(), null);
        }catch(Exception e){
            return ResponseDto.error("FAIL", e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseDto<Void> deleteSchedule(ScheduleDto param) {
      try{
        Optional<Schedule> schedule = scheduleRepository.findById(param.getScheduleId());
        scheduleRepository.delete(schedule.get());
        scheduledMap.get(param.getScheduleId()).shutdown();
        scheduledMap.remove(param.getScheduleId());
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
        // kafkaService.produceMessage(param);
      };
    }
}