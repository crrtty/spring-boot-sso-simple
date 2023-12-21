package com.ly.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.google.common.collect.ImmutableMap;
import com.ly.constants.CommonConstant;
import com.ly.controller.vo.LoginVo;
import com.ly.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Objects;

/**
 * @author robben
 * @date 2023/12/19
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.ssoUrl:http://localhost:8080/sso/login}")
    private String ssoUrl;
//    @Value("${jwt.expire:86400}")
    @Value("${jwt.expire:300}")
    private long expireTime;
    private final RedisTemplate<String, Object> redisTemplate;

    public String preLogin(HttpServletRequest request){
        String redirectUrl = request.getParameter("redirectUrl");
        String setCookieURL = request.getParameter("setCookieURL");
        String clientId = request.getParameter("clientId");
        Cookie cookie = ServletUtil.getCookie(request, "token");
        String token = Objects.isNull(cookie)?null:cookie.getValue();
        String ssoServerURL = ssoUrl + "?setCookieURL="+setCookieURL+"&redirectUrl="+redirectUrl;
        if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(clientId)){
            //校验token是否有效，有效则设置客户端cookie
            if (this.checkJwt(token, clientId) && StringUtils.isNotBlank(setCookieURL)){
                return setCookieURL + "?token=" + token + "&redirectUrl=" + redirectUrl;
            }
        }
        return ssoServerURL;
    }

    public String login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        if (StringUtils.isBlank(loginVo.getUsername()) || StringUtils.isBlank(loginVo.getPassword())){
            throw new RuntimeException("用户名密码错误");
        }
        //校验clientId，获取子系统对应的域名，方便redirectUrl、setCookieURL拼接，暂时从map中获取
        String clientUrl = CommonConstant.clientMap.get(loginVo.getClientId());
        if (StringUtils.isBlank(clientUrl)){
            throw new RuntimeException("非法请求");
        }
        //DB校验用户名密码，登录成功后获取用户信息，包括uid，暂时默认都为100000001
        String uid = "100000001";
        if ("admin".equals(loginVo.getUsername()) && "admin".equals(loginVo.getPassword())){
            String token = JWTUtil.createToken(ImmutableMap.of("clientId", loginVo.getClientId(), "uid", uid, "username", loginVo.getUsername(), JWTPayload.EXPIRES_AT, DateUtil.currentSeconds() + expireTime), secret.getBytes());
            //存入redis
            redisTemplate.opsForValue().set(CommonConstant.SSO_TOKEN_PREFIX + loginVo.getClientId() + uid, token, Duration.ofSeconds(expireTime));
            //存入cookie
            ServletUtil.addCookie(response, "token", token, -1, "/", CookieUtil.getDomainName(request));
            return clientUrl + "/setCookie?token=" + token + "&redirectUrl=" + loginVo.getRedirectUrl();
        }
        //返回默认登录页面
        return ssoUrl + "?clientId=" + loginVo.getClientId() + "&setCookieURL=setCookie&redirectUrl=" + loginVo.getRedirectUrl();
    }

    public boolean checkJwt(String token, String targetClientId){
        if (StringUtils.isBlank(token) || StringUtils.isBlank(targetClientId)){
            return false;
        }
        boolean validate = JWT.of(token).setKey(secret.getBytes()).validate(expireTime);
        if (!validate){
            return false;
        }
        //解析token获取uid，判断redis中是否存在
        JWT jwt = JWTUtil.parseToken(token);
        String sourceClientId = String.valueOf(jwt.getPayload("clientId"));
        String uid = String.valueOf(jwt.getPayload("uid"));
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(sourceClientId) || !sourceClientId.equals(targetClientId)){
            return false;
        }
        String tokenCache = (String) redisTemplate.opsForValue().get(CommonConstant.SSO_TOKEN_PREFIX + targetClientId + uid);
        return !StringUtils.isBlank(tokenCache) && token.equals(tokenCache);
    }

    public boolean logout(String token, String targetClientId){
        //解析token获取uid，判断redis中是否存在
        JWT jwt = JWTUtil.parseToken(token);
        String sourceClientId = String.valueOf(jwt.getPayload("clientId"));
        String uid = String.valueOf(jwt.getPayload("uid"));
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(sourceClientId) || !sourceClientId.equals(targetClientId)){
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.delete(CommonConstant.SSO_TOKEN_PREFIX + targetClientId + uid));
    }
}
