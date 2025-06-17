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
@EqualsAndHashCode(of = { "clientId" })
@Entity
@Table(name = "tb_client")
@EntityListeners(AuditingEntityListener.class)
public class Client implements Persistable<String> {
    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "callback_url")
    private String callbackUrl;

    @Override
    public boolean isNew() {
        return clientId == null;
    }

    @Override
    public String getId() {
        return clientId;
    }
}
