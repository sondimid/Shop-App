package com.example.ShopApp_BE.Filter;

import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Utils.ApiProperties;
import com.example.ShopApp_BE.Utils.JwtProperties;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final ApiProperties apiProperties;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private static final String ACCESS_TOKEN = "access_token";

    private final Set<String> WHITE_LIST = new HashSet<>();

    @PostConstruct
    public void initWhiteList() {
        WHITE_LIST.add(String.format("%s/users/login", apiProperties.getPrefix()));
        WHITE_LIST.add(String.format("%s/users/register", apiProperties.getPrefix()));
        WHITE_LIST.add(String.format("%s/users/refresh-token", apiProperties.getPrefix()));
        WHITE_LIST.add("/uploads/**");
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
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token, ACCESS_TOKEN);
        if(phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null
            && tokenRepository.existsByAccessToken(token)) {
            UserEntity userEntity = (UserEntity) userDetailsService.loadUserByUsername(phoneNumber);
            if(jwtTokenUtils.isValid(token, userEntity, ACCESS_TOKEN)) {
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
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return WHITE_LIST.stream().anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }
}
