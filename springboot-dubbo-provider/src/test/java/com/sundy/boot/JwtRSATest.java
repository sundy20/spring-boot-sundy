package com.sundy.boot;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sundy.boot.utils.RSAKeyUtil;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * JWT RSA test
 */
public class JwtRSATest {
    private static JWTVerifier jwtVerifier;
    private static Algorithm algorithmRSA256Private;
    private static Algorithm algorithmRSA256Public;

    @BeforeClass
    public static void setUp() throws Exception {
        byte[] privateKey = IOUtils.toByteArray(JwtRSATest.class.getResourceAsStream("/rsa_keys/private_key.der"));
        byte[] publicKey = IOUtils.toByteArray(JwtRSATest.class.getResourceAsStream("/rsa_keys/public_key.der"));
        algorithmRSA256Private = Algorithm.RSA256(null, RSAKeyUtil.readPrivateKey(privateKey));
        algorithmRSA256Public = Algorithm.RSA256(RSAKeyUtil.readPublicKey(publicKey), null);
        jwtVerifier = JWT.require(algorithmRSA256Public).withIssuer("mvnsearch").build();
    }

    @Test
    public void testGenerateToken() throws Exception {
        String token = JWT.create()
                .withIssuer("mvnsearch")
                .withSubject("xyxy")
                .withIssuedAt(new Date())
                .withArrayClaim("authorities", new String[]{"SMS"})
                .sign(algorithmRSA256Private);
        System.out.println(token);
    }

    @Test
    public void testVerify() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ4eXh5IiwiaXNzIjoibXZuc2VhcmNoIiwiaWF0IjoxNTE2MjU4ODMxLCJhdXRob3JpdGllcyI6WyJTTVMiXX0.eKTXsQsnKA3l6Hy-5dmf7CFL7zntfmbrkDIG3dnc6FrTtLO3BrvPpj2d2X_ULDVUU9k5n3rIXfKVlD3qHtgtytYF6Sn5MpqbHUYRttDb4NQnM5MUJKY8LNfCk05gJ5xMYJLa9Vk52oe7h42TT2O01KrSX-rLjJaUxbCL3Cz4XQlmbc5o-1hIoc7G-QgSPT6b-zXDWROFSBl0J6eIsifQbL2FaM2SeOKoiwpBQQW3-YOFShMK58Ns7mDj0lAiOqq1yCoFbigAyeSM-y32tye68aJmaWZB_1KxO-h4PTfR9JHVu1BbWcxgPudGPM071fWQB1TB5rPDfVrd8eYuT0rOCg";
        DecodedJWT jwt = jwtVerifier.verify(token);
        Claim claim = jwt.getClaim("authorities");
        String[] authorities = claim.asArray(String.class);
        for (String authority : authorities) {
            System.out.println(authority);
        }
    }
}
