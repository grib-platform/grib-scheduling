package kr.co.grib.scheduling.config.jwtConfig;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomJwtTokenUtil {

    @Value("${spring.client.secret}")
    private String clientSecret;

    // JWT에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    // JWT에서 사용자 역할 추출
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    // JWT에서 발행일 추출
    public Date getIssuedAtFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuedAt();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 토큰에서 만료 일자 추출
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }


    public boolean getAuthCheck(HttpServletRequest request, String role) {
        String[] oauthRolesArr = (String[]) request.getAttribute("oauthRoles");
        String oauthRoles = String.join(", ", oauthRolesArr);
        return oauthRoles.contains(role);
    }

    // 토큰에서 Claims 추출
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(clientSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}