package com.example.ShopApp_BE.Config;

import com.example.ShopApp_BE.Filter.JwtTokenFilter;
import com.example.ShopApp_BE.Utils.ApiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final ApiProperties apiProperties;
    private final JwtTokenFilter jwtTokenFilter;
    private final Set<String> WHITE_LIST = new HashSet<>();

    @PostConstruct
    public void initWhiteList() {
        WHITE_LIST.add(String.format("%s/users/login", apiProperties.getPrefix()));
        WHITE_LIST.add(String.format("%s/users/register", apiProperties.getPrefix()));
        WHITE_LIST.add(String.format("%s/users/refresh-token", apiProperties.getPrefix()));
        WHITE_LIST.add("/uploads/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->
                    request.requestMatchers(WHITE_LIST.toArray(new String[0])).permitAll()
                            .requestMatchers(String.format("%s/users/update",apiProperties.getPrefix())).hasAnyRole("USER", "ADMIN")
                            .anyRequest().authenticated());

        return http.build();
    }
}
