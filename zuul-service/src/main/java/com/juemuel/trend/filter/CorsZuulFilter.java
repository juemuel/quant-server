package com.juemuel.trend.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class CorsZuulFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre"; // 请求前拦截
    }

    @Override
    public int filterOrder() {
        return 0; // 执行顺序，越小越先执行
    }

    @Override
    public boolean shouldFilter() {
        return true; // 总是启用这个过滤器
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");

        return null;
    }
}
