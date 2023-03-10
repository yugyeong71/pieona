package com.example.pieona.user.controller;

import com.example.pieona.common.SuccessMessage;
import com.example.pieona.user.dto.ListUser;
import com.example.pieona.user.dto.SignRequest;
import com.example.pieona.user.dto.SignResponse;
import com.example.pieona.jwt.dto.TokenDto;
import com.example.pieona.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SignResponse> login(@RequestBody SignRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessMessage> signUp(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @GetMapping("/signup/{email}/email")
    public ResponseEntity<Boolean> existEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.existEmail(email));
    }

    @GetMapping("/signup/{nickname}/nickname")
    public ResponseEntity<Boolean> existNickname(@PathVariable String nickname){
        return ResponseEntity.ok(userService.existNickname(nickname));
    }

    @GetMapping("/member/{id}")
    public ListUser list(@PathVariable Long id){
        return userService.listUser(id);
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenDto token) throws Exception{
        return new ResponseEntity<>(userService.refreshAccessToken(token), HttpStatus.OK);
    }


}
