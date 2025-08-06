package kr.co.grib.scheduling.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/read")
    public ResponseEntity<ResponseDto<List<ScheduleDto>>> readScheduleList(
        HttpServletRequest request,
        @RequestParam("pageNumber") int pageNumber,
        @RequestParam("pageSize") int pageSize,
        @RequestParam("orderBy") String orderBy,
        @RequestParam("order") String order,
        ScheduleDto param
    ) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<List<ScheduleDto>> result = scheduleService.readScheduleList(pageNumber, pageSize, orderBy, order, param);
            if(result.getCode().equals("FAIL")){
                return ResponseEntity.badRequest().body(result);
            }else{
                return ResponseEntity.ok(result);
            }
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @PostMapping("/start")
    public ResponseEntity<ResponseDto<Void>> startSchedule(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = scheduleService.startSchedule(param);
            return ResponseEntity.ok(result);
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<ResponseDto<Void>> stopSchedule(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = scheduleService.stopSchedule(param);
            return ResponseEntity.ok(result);
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }
}