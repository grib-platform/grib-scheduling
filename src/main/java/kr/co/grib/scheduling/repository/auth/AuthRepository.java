package kr.co.grib.scheduling.repository.auth;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.grib.scheduling.domain.auth.Auth;
import kr.co.grib.scheduling.domain.auth.AuthId;

@Repository
public interface AuthRepository extends JpaRepository<Auth, AuthId> {
    Auth findByClientIdAndUserId(String clientId, String userId);
    Auth findByAuthCode(String authCode);
    Auth findByAuthAccessToken(String authAccessToken);
    Auth findByAuthRefreshToken(String authRefreshToken);

    Page<Auth> findAllByClientIdContainsAndAuthCreatedAtBetween(String clientId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Auth> findAllByClientIdContainsAndAuthUpdatedAtBetween(String clientId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Auth> findAllByClientIdContains(Pageable pageable, String clientId);
    
    Page<Auth> findAllByUserIdContainsAndAuthCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Auth> findAllByUserIdContainsAndAuthUpdatedAtBetween(String userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Auth> findAllByUserIdContains(Pageable pageable, String userId);
    
    Page<Auth> findAllByAuthRolesContainsAndAuthCreatedAtBetween(String authRoles, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Auth> findAllByAuthRolesContainsAndAuthUpdatedAtBetween(String authRoles, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Auth> findAllByAuthRolesContains(Pageable pageable, String authRoles);
    
    Page<Auth> findByAuthCodeExpiresAtBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);
    Page<Auth> findByAuthAccessTokenExpiresAtBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);
    Page<Auth> findByAuthRefreshTokenExpiresAtBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);
    Page<Auth> findByAuthCreatedAtBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);
    Page<Auth> findByAuthUpdatedAtBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);
}