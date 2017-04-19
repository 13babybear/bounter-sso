package com.bounter.app2.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by admin on 2017/4/16.
 */
@Controller
public class AppController {

    @RequestMapping("/app2")
    public String protectedResource() {
        return "index";
    }
    
    /**
     * 接收SSO服务器发送的批量登出命令,进行简单登出
     * @return
     */
    @RequestMapping("/sso/batchLogout")
    public void batchLogout(HttpSession session) {
        if(session != null) {
        	session.invalidate();
        }
    }
}
