package kr.co.grib.scheduling.domain;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode(of = { "scheduleId" })
@Entity
@Table(name = "tb_schedule")
@EntityListeners(AuditingEntityListener.class)
public class Schedule implements Persistable<String> {
    @Id
    @Column(name = "schedule_id")
    private String scheduleId;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "api_body")
    private String apiBody;

    @Column(name = "client_id")
    private String clientId;

    @Override
    public boolean isNew() {
        return scheduleId == null;
    }

    @Override
    public String getId() {
        return scheduleId;
    }
}
