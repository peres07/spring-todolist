package com.learningjava.todolist.infra.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.learningjava.todolist.entity.UserModal;
import com.learningjava.todolist.repository.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            UUID subject = UUID.fromString(tokenService.validateToken(token));
            UserModal user = userRepository.findById(subject).orElse(null);

            try {
                assert user != null;
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", subject);
            } catch (Exception e) {
                response.sendError(401);
                return;
            }

        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.startsWith("Basic")) return null;
        return authHeader.replace("Bearer ", "");
    }
}
