package com.juemuel.trend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

// 允许预检请求
@RestController
public class CorsController {

    @RequestMapping(value = "/api/**", method = RequestMethod.OPTIONS)
    public void handleOptions(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setStatus(HttpServletResponse.SC_OK);  // 增加了 .SC_OK 来明确返回 200，这有助于预检请求通过
    }
}
