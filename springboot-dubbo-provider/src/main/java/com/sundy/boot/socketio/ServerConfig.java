package com.sundy.boot.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author plus.wang
 * @description socketio server 配置
 * @date 2018/4/16
 */
@Configuration
public class ServerConfig {

    @Value("${ws.host}")
    private String host;

    @Value("${ws.port}")
    private Integer port;

    @Bean
    public SocketIOServer server() {

        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();

        configuration.setHostname(host);

        configuration.setPort(port);

        configuration.setAuthorizationListener(data -> {
            //TODO
            // 握手协议参数使用JWT的Token认证方案
            String token = data.getSingleUrlParam("token");

            return true;
        });

        return new SocketIOServer(configuration);
    }


    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {

        return new SpringAnnotationScanner(socketServer);
    }

    @Bean
    public DefaultKaptcha captchaProducer() {

        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        Properties properties = new Properties();
        //验证码宽度
        properties.setProperty("kaptcha.image.width", "115");
        //验证码高度
        properties.setProperty("kaptcha.image.height", "35");
        //生成验证码内容范围
        properties.setProperty("kaptcha.textproducer.char.string", "abcde2345678gfynmnpwx");
        // 验证码个数
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        //是否有边框
        properties.setProperty("kaptcha.border", "no");
        //验证码字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "red");
        //验证码字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "24");
        // 验证码所属字体样式
        properties.setProperty("kaptcha.textproducer.font.names", "Arial, Courier");

        properties.setProperty("kaptcha.background.clear.from", "white");

        properties.setProperty("kaptcha.background.clear.to", "white");

        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");

        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        //干扰线颜色
        properties.setProperty("kaptcha.noise.color", "red");
        //验证码文本字符间距
        properties.setProperty("kaptcha.textproducer.char.space", "3");

        Config Config = new Config(properties);

        defaultKaptcha.setConfig(Config);

        return defaultKaptcha;
    }
}
