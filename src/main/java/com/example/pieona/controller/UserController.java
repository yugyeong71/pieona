package com.example.pieona.controller;

import com.example.pieona.common.SuccessMessage;
import com.example.pieona.dto.SignRequest;
import com.example.pieona.dto.SignResponse;
import com.example.pieona.dto.TokenDto;
import com.example.pieona.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SignResponse> login(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping( "/signup")
    public SuccessMessage signUp(@RequestBody SignRequest request) throws Exception {
        userService.signUp(request);
        return new SuccessMessage();
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenDto token) throws Exception{
        return new ResponseEntity<>(userService.refreshAccessToken(token), HttpStatus.OK);
    }

}
