package com.example.ShopApp_BE.Utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {
    private final JwtTokenUtils jwtTokenUtils;

    public Cookie getCookie(String refreshToken) {
        Long exp = jwtTokenUtils.extractExpiration(refreshToken, TokenType.REFRESH);
        int cookieMaxAge = (int) (exp - System.currentTimeMillis()) / 1000;
        if(cookieMaxAge < 0)
            cookieMaxAge = 0;

        Cookie refreshTokenCookie = new Cookie(MessageKeys.REFRESH_TOKEN_HEADER, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(cookieMaxAge);
        return refreshTokenCookie;
    }

    public Cookie getOrderCodeCookie(Long orderCode) {
        Cookie cookie = new Cookie(MessageKeys.ORDER_CODE, String.valueOf(orderCode));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        return cookie;
    }

    public void invalidateCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
