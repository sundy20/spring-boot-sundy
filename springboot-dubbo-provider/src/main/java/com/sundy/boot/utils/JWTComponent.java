package com.sundy.boot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author plus.wang
 * @description jwt 签名组件
 * @date 2018/5/2
 */
@Component
public class JWTComponent {

    private static final Logger log = LoggerFactory.getLogger(JWTComponent.class);

    /**
     * 生成签名
     *
     * @param username 用户名
     */
    public String sign(String username, String secret) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create().withClaim("username", username).withIssuedAt(new Date()).sign(algorithm);

        } catch (UnsupportedEncodingException e) {

            log.error("JWTComponent.sign error", e);

            return null;
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token    密钥
     * @param username 用户名
     */
    public boolean verify(String token, String username, String secret) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();

            verifier.verify(token);

            return true;

        } catch (Exception exception) {

            log.error("JWTComponent.verify error", exception);

            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     */
    public String getUsername(String token) {

        try {

            DecodedJWT jwt = JWT.decode(token);

            return jwt.getClaim("username").asString();

        } catch (JWTDecodeException e) {

            log.error("JWTComponent.getUsername error", e);

            return null;
        }
    }

    public static void main(String[] args) {

        JWTComponent jwtUtil = new JWTComponent();

        String sign = jwtUtil.sign("sundy", "wz555666888");

        System.out.println(sign);

        String username = jwtUtil.getUsername(sign);

        System.out.println(username);

        boolean verify = jwtUtil.verify(sign, "sundy", "wz555666888");

        System.out.println(verify);
    }
}
