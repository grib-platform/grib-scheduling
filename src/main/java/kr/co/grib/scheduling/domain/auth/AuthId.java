package kr.co.grib.scheduling.domain.auth;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
public class AuthId implements Serializable {

    @EqualsAndHashCode.Include
    private String clientId;

    @EqualsAndHashCode.Include
    private String userId;
}