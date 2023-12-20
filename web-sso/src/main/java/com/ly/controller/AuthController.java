package com.ly.controller;

import com.ly.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author robben
 * @date 2023/12/19
 */
@Controller
@RequestMapping("/sso")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String login(){
        return "login.html";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(String username, String password){
        return loginService.login(username, password);
    }

    @GetMapping("/check")
    @ResponseBody
    public boolean checkJwt(String token){
        return loginService.checkJwt(token);
    }

    @GetMapping("/logout")
    @ResponseBody
    public boolean logout(String uid){
        return loginService.logout(uid);
    }
}
