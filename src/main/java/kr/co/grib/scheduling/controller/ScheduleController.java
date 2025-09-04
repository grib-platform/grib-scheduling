package kr.co.grib.scheduling.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.SearchDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<ScheduleDto>>> readScheduleList(
        HttpServletRequest request,
        @RequestParam(name="pageNumber", defaultValue="0") int pageNumber,
        @RequestParam(name="pageSize", defaultValue="10") int pageSize,
        @RequestParam(name="orderBy", defaultValue="createdAt") String orderBy,
        @RequestParam(name="order", defaultValue="desc") String order,
        SearchDto param
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

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createSchedule(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = scheduleService.createSchedule(param);
            return ResponseEntity.ok(result);
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDto<Void>> updateSchedule(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = scheduleService.updateSchedule(param);
            return ResponseEntity.ok(result);
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteSchedule(HttpServletRequest request, @RequestBody ScheduleDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = scheduleService.deleteSchedule(param);
            return ResponseEntity.ok(result);
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }
}