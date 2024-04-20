package com.task.todoapps.services;

import com.task.todoapps.auth.AuthenticationRequest;
import com.task.todoapps.auth.AuthenticationResponse;
import com.task.todoapps.auth.RegisterRequest;
import com.task.todoapps.config.JwtService;
import com.task.todoapps.model.User;
import com.task.todoapps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final  UserServices userServices;

    public ResponseEntity<?> authenticate(AuthenticationRequest authenticationRequest){
        try {
            User user=userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new NoSuchElementException("User not found"));
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),authenticationRequest.getPassword()));

            Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
            var jwtAccessToken= jwtService.generateToken(user);
            var jwtRefreshToken=jwtService.generateRefreshToken(user);
            return ResponseEntity.ok(AuthenticationResponse.builder().access_token(jwtAccessToken).refresh_token(jwtRefreshToken).email(user.getEmail()).name(user.getName()).build());

        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }catch (BadCredentialsException e){
            return ResponseEntity.badRequest().body("Invalid Crendential");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    public ResponseEntity<?> register(RegisterRequest registerRequest) {

        try {
            if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
                throw new IllegalArgumentException("user with"+registerRequest.getEmail().toString()+"email already exists");
            }
            userServices.saveUser(new User(registerRequest.getName(),registerRequest.getEmail(),registerRequest.getPassword()));
            User user=userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();
            return  ResponseEntity.ok(user);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }
    }

}
