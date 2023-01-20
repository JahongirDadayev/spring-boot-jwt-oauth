package com.example.springsocial.service;

import com.example.springsocial.entity.DbUser;
import com.example.springsocial.enums.Authority;
import com.example.springsocial.model.request.UserRequestModel;
import com.example.springsocial.model.response.ApiResponse;
import com.example.springsocial.model.request.TokenModel;
import com.example.springsocial.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public ApiResponse signUp(UserRequestModel userRequestModel) {
        if (!userRepository.existsByUsername(userRequestModel.getUsername())) {
            DbUser user = DbUser.builder().
                    firstName(userRequestModel.getFirstName())
                    .lastName(userRequestModel.getLastName())
                    .username(userRequestModel.getUsername())
                    .password(passwordEncoder.encode(userRequestModel.getPassword()))
                    .picture(userRequestModel.getPicture())
                    .enabled(true)
                    .authorities(Set.of(Authority.USER))
                    .build();
            userRepository.save(user);
            return new ApiResponse(null, new TokenModel(jwtService.generateToken(user, Map.of("authorities", user.getAuthorities()))), true);
        } else {
            return new ApiResponse("User already exists", null, false);
        }
    }

    public ApiResponse signIn(UserRequestModel userRequestModel) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestModel.getUsername(), userRequestModel.getPassword()));
            DbUser user = (DbUser) authenticate.getPrincipal();
            return new ApiResponse(null, new TokenModel(jwtService.generateToken(user, Map.of("authorities", user.getAuthorities()))), true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Login or password incorrect", null, false);
        }
    }

    public ApiResponse authenticationWithOauth(TokenModel tokenModel) {
        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(tokenModel.getToken());
            if (Objects.nonNull(firebaseToken) && !userRepository.existsByUsername(firebaseToken.getEmail())) {
                DbUser user = DbUser.builder()
                        .firstName(firebaseToken.getName().substring(0, firebaseToken.getName().indexOf(" ")))
                        .lastName(firebaseToken.getName().substring(firebaseToken.getName().indexOf(" ") + 1))
                        .username(firebaseToken.getEmail())
                        .picture(firebaseToken.getPicture())
                        .authorities(Set.of(Authority.USER))
                        .enabled(true)
                        .build();
                userRepository.save(user);
                return new ApiResponse(null, new TokenModel(jwtService.generateToken(user, Map.of("authorities", user.getAuthorities()))), true);
            } else {
                DbUser user = userRepository.findByUsername(firebaseToken.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
                return new ApiResponse(null, new TokenModel(jwtService.generateToken(user, Map.of("authorities", user.getAuthorities()))), true);
            }
        } catch (Exception e) {
            log.error("Firebase Error {}", e.getMessage());
            e.printStackTrace();
            return new ApiResponse("An error occurred", null, false);
        }
    }
}
