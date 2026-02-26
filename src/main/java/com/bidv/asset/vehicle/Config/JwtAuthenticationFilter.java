package com.bidv.asset.vehicle.Config;

import com.bidv.asset.vehicle.Utill.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            System.out.println("--- [Filter] No Auth Header or not Bearer: " + authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).trim();
        System.out.println("Auth header: " + authHeader);
        System.out.println("JWT: " + jwt);
        try {
            username = jwtUtils.extractUsername(jwt);
            System.out.println("Username extracted: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (!jwtUtils.isTokenExpired(jwt)) {
                    String role = jwtUtils.extractRole(jwt);
                    List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
                    if (role != null) {
                        String roleUpper = role.toUpperCase();
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + roleUpper));
                        System.out.println(">>> JWT Auth OK - user: " + username + " | authority: ROLE_" + roleUpper);
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println(">>> JWT Token is EXPIRED");
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println(">>> JWT Token expired: " + e.getMessage());
            e.printStackTrace();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.err.println(">>> JWT Signature invalid: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(">>> JWT Auth Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
