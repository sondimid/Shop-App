package com.example.ShopApp_BE.Config;

import com.example.ShopApp_BE.Filter.JwtTokenFilter;
import com.example.ShopApp_BE.Utils.ApiProperties;
import com.example.ShopApp_BE.Utils.WhiteList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final ApiProperties apiProperties;
    private final JwtTokenFilter jwtTokenFilter;
    private static List<String> WHITE_LIST = new ArrayList<>();

    @PostConstruct
    public void initWhiteList() {
        WHITE_LIST = List.of(
                String.format("%s/users/login/**", apiProperties.getPrefix()),
                String.format("%s/users/register", apiProperties.getPrefix()),
                String.format("%s/users/refresh-token", apiProperties.getPrefix()),
                String.format("%s/users/forgot-password", apiProperties.getPrefix()),
                String.format("%s/users/verify-account", apiProperties.getPrefix()),
                String.format("%s/users/reset-password", apiProperties.getPrefix()),
                String.format("%s/users/oauth2/**", apiProperties.getPrefix()),
                String.format("%s/products/lastest", apiProperties.getPrefix()),
                String.format("%s/products/best-deal", apiProperties.getPrefix()),
                String.format("%s/categories", apiProperties.getPrefix()),
                "/uploads/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->
                    request.requestMatchers(WHITE_LIST.toArray(new String[0])).permitAll()
                            .requestMatchers(HttpMethod.GET, String.format("%s/products/**", apiProperties.getPrefix()),
                                    String.format("%s/categories/**", apiProperties.getPrefix()))
                            .permitAll()
                            .anyRequest().authenticated());
        return http.build();
    }
}
