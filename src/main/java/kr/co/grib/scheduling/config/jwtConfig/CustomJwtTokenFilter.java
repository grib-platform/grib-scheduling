package kr.co.grib.scheduling.config.jwtConfig;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.grib.scheduling.domain.auth.Auth;
import kr.co.grib.scheduling.repository.auth.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomJwtTokenFilter extends OncePerRequestFilter {
    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){      //header에 AUTHORIZATION이 없거나, Bearer로 시작하지 않으면 filter
            log.error("header가 없거나, 형식이 틀립니다. - {}", authorizationHeader);
            request.setAttribute("apiStatus", "TOKEN_HEADER_ERR");
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        try {
            token = authorizationHeader.split(" ")[1].trim();
        }catch (NullPointerException e){
            log.error("토큰을 분리하는데 실패했습니다. - {}", authorizationHeader);
            request.setAttribute("apiStatus", "TOKEN_VALUE_ERR");
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            log.error("토큰을 분리하는데 실패했습니다. - {}", authorizationHeader);
            request.setAttribute("apiStatus", "TOKEN_VALUE_ERR");
            filterChain.doFilter(request, response);
            return;
        }

        try{
            Auth auth = authRepository.findByAuthAccessToken(token);
            if (auth == null) {
                request.setAttribute("apiStatus", "TOKEN_VALUE_NULL");
                filterChain.doFilter(request, response);
                return;
            }
            LocalDateTime accessTokenExpiresAt = auth.getAuthAccessTokenExpiresAt();

            //토큰이 Valid한지 확인하기
            if(accessTokenExpiresAt.isBefore(LocalDateTime.now())){
                log.error("만료된 토큰입니다.");
                request.setAttribute("apiStatus", "TOKEN_EXPIRED");
                filterChain.doFilter(request, response);
                return;
            }
            //ROLE_ADMIN, ROLE_USER ETC...
            String[] stringRoles = auth.getAuthRoles().replaceAll("\\s+", "").split(",");
            request.setAttribute("apiStatus", "NORMAL");
            request.setAttribute("oauthRoles", stringRoles);
            filterChain.doFilter(request, response);
        }catch (NullPointerException e){
            e.printStackTrace();
            request.setAttribute("apiStatus", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("apiStatus", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
    }
}