package kr.co.grib.scheduling.service.schedule.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import kr.co.grib.scheduling.domain.Client;
import kr.co.grib.scheduling.domain.Schedule;
import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.common.PageResponse;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.repository.ClientRepository;
import kr.co.grib.scheduling.repository.ScheduleRepository;
import kr.co.grib.scheduling.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClientRepository clientRepository;
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
                                      .apiBody(param.getApiBody())
                                      .clientId(param.getClientId())
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
      }catch(Exception e){
        e.printStackTrace();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }
    }
    
    @Override
    public ResponseDto<Void> startScheduleInit(ScheduleDto param) {
      try{
        Schedule newSchedule = Schedule.builder()
                              .scheduleId(param.getScheduleId())
                              .cronExpression(param.getCronExpression())
                              .apiBody(param.getApiBody())
                              .clientId(param.getClientId())
                              .build();
        scheduleRepository.save(newSchedule);
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(param), new CronTrigger(param.getCronExpression()));
        scheduledMap.put(param.getScheduleId(), scheduler);
        return ResponseDto.data(null);
      }catch(Exception e){
        e.printStackTrace();
        return ResponseDto.error("FAIL", e.getMessage(), null);
      }
    }

    private Runnable getRunnable(ScheduleDto param) {
      return () -> {
        try{
          Optional<Client> client = clientRepository.findById(param.getClientId());
          if(client.isPresent()){
            WebClient webClient = WebClient.create(client.get().getCallbackUrl());
            JSONObject messageJson = new JSONObject(param.getApiBody());
            Map<String, Object> bodyMap = messageJson.toMap();
            String response = webClient.post()
                                      .header("Authorization", "Bearer " + client.get().getAccessToken())
                                      .bodyValue(bodyMap)
                                      .retrieve()
                                      .bodyToMono(String.class)
                                      .block();
            log.info(response);
          }else{
            log.info(param.getClientId() + " : client is null.");
          }
        }catch(Exception e){
          e.printStackTrace();
        }
      };
    }

    @Override
    @Transactional
    public ResponseDto<Void> stopSchedule(ScheduleDto param) {
      try{
        Optional<Schedule> schedule = scheduleRepository.findById(param.getScheduleId());
        scheduleRepository.delete(schedule.get());
        scheduledMap.get(param.getScheduleId()).shutdown();
        return ResponseDto.data(null);
      }catch(Exception e){
        e.printStackTrace();
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
      }else if(param.getClientId() != null){
          scheduleList = scheduleRepository.findAllByClientIdContains(pageable, param.getClientId());
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