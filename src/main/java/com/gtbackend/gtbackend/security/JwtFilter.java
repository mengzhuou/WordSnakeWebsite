package com.gtbackend.gtbackend.security;

import com.gtbackend.gtbackend.model.User;
import com.gtbackend.gtbackend.dao.UserLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserLoader userLoader;

    public JwtFilter(JwtService jwtService, UserLoader userLoader) {
        this.jwtService = jwtService;
        this.userLoader = userLoader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")){
            try {
                String jwtToken = extractJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION));

                if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String email = jwtService.extractUsername(jwtToken);
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    List<String> roles = jwtService.extractRoles(jwtToken);
                    for (String role: roles){
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                    Optional<User> user = userLoader.loadUserByEmail(email);

                    if (user.isPresent() && jwtService.validateToken(jwtToken, user.get())) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.get(), null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        String newToken;

                        try {
                            newToken = jwtService.generateToken(user.get());
                            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);
                        } catch (Exception e) {
                            logger.error("Failed to generate JWT token.", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
