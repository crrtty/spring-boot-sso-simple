package com.ly.service;

import cn.hutool.jwt.JWTUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author robben
 * @date 2023/12/19
 */
@Service
public class LoginService {

    @Value("${jwt.secret}")
    private String secret;

    public String login(String username, String password){
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new RuntimeException("用户名密码错误");
        }
        if ("admin".equals(username) && "admin".equals(password)){
            return JWTUtil.createToken(ImmutableMap.of("uid",1,"username",username), secret.getBytes());
        }
        return null;
    }

    public boolean checkJwt(String token){
        return JWTUtil.verify(token, secret.getBytes());
    }
}
