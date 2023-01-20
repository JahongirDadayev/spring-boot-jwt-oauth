package com.example.springsocial.controller;

import com.example.springsocial.model.request.UserRequestModel;
import com.example.springsocial.model.response.ApiResponse;
import com.example.springsocial.model.request.TokenModel;
import com.example.springsocial.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(path = "sign_up")
    public ResponseEntity<ApiResponse> signUp(@RequestBody UserRequestModel userRequestModel){
        ApiResponse apiResponse = authService.signUp(userRequestModel);
        return (apiResponse.getSuccess())?ResponseEntity.status(HttpStatus.CREATED).body(apiResponse):ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PostMapping(path = "/sign_in")
    public ResponseEntity<ApiResponse> signIn(@RequestBody UserRequestModel userRequestModel){
        ApiResponse apiResponse = authService.signIn(userRequestModel);
        return (apiResponse.getSuccess())?ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponse):ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PostMapping(path = "/oauth")
    public ResponseEntity<ApiResponse> authenticationWithOauth(@RequestBody TokenModel tokenModel) {
        ApiResponse apiResponse = authService.authenticationWithOauth(tokenModel);
        return (apiResponse.getSuccess())?ResponseEntity.status(HttpStatus.CREATED).body(apiResponse):ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
