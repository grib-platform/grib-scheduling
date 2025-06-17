package kr.co.grib.scheduling.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.grib.scheduling.config.jwtConfig.CustomJwtTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FilterConfiguration implements WebMvcConfigurer {

    private final CustomJwtTokenFilter customJwtTokenFilter;

    @Bean
    public FilterRegistrationBean<CustomJwtTokenFilter> customJwtTokenFilterRegistrationBean() {
        FilterRegistrationBean<CustomJwtTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(customJwtTokenFilter);
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
