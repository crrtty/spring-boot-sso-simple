package com.ly.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.google.common.collect.ImmutableMap;
import com.ly.constants.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author robben
 * @date 2023/12/19
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire:86400}")
//    @Value("${jwt.expire:30}")
    private long expireTime;
    private final RedisTemplate<String, Object> redisTemplate;

    public String login(String username, String password){
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new RuntimeException("用户名密码错误");
        }
        //DB校验用户名密码，登录成功后获取用户信息，包括uid，暂时默认都为1
        String uid = "100000001";
        if ("admin".equals(username) && "admin".equals(password)){
            String token = JWTUtil.createToken(ImmutableMap.of("uid", uid, "username", username, JWTPayload.EXPIRES_AT, DateUtil.currentSeconds() + expireTime), secret.getBytes());
            //存入redis
            redisTemplate.opsForValue().set(CommonConstant.SSO_TOKEN_PREFIX + uid, token, Duration.ofSeconds(expireTime));
            return token;
        }
        return null;
    }

    public boolean checkJwt(String token){
        if (StringUtils.isBlank(token)){
            return false;
        }
        boolean validate = JWT.of(token).setKey(secret.getBytes()).validate(expireTime);
        if (!validate){
            return false;
        }
        //解析token获取uid，判断redis中是否存在
        JWT jwt = JWTUtil.parseToken(token);
        String uid = String.valueOf(jwt.getPayload("uid"));
        if (StringUtils.isBlank(uid)){
            return false;
        }
        String tokenCache = (String) redisTemplate.opsForValue().get(CommonConstant.SSO_TOKEN_PREFIX + uid);
        return !StringUtils.isBlank(tokenCache) && token.equals(tokenCache);
    }

    public boolean logout(String uid){
        return Boolean.TRUE.equals(redisTemplate.delete(CommonConstant.SSO_TOKEN_PREFIX + uid));
    }
}
