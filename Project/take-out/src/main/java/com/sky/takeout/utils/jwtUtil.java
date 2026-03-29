package com.sky.takeout.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class jwtUtil {
    //签名密钥（实际项目从配置中获取，实际绝对不能硬编码。此处练习用）
    private static final String SECRET_KEY = "sky-take-out-secret-key-2026";
    //token有效期（毫秒）
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 12;

    //生成JWT令牌
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder() //创建jwtBuilder对象
                .setClaims(claims) //载荷
                .setIssuedAt(new Date()) //签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) //防止令牌被篡改
                .compact(); //拼接
    }

    //解析JWT令牌
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)//设置签名密钥，必须和生成时相同
                .parseClaimsJws(token) //解析令牌，验证签名和格式
                .getBody();
    }

    //验证令牌是否过期
    public static boolean isTokenExpired(String token) {
        //先解析令牌
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            //解析异常视为过期
            return true;
        }
    }
}
