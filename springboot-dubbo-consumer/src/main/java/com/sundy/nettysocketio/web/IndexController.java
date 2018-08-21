package com.sundy.nettysocketio.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author plus.wang
 * @description
 * @date 2018/4/13
 */
@Controller
public class IndexController {

    @RequestMapping(value = {"/", "/index"})
    public String index() {

        return "index";
    }
}
