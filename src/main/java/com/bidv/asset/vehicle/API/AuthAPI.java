package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.LoginRequest;
import com.bidv.asset.vehicle.DTO.LoginResponse;
import com.bidv.asset.vehicle.Service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthAPI {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        if (loginResponse.isSuccess()) {
            // Set Refresh Token in HttpOnly cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days in seconds
            response.addCookie(refreshTokenCookie);

            // Clean up refreshToken from response body before sending
            loginResponse.setRefreshToken(null);

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(401).body(loginResponse);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        LoginResponse loginResponse = authService.refreshToken(refreshToken);

        if (loginResponse.isSuccess()) {
            // Set NEW Refresh Token in HttpOnly cookie (Rotation)
            Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(refreshTokenCookie);

            // Clean up refreshToken from response body before sending
            loginResponse.setRefreshToken(null);

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(401).body(loginResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        authService.logout(refreshToken);

        // Clear cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
