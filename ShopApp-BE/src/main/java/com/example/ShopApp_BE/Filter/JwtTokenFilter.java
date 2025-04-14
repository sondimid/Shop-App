package com.example.ShopApp_BE.Filter;

import com.example.ShopApp_BE.Config.WebSecurityConfig;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Repository.BackListTokenRepository;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Utils.ApiProperties;
import com.example.ShopApp_BE.Utils.JwtProperties;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.TokenType;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final ApiProperties apiProperties;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final BackListTokenRepository backListTokenRepository;
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
                String.format("%s/users/verify-account", prefix)
        ));

        WHITE_LIST.put("GET", List.of(
                String.format("%s/users/oauth2/**", prefix),
                String.format("%s/products/**", prefix),
                String.format("%s/categories/**", prefix),
                "/uploads/**"
        ));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        final String token = authHeader.replace("Bearer ", "");
        if(backListTokenRepository.existsByAccessToken(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtTokenUtils.extractEmail(token, TokenType.ACCESS);
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity userEntity = (UserEntity) userDetailsService.loadUserByUsername(email);
            if(jwtTokenUtils.isValid(token, userEntity, TokenType.ACCESS)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userEntity,
                                null,
                                userEntity.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
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
