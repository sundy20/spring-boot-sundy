package com.sundy.boot.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author plus.wang
 * @description
 * @date 2018/4/13
 */
@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = {"/", "/index"})
    public String index() {

        return "index";
    }
}
