package kr.co.grib.scheduling.config.jwtConfig;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomJwtTokenFilter extends OncePerRequestFilter {

    private final CustomJwtTokenUtil customJwtTokenUtil;

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
        } catch (Exception e) {
            log.error("토큰을 분리하는데 실패했습니다. - {}", authorizationHeader);
            request.setAttribute("apiStatus", "TOKEN_VALUE_ERR");
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String userId = customJwtTokenUtil.getUsernameFromToken(token);
            if(!customJwtTokenUtil.validateToken(token, userId)){
                log.error("만료된 토큰입니다.");
                request.setAttribute("apiStatus", "TOKEN_EXPIRED");
                filterChain.doFilter(request, response);
                return;
            }
            //ROLE_ADMIN, ROLE_USER ETC...
            String[] stringRoles = customJwtTokenUtil.getRoleFromToken(token).replaceAll("\\s+", "").split(",");
            request.setAttribute("apiStatus", "NORMAL");
            request.setAttribute("oauthRoles", stringRoles);
            filterChain.doFilter(request, response);
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("apiStatus", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
    }
}