package com.bounter.sso.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bounter.sso.service.SSOService;
import com.bounter.sso.utility.CookieUtil;
import com.bounter.sso.utility.ResponseObject;

/**
 * Created by admin on 2017/4/16.
 */
@Controller
public class SSOController {
    @Autowired
    private SSOService ssoService;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    @RequestMapping("/")
    public String home(HttpServletRequest request) {
        return "login";
    }
    
    @RequestMapping(value = "/sso/login", method={RequestMethod.GET, RequestMethod.POST})
    public String ssoLogin(HttpServletRequest request, HttpServletResponse response, String username, String password, String redirect) throws IOException {
    	//先判断Cookie中是否有sso-token,如果有sso-token跳过登录
    	String ssoToken = CookieUtil.getValue(request, "sso-token");
    	if(ssoToken != null) {
    		//把app地址保存到key为sso-token的redis集合中
            setOps.add(ssoToken,redirect);
    		return "redirect:" + redirect + "?sso-token=" + ssoToken;
    	}
    	
    	//登录认证
        boolean loginSuccess = ssoService.loginCheck(username,password);
        
        //登录认证成功
        if (loginSuccess) {
            //创建sso-token
            ssoToken = UUID.randomUUID().toString();
            //把app地址保存到key为sso-token的redis集合中
            setOps.add(ssoToken,redirect);
            //设置过期时间为30分钟
            stringRedisTemplate.expire(ssoToken,30,TimeUnit.MINUTES);
            //将token保存到浏览器cookie中,30分钟后失效，失效时间与redis一致
            CookieUtil.create(response, "sso-token", ssoToken, false, 1800);
            //带着sso-token重定向到app页面
            return "redirect:" + redirect + "?sso-token=" + ssoToken;
        } else {
            return "login";
        }
    }

    @RequestMapping("/sso/authenticate")
    @ResponseBody
    public ResponseObject<String> ssoAuthenticate(HttpServletRequest request, String ssoToken) {
    	ResponseObject<String> retObj = new ResponseObject<String>();
        //从redis中检索是否存在验证的sso-token
        if(stringRedisTemplate.hasKey(ssoToken)) {
            retObj.setSuccess(true);
        } else {
        	retObj.setSuccess(false);
        }
        return retObj;
    }
    
    @RequestMapping("/sso/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, String ssoToken) {
    	//验证ssoToken，如果token不存在或已过期则不能注销，必须先登录
        if(stringRedisTemplate.hasKey(ssoToken)) {
        	//清除cookie中的数据
        	CookieUtil.create(response, "sso-token", null, false, 0);
            //删除redis中相关的token
            stringRedisTemplate.delete(ssoToken);
            //让session失效，触发session失效监听器取执行批量登出
            request.getSession().invalidate();
        }
        return "login";
    }
}
