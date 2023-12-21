package com.ly.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author robben
 * @date 2023/12/19
 */
public class CommonConstant {

    public static final String SSO_TOKEN_PREFIX = "sso:t:";

    /**
     * clientId-子系统url映射
     */
    public static final Map<String,String> clientMap = ImmutableMap.of("web-a", "http://localhost:8081","web-b","http://localhost:8082");

}
