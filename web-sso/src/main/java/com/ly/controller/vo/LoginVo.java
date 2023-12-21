package com.ly.controller.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author robben
 * @date 2023/12/20
 */
@Data
@Builder
public class LoginVo {

    /**
     * 登录账号（用户名、邮箱、账号ID）
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 子系统clientId
     */
    private String clientId;
    /**
     * 重定向url
     */
    private String redirectUrl;
}
