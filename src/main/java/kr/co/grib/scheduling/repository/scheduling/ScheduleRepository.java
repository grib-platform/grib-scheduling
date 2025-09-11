package kr.co.grib.scheduling.repository.scheduling;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.grib.scheduling.domain.scheduling.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findAllByTopic(String topic);
    Page<Schedule> findByCreatedAtBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);

    Page<Schedule> findAllByCronExpressionContainsAndCreatedAtBetween(String cronExpression, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Schedule> findAllByCronExpressionContains(Pageable pageable, String cronExpression);

    Page<Schedule> findAllByMessageContainsAndCreatedAtBetween(String message, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Schedule> findAllByMessageContains(Pageable pageable, String message);

    Page<Schedule> findAllByScheduleIdContainsAndCreatedAtBetween(String scheduleId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Schedule> findAllByScheduleIdContains(Pageable pageable, String scheduleId);

    Page<Schedule> findAllByTopicContainsAndCreatedAtBetween(String topic, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Schedule> findAllByTopicContains(Pageable pageable, String topic);
}
