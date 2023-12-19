package com.ly.filter;

import cn.hutool.http.HttpUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author robben
 * @date 2023/12/19
 */
@Component
@WebFilter(urlPatterns = "/**")
public class LoginFilter implements Filter {

    @Value("${sso.server}")
    private String serverHost;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = httpServletRequest.getParameter("token");
        if (this.check(token)){
            filterChain.doFilter(servletRequest, servletResponse);
        }else{
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.sendRedirect(serverHost + "/login");
        }
    }

    private boolean check(String token){
        if (StringUtils.isBlank(token)){
            return false;
        }
        String result = HttpUtil.get(serverHost + "/check", ImmutableMap.of("token", token), 2000);
        if (StringUtils.isBlank(result)){
            return false;
        }
        return Boolean.parseBoolean(result);
    }
}
