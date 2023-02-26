package com.example.pieona.controller;

import com.example.pieona.common.SuccessMessage;
import com.example.pieona.dto.ListUser;
import com.example.pieona.dto.SignRequest;
import com.example.pieona.dto.SignResponse;
import com.example.pieona.dto.TokenDto;
import com.example.pieona.security.JpaUserDetailsService;
import com.example.pieona.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JpaUserDetailsService jpaUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<SignResponse> login(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping( "/signup")
    public SuccessMessage signUp(@RequestBody SignRequest request) throws Exception {
        userService.signUp(request);
        return new SuccessMessage();
    }

    @GetMapping("/signup/{email}/email")
    public ResponseEntity<Boolean> existEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.existEmail(email));
    }

    @GetMapping("/signup/{nickname}/nickname")
    public ResponseEntity<Boolean> existNickname(@PathVariable String nickname){
        return ResponseEntity.ok(userService.existNickname(nickname));
    }

    @GetMapping("/member/list")
    public ResponseEntity<ListUser> list(@RequestBody ListUser listUser){
        return new ResponseEntity<>(userService.listUser(listUser), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenDto token) throws Exception{
        return new ResponseEntity<>(userService.refreshAccessToken(token), HttpStatus.OK);
    }

    @GetMapping("/") // https 테스트용
    public String test(){
        return "test";
    }

}
