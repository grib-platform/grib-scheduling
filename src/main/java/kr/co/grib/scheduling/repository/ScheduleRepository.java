package kr.co.grib.scheduling.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.grib.scheduling.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findAllByTopic(String topic);
    Page<Schedule> findAllByScheduleIdContains(Pageable pageable, String scheduleId);
    Page<Schedule> findAllByTopicContains(Pageable pageable, String clientId);
}
