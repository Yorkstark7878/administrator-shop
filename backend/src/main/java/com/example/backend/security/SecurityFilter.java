package com.example.backend.security;

import com.example.backend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UserService usersService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //String urlRequest = request.getRequestURI();

/*
        try {
            String header = request.getHeader("Authorization");
            // Authentification
            // Authorization: bearer eyolfdilfsdkfdslkflsdkfjjdslfhlsdfjlsdfjlk
            String token = header.substring(7); //eyolfdilfsdkfdslkflsdkfjjdslfhlsdfjlsdfjlk
            token = JwtTokenManager.getUser(token);
            UserDetails user = usersService.findByToken(token);

            if (user == null){
                filterChain.doFilter(request, response);
                return;
            }
            // On le passe aux controllers grâce au context
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            //logger.info("Trying parse token but failed");
        }*/

        filterChain.doFilter(request, response);
    }
}