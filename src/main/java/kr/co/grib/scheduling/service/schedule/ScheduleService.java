package kr.co.grib.scheduling.service.schedule;

import java.util.List;

import kr.co.grib.scheduling.dto.ScheduleDto;
import kr.co.grib.scheduling.dto.SearchDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;

public interface ScheduleService {
    public ResponseDto<Void> createSchedule(ScheduleDto param);
    public ResponseDto<List<ScheduleDto>> readScheduleList(int pageNumber, int pageSize, String orderBy, String order, SearchDto param);
    public ResponseDto<Void> updateSchedule(ScheduleDto param);
    public ResponseDto<Void> deleteSchedule(ScheduleDto param);
    public ResponseDto<Void> startScheduleInit(ScheduleDto param);
}