package com.sky.takeout.config;

import com.sky.takeout.utils.JwtUtil;
import com.sky.takeout.utils.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    /*  请求到达Controller之前执行
        返回true：放行
        返回false：拦截，不继续执行
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头获取token
        String token = request.getHeader("token");

        //token为空，拒绝访问
        if (!StringUtils.hasText(token)) {
            response.setStatus(401); //未授权
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0, \"msg\":\"未登录，请先登录\"}");
        }
        //解析token
        try {
            Claims claims = JwtUtil.parseToken(token);

            //将UserId存入ThreadLocal
            UserContext.setCurrentId(Long.valueOf(claims.get("id").toString()));

            //放行
            return true;
        } catch (Exception e) {
            //token无效或过期
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401, \"msg\":\"token已过期，请重新登录\"}");
            return false;
        }
    }

    /*请求完成后执行
    * ThreadLocal必须清除，防止内存泄露*/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
