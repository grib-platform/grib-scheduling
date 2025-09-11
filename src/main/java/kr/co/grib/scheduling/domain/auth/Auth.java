package kr.co.grib.scheduling.domain.auth;

import java.time.LocalDateTime;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@IdClass(AuthId.class)
@Table(name = "tb_auth")
public class Auth implements Persistable<AuthId> {

    @Id
    @Column(name = "client_id")
    private String clientId;
    
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "auth_code")
    private String authCode;
    
    @Column(name = "auth_code_expires_at")
    private LocalDateTime authCodeExpiresAt;
    
    @Column(name = "auth_access_token")
    private String authAccessToken;
    
    @Column(name = "auth_access_token_expires_at")
    private LocalDateTime authAccessTokenExpiresAt;
    
    @Column(name = "auth_roles")
    private String authRoles;
    
    @Column(name = "auth_refresh_token")
    private String authRefreshToken;
    
    @Column(name = "auth_refresh_token_expires_at")
    private LocalDateTime authRefreshTokenExpiresAt;
    
    @Column(name = "auth_created_at")
    private LocalDateTime authCreatedAt;
    
    @Column(name = "auth_updated_at")
    private LocalDateTime authUpdatedAt;

    @Override
    public boolean isNew() {
        return clientId == null;
    }

    @Override
    public AuthId getId() {
        if (clientId == null || userId == null) {
            return null;
        } else {
            return new AuthId(clientId, userId);
        }
    }
    
}