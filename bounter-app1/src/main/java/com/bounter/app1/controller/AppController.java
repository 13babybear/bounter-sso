package com.bounter.app1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by admin on 2017/4/16.
 */
@Controller
public class AppController {

    @RequestMapping("/app1")
    public String protectedResource() {
        return "index";
    }
}
