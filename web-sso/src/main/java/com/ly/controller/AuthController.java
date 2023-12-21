package com.ly.controller;

import com.google.common.collect.ImmutableMap;
import com.ly.controller.vo.LoginVo;
import com.ly.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author robben
 * @date 2023/12/19
 */
@Controller
@RequestMapping("/sso")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    /**
     * 返回统一登录页面
     * @return
     */
    @GetMapping("/login")
    public ModelAndView login(String redirectUrl, String clientId){
        return new ModelAndView("login", ImmutableMap.of("redirectUrl", redirectUrl, "clientId", clientId));
    }

    /**
     * 预登录，校验用户是否已登录，未登录跳转统一登录页面，已登录设置客户端cookie后重定向回客户端页面
     * @param request
     * @return
     */
    @GetMapping("/preLogin")
    public String preLogin(HttpServletRequest request){
        return "redirect:" + loginService.preLogin(request);
    }

    /**
     * 统一登录
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        return loginService.login(loginVo, request, response);
    }

    /**
     * 校验token是否生效
     * @param token jwt token
     * @param clientId 子系统clientId
     * @return
     */
    @GetMapping("/check")
    @ResponseBody
    public boolean checkJwt(String token, String clientId){
        return loginService.checkJwt(token, clientId);
    }

    /**
     * 登出
     * @param token jwt token
     * @param clientId 子系统clientId
     * @return
     */
    @PostMapping("/logout")
    @ResponseBody
    public boolean logout(String token, String clientId){
        return loginService.logout(token, clientId);
    }
}
