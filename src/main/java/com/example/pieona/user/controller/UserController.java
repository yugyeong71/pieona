package com.example.pieona.user.controller;

import com.example.pieona.common.SecurityUtil;
import com.example.pieona.common.SuccessMessage;
import com.example.pieona.user.dto.*;
import com.example.pieona.jwt.dto.TokenDto;
import com.example.pieona.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SignResponse> login(@RequestBody SignRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/user/logout")
    public SuccessMessage logout(String token, HttpServletRequest request){
        userService.logout(token, request);
        return new SuccessMessage();
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

    @PutMapping("/user/update")
    public ResponseEntity<SuccessMessage> updateUser(@RequestBody UpdateUserDto updateUserDto){
        return ResponseEntity.ok(userService.updateUser(updateUserDto));
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<SuccessMessage> delUser(){
        return new ResponseEntity<>(userService.delUser(), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenDto token) throws Exception{
        return new ResponseEntity<>(userService.refreshAccessToken(token), HttpStatus.OK);
    }

    @PutMapping("/user/password")
    public SuccessMessage updatePassword(@RequestBody UpdatePwdDto updatePwdDto) throws Exception {
        userService.updatePassword(updatePwdDto.checkPassword(),updatePwdDto.newPassword(), SecurityUtil.getLoginUsername());

        return new SuccessMessage();
    }

    @PostMapping("/findPw")
    public SuccessMessage findPwMail(@RequestBody Map<String, String> map) throws Exception{
        MailDto mailDto = userService.mailTemporary(map.get("email"));
        userService.mailSend(mailDto);

        return new SuccessMessage();
    }

}
