package com.larkin.defcode.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.larkin.defcode.dto.response.ErrorResponse;
import com.larkin.defcode.entity.Role;
import com.larkin.defcode.entity.TokenType;
import com.larkin.defcode.entity.User;
import com.larkin.defcode.exception.InvalidJwtTokenException;
import com.larkin.defcode.security.UserDetailsImpl;
import com.larkin.defcode.security.service.JwtProvider;
import com.larkin.defcode.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Starting JwtFilter");
        if (request.getRequestURI().startsWith("/auth")) {
            log.debug("Request URI starts with /auth");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("Request does not contain Authentication header or Bearer prefix");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        try {
            jwtProvider.validateAccessToken(token);

            String username = jwtProvider.extractUsername(token, TokenType.ACCESS);
            String role = jwtProvider.extractRole(token, TokenType.ACCESS);

            User tempUser = User.builder().username(username).role(Role.valueOf(role)).build();
            UserDetails userDetails = new UserDetailsImpl(tempUser);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (InvalidJwtTokenException e) {
            log.debug("Invalid JWT token");
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .timestamp(new Date().toString())
                    .build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }
}
