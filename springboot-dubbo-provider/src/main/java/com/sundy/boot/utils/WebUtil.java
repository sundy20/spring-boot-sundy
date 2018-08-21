package com.sundy.boot.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Web 操作工具类
 */
public class WebUtil {

    private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);

    private static final String CHARSET = "UTF-8";

    /**
     * 将数据以 JSON 格式写入响应中
     */
    public static void writeJSON(HttpServletResponse response, Object data) {
        try {
            // 设置响应头
            response.setContentType("application/json"); // 指定内容类型为 JSON 格式
            response.setCharacterEncoding(CHARSET); // 防止中文乱码
            // 向响应中写入数据
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(data, SerializerFeature.WriteMapNullValue));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("在响应中写数据出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 将数据以 HTML 格式写入响应中（在 JS 中获取的是 JSON 字符串，而不是 JSON 对象）
     */
    public static void writeHTML(HttpServletResponse response, Object data) {
        try {
            // 设置响应头
            response.setContentType("text/html"); // 指定内容类型为 HTML 格式
            response.setCharacterEncoding(CHARSET); // 防止中文乱码
            // 向响应中写入数据
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(data, SerializerFeature.WriteMapNullValue));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("在响应中写数据出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 转发请求
     */
    public static void forwardRequest(String path, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception e) {
            logger.error("转发请求出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 重定向请求
     */
    public static void redirectRequest(String path, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(request.getContextPath() + path);
        } catch (Exception e) {
            logger.error("重定向请求出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送错误代码
     */
    public static void sendError(int code, String message, HttpServletResponse response) {
        try {
            response.sendError(code, message);
        } catch (Exception e) {
            logger.error("发送错误代码出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是否为 AJAX 请求
     */
    public static boolean isAJAX(HttpServletRequest request) {
        return request.getHeader("x-requested-with") != null;
    }

    /**
     * 创建验证码
     */
    public static String createCaptcha(HttpServletResponse response) {
        StringBuilder captcha = new StringBuilder();
        try {
            // 参数初始化
            int width = 60;                      // 验证码图片的宽度
            int height = 25;                     // 验证码图片的高度
            int codeCount = 4;                   // 验证码字符个数
            int codeX = width / (codeCount + 1); // 字符横向间距
            int codeY = height - 4;              // 字符纵向间距
            int fontHeight = height - 2;         // 字体高度
            int randomSeed = 10;                 // 随机数种子
            char[] codeSequence = {              // 验证码中可出现的字符
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
            };
            // 创建图像
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            // 将图像填充为白色
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            // 设置字体
            g.setFont(new Font("Courier New", Font.BOLD, fontHeight));
            // 绘制边框
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, width - 1, height - 1);
            // 产生随机干扰线（160条）
            g.setColor(Color.WHITE);
            // 创建随机数生成器
            Random random = new Random();
            for (int i = 0; i < 160; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, x + xl, y + yl);
            }
            // 生成随机验证码
            int red, green, blue;
            for (int i = 0; i < codeCount; i++) {
                // 获取随机验证码
                String validateCode = String.valueOf(codeSequence[random.nextInt(randomSeed)]);
                // 随机构造颜色值
                red = random.nextInt(255);
                green = random.nextInt(255);
                blue = random.nextInt(255);
                // 将带有颜色的验证码绘制到图像中
                g.setColor(new Color(red, green, blue));
                g.drawString(validateCode, (i + 1) * codeX - 6, codeY);
                // 将产生的随机数拼接起来
                captcha.append(validateCode);
            }
            // 禁止图像缓存
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            // 设置响应类型为 JPEG 图片
            response.setContentType("image/jpeg");
            // 将缓冲图像写到 Servlet 输出流中
            ServletOutputStream sos = response.getOutputStream();
            ImageIO.write(bi, "jpeg", sos);
            sos.close();
        } catch (Exception e) {
            logger.error("创建验证码出错！", e);
            throw new RuntimeException(e);
        }
        return captcha.toString();
    }

    /**
     * 是否为 IE 浏览器
     */
    public static boolean isIE(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent").toLowerCase();
        return (agent != null && agent.contains("MSIE")) || (agent != null && agent.contains("trident"));
    }

    /**
     * 是否为 微信浏览器
     */
    public static boolean isWEIXIN(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent").toLowerCase();
        return agent != null && agent.contains("micromessenger");
    }

    /**
     * 获取user agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent").toLowerCase();
        return agent;
    }

    /**
     * 获取客户端IP
     */
    public static final String getHost(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if ("127.0.0.1".equals(ip)) {
            InetAddress inet = null;
            try { // 根据网卡取本机配置的IP
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                logger.error("根据网卡取本机配置的IP 发生异常：", e);
            }

            ip = inet.getHostAddress();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 判断是否是手机浏览器
     *
     * @param request
     * @return
     */
    public static final boolean isMoblie(HttpServletRequest request) {
        boolean isMoblie = false;
        String[] mobileAgents = {"iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
                "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",
                "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
                "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",
                "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",
                "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
                "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",
                "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",
                "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",
                "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",
                "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",
                "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
                "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tsm-", "upg1", "upsi", "vk-v",
                "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",
                "Googlebot-Mobile"};
        if (request.getHeader("User-Agent") != null) {
            for (String mobileAgent : mobileAgents) {
                if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
                    isMoblie = true;
                    break;
                }
            }
        }
        return isMoblie;
    }

}
