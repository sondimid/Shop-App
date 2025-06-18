package com.example.ShopApp_BE.Filter;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Service.RedisService;
import com.example.ShopApp_BE.Utils.ApiProperties;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final ApiProperties apiProperties;
    private final UserDetailsService userDetailsService;
    private final RedisService<String, String, Object> redisService;
    private static Map<String, List<String>> WHITE_LIST = new HashMap<>();


    @PostConstruct
    public void initWhiteList() {
        String prefix = apiProperties.getPrefix();

        WHITE_LIST.put("POST", List.of(
                String.format("%s/users/login/**", prefix),
                String.format("%s/users/register", prefix),
                String.format("%s/users/refresh-token", prefix),
                String.format("%s/users/forgot-password", prefix),
                String.format("%s/users/reset-password", prefix),
                String.format("%s/users/verify-account", prefix),
                String.format("%s/admin/login", prefix)
        ));

        WHITE_LIST.put("GET", List.of(
                String.format("%s/users/oauth2/**", prefix),
                String.format("%s/products/**", prefix),
                String.format("%s/categories/**", prefix),
                String.format("%s/chat/**", prefix),
                "/chat/**",
                "/uploads/**",
                "/shopapp/uploads/**"
        ));
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain){
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        final String token = authHeader.replace("Bearer ", "");

        String email = jwtTokenUtils.extractEmail(token, TokenType.ACCESS);
        UserEntity userEntity = (UserEntity) userDetailsService.loadUserByUsername(email);

        if(userEntity.getIsActive() == Boolean.FALSE){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(MessageKeys.ACCOUNT_LOCK);
            return;
        }

        if(!jwtTokenUtils.isNotExpired(token, TokenType.ACCESS) || redisService.hasKey(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(MessageKeys.ACCESS_TOKEN_INVALID);
            return;
        }

        if(jwtTokenUtils.extractTokenVersion(token, TokenType.ACCESS) < userEntity.getTokenVersion()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(MessageKeys.ACCOUNT_LOGOUT);
            return;
        }
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userEntity,
                            authHeader.replace("Bearer ", ""),
                            userEntity.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();

        List<String> paths = WHITE_LIST.get(method);
        if (paths == null) return false;

        return paths.stream().anyMatch(pattern -> matcher.match(pattern, uri));
    }

}
