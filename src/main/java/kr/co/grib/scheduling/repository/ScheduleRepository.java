package kr.co.grib.scheduling.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.grib.scheduling.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    Page<Schedule> findAllByScheduleIdContains(Pageable pageable, String scheduleId);
    Page<Schedule> findAllByClientIdContains(Pageable pageable, String clientId);
}
