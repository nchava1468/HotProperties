package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.dtos.JwtResponse;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.jwt.JwtUtil;
import com.hotproperties.hotproperties.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public JwtResponse authenticateAndGenerateToken(User user) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return new JwtResponse(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public Cookie loginAndCreateJwtCookie(User user) throws BadCredentialsException {
        JwtResponse jwtResponse = authenticateAndGenerateToken(user);

        Cookie jwtCookie = new Cookie("jwt", jwtResponse.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1 hour

        return jwtCookie;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}