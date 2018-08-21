package com.sundy.boot.web.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.sundy.boot.domain.UserBean;
import com.sundy.boot.domain.UserService;
import com.sundy.boot.exception.UnauthorizedException;
import com.sundy.boot.redis.RedisComponent;
import com.sundy.boot.utils.CodecUtil;
import com.sundy.boot.utils.CookieUtil;
import com.sundy.boot.utils.JWTComponent;
import com.sundy.boot.utils.WebUtil;
import com.sundy.boot.web.filter.SessionScopeValues;
import com.sundy.share.dto.Result;
import com.sundy.share.enums.ResultCode;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private DefaultKaptcha captchaProducer;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTComponent jwtUtil;

    @Autowired
    private RedisComponent redisComponent;

    @PostMapping("/login")
    public Result<String> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest httpServletRequest) {

        UserBean userBean = userService.getUser(username);

        if (userBean.getPassword().equals(password)) {

            String token = jwtUtil.sign(username, password);

            String userAgent = WebUtil.getUserAgent(httpServletRequest);

            String ip = WebUtil.getHost(httpServletRequest);

            String juuid = CookieUtil.getValue(httpServletRequest, "juuid");

            SessionScopeValues sessionScopeValues = new SessionScopeValues(token, userAgent, ip, userBean.getUserId(), juuid);

            log.info("WebController.login sessionScopeValues: {}", JSON.toJSONString(sessionScopeValues));

            HttpSession httpSession = httpServletRequest.getSession();

            log.info("JWTFilter.executeLogin sessionId : {} , httpSession: {}", httpSession.getId(), JSON.toJSONString(httpSession, SerializerFeature.WriteMapNullValue));

            //根据登录成功后的token 放入redis——>filter 信息
            return Result.success(token);

        } else {

            throw new UnauthorizedException();
        }
    }

    @GetMapping(value = "/captcha")
    public String captcha() {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            String capText = captchaProducer.createText();

            String uuid = CodecUtil.createUUID();

            redisComponent.set(uuid, capText, 60, TimeUnit.SECONDS);

            BufferedImage bi = captchaProducer.createImage(capText);

            ImageIO.write(bi, "jpeg", baos);

            String imgBase64 = Base64.encodeBase64String(baos.toByteArray());

            return "data:image/jpeg;base64," + imgBase64;

        } catch (IOException e) {

            log.error("WebController.captcha error : ", e);

            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @GetMapping(value = "/reqNo")
    public Result<String> reqNo() {

        return Result.success(CodecUtil.createUUID());
    }

    @GetMapping("/secure/article")
    public Result<String> article() {

        Subject subject = SecurityUtils.getSubject();

        if (subject.isAuthenticated()) {

            return Result.success(ResultCode.SUCCESS.getCode(), "You are already logged in");

        } else {

            return Result.success("200002", "You are guest");
        }
    }

    @GetMapping("/secure/require_auth")
    @RequiresAuthentication
    public Result<String> requireAuth() {
        return Result.success(ResultCode.SUCCESS.getCode(), "You are authenticated");
    }

    @GetMapping("/secure/require_role")
    @RequiresRoles("admin")
    public Result<String> requireRole() {

        return Result.success(ResultCode.SUCCESS.getCode(), "You are visiting require_role");
    }

    @GetMapping("/secure/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public Result<String> requirePermission() {

        return Result.success(ResultCode.SUCCESS.getCode(), "You are visiting permission require edit,view");
    }

    @RequestMapping(path = "/401", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<String> unauthorized() {

        return Result.failure("401", "Unauthorized");
    }

    /**
     * /loggers  查看日志级别
     * /loggers/com.sundy.boot post请求  {"configuredLevel": "DEBUG"} 动态修改日志级别
     */
    @ResponseBody
    @RequestMapping(value = "/loglevel", method = RequestMethod.GET)
    public String testLogLevel() {

        log.debug("Logger Level ：DEBUG");

        log.warn("Logger Level ：WARN");

        log.info("Logger Level ：INFO");

        log.error("Logger Level ：ERROR");

        return "";
    }
}
