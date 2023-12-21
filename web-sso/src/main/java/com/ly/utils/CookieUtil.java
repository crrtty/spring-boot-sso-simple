package com.ly.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author robben
 * @date 2023/12/20
 */
public class CookieUtil {

    /**
     * 得到cookie的域名
     */
    public static final String getDomainName(HttpServletRequest request) {
        String domainName = null;

        // 获取完整的请求URL地址。
        String serverName = request.getRequestURL().toString();
        if (serverName == null || serverName.equals("")) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            if (serverName.startsWith("http://")){
                serverName = serverName.substring(7);
            } else if (serverName.startsWith("https://")){
                serverName = serverName.substring(8);
            }
            //这里有可能域名只有，例如: jwt.io ,spring.io，那么这样end为-1
            final int end = serverName.indexOf("/");
            if (end != -1) {
                // .test.com  www.test.com.cn/sso.test.com.cn/.test.com.cn  spring.io/xxxx/xxx
                serverName = serverName.substring(0, end);
            }
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                //spring boot api 不支持这样的domain格式，当然也可以配置
                //参考：https://blog.csdn.net/doctor_who2004/article/details/81750713
                //domainName = "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
                domainName = domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                //domainName = "." + domains[len - 2] + "." + domains[len - 1];
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName != null && domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }
}
