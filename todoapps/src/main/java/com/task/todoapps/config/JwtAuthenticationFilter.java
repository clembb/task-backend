package com.task.todoapps.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.todoapps.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private final UserRepository userRepository;

    @Value("${secret.key}")
    private  String secretkey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader=request.getHeader(AUTHORIZATION);
        if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            try {
                String token=authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm=Algorithm.HMAC256(secretkey.getBytes());
                JWTVerifier verifier= JWT.require(algorithm).build();
                DecodedJWT decodedJWT=verifier.verify(token);
                String username=decodedJWT.getSubject();
                userRepository.findByEmail(username).orElseThrow(()-> new Exception("invalid Token"));
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= new UsernamePasswordAuthenticationToken(username,null);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request,response);
            }catch (Exception e){
                ErrorResponse errroResponse=new ErrorResponse(FORBIDDEN,e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(errroResponse.getStatusCodeValue());
                new ObjectMapper().writeValue(response.getOutputStream(),errroResponse);
            }

        }else {
            if (isRegistrationOrLoginEndpoint(request)) {
                filterChain.doFilter(request, response);
            } else {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header missing or invalid");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(errorResponse.getStatusCodeValue());
                new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
            }

        }
    }
    private boolean isRegistrationOrLoginEndpoint(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.equals("/register") || requestURI.equals("/login");
    }
}
