package com.example.ShopApp_BE.Filter;

import com.example.ShopApp_BE.Config.WebSecurityConfig;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final ApiProperties apiProperties;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    private static List<String> WHITE_LIST = new ArrayList<>();

    @PostConstruct
    public void initWhiteList() {
        WHITE_LIST = List.of(
                String.format("%s/users/login/**", apiProperties.getPrefix()),
                String.format("%s/users/register", apiProperties.getPrefix()),
                String.format("%s/users/refresh-token", apiProperties.getPrefix()),
                String.format("%s/users/forgot-password", apiProperties.getPrefix()),
                String.format("%s/users/reset-password", apiProperties.getPrefix()),
                String.format("%s/users/oauth2/**", apiProperties.getPrefix()),
                "/uploads/**"
        );
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
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token, TokenType.ACCESS);
        if(phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserEntity userEntity = (UserEntity) userDetailsService.loadUserByUsername(phoneNumber);
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
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return WHITE_LIST.stream().anyMatch(endpoint -> antPathMatcher.match(endpoint, request.getRequestURI()));
    }
}
