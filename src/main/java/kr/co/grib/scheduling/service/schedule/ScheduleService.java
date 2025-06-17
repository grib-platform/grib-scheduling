package kr.co.grib.scheduling.service.schedule;

import java.util.List;

import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;

public interface ScheduleService {
    public ResponseDto<List<ScheduleDto>> readScheduleList(int pageNumber, int pageSize, String orderBy, String order, ScheduleDto param);
    public ResponseDto<Void> startSchedule(ScheduleDto param);
    public ResponseDto<Void> startScheduleInit(ScheduleDto param);
    public ResponseDto<Void> stopSchedule(ScheduleDto param);
}