package com.bounter.sso.controller;

import com.alibaba.fastjson.JSONObject;
import com.bounter.sso.service.SSOService;
import com.bounter.sso.utility.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by admin on 2017/4/16.
 */
@Controller
public class SSOController {
    @Autowired
    private SSOService ssoService;

    @RequestMapping(value = "/sso/login", method={RequestMethod.GET, RequestMethod.POST})
    public String ssoLogin(HttpSession session, HttpServletResponse response, String username, String password, String redirect) throws IOException {
        //登录验证
        boolean loginSuccess = ssoService.loginCheck(username,password);

        //登录认证成功
        if (loginSuccess) {
            //创建sso-token
            String ssoToken = UUID.randomUUID().toString();
            //将token保存到session中
            session.setAttribute("sso-token",ssoToken);
            //将用户信息保存到token中
            session.setAttribute("uid",username);
            CookieUtil.create(response, "sso-token", ssoToken, false, -1);
            //带着sso-token重定向到app页面
            return "redirect:" + redirect + "?sso-token=" + ssoToken;
        } else {
            return "login";
        }
    }

    @RequestMapping("/sso/authenticate")
    @ResponseBody
    public JSONObject ssoAuthenticate(HttpServletRequest request, String ssoToken) {
        HttpSession session = request.getSession();
        JSONObject retObj = new JSONObject();
        if(session.getAttribute("sso-token") != null && ssoToken.equals(session.getAttribute("sso-token"))) {
            retObj.put("ret", "success");
            retObj.put("uid", session.getAttribute("uid"));
        } else {
            retObj.put("ret", "failure");
        }
        return retObj;
    }
}
