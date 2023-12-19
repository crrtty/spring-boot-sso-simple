package com.ly.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author robben
 * @date 2023/12/19
 */
@RestController
@RequestMapping("/b")
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "test b";
    }
}
