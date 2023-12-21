package com.ly.filter;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author robben
 * @date 2023/12/19
 */
@Component
@WebFilter(urlPatterns = "/**")
public class LoginFilter implements Filter {

    @Value("${sso.domain}")
    private String serverHost;
    @Value("${sso.clientId:web-b}")
    private String clientId;
    @Value("${local.domain:http://localhost:8082}")
    private String localUrl;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取cookie中的token
        Cookie cookie = ServletUtil.getCookie(request, "token");
        String cookieToken = Objects.isNull(cookie)?null:cookie.getValue();
        //根据url的不同执行不同的操作
        if ("/logout".equals(request.getRequestURI())){
            //登出
            this.logout(cookieToken, clientId);
        } else if ("/setCookie".equals(request.getRequestURI())) {
            //客户端设置cookie
            this.setCookie(request, response);
        } else if (StringUtils.isNotBlank(cookieToken) && this.check(cookieToken)) {
            //token存在且有效，放行
            filterChain.doFilter(servletRequest, servletResponse);
        } else{
            //直接跳转统一登录页面，带上redirect url
            response.sendRedirect(serverHost + "/login?clientId=" + clientId + "&redirectUrl=" + request.getRequestURI());
        }
    }

    private void logout(String token, String clientId){
        //如果对返回结果有依赖可自行扩充
        HttpUtil.post(serverHost + "/check", ImmutableMap.of("token", token, "clientId", clientId), 2000);
    }

    private void setCookie(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletUtil.addCookie(response, "token", request.getParameter("token"), -1);
        String redirectUrl = request.getParameter("redirectUrl");
        if (StringUtils.isNotBlank(redirectUrl)){
            response.sendRedirect(localUrl + redirectUrl);
        }
    }

    private boolean check(String token){
        if (StringUtils.isBlank(token)){
            return false;
        }
        String result = HttpUtil.get(serverHost + "/check", ImmutableMap.of("token", token,"clientId", clientId), 2000);
        if (StringUtils.isBlank(result)){
            return false;
        }
        return Boolean.parseBoolean(result);
    }
}
