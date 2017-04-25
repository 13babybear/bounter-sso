package com.bounter.sso.controller;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bounter.sso.service.SSOService;
import com.bounter.sso.utility.CookieUtil;
import com.bounter.sso.utility.ResponseObject;
import com.bounter.sso.utility.StringUtil;

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

    //统一的会话失效时间，单位'分钟'
    private static final int EXPIRE_TIME = 30;
    
    @RequestMapping("/")
    public String home(HttpServletRequest request) {
        return "login";
    }
    
    /**
     * 登录
     * @param request
     * @param response
     * @param username
     * @param password
     * @param redirect
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/sso/login", method={RequestMethod.GET, RequestMethod.POST})
    public String ssoLogin(HttpServletRequest request, HttpServletResponse response, String username, String password, String redirect) throws IOException {
    	//同一用户登录不同应用，先判断Cookie中是否有sso-token,如果有sso-token跳过登录
    	String ssoToken = CookieUtil.getValue(request, "sso-token");
    	if(ssoToken != null) {
    		//把新的应用地址保存到key为sso-token的redis集合中
            setOps.add(ssoToken,redirect);
            //刷新key为sso-token的失效时间
            stringRedisTemplate.expire(ssoToken,EXPIRE_TIME,TimeUnit.MINUTES);
    		return "redirect:" + redirect + "&sso-token=" + ssoToken;
    	}
    	
    	//默认的跳转页面
    	String redirectUrl = "login";
    	if(!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
    		//登录认证
            boolean loginSuccess = ssoService.loginCheck(username,password);
            
            //登录认证成功
            if (loginSuccess) {
                //创建sso-token
                ssoToken = UUID.randomUUID().toString();
                //将token保存到session中
                request.getSession().setAttribute("sso-token", ssoToken);
                //把app地址保存到key为sso-token的redis集合中
                setOps.add(ssoToken,redirect);
                //设置过期时间为30分钟
                stringRedisTemplate.expire(ssoToken,EXPIRE_TIME,TimeUnit.MINUTES);
                //将token保存到浏览器cookie中,30分钟后失效，失效时间与redis一致
                CookieUtil.create(response, "sso-token", ssoToken, false, EXPIRE_TIME * 60);
                //带着sso-token重定向到app页面
                redirectUrl =  "redirect:" + redirect + "&sso-token=" + ssoToken;
            }
    	}
    	
    	return redirectUrl;
    	
    }

    /**
     * token验证
     * @param request
     * @param ssoToken
     * @return
     */
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
    
    /**
     * 注销
     * @param request
     * @param response
     * @param ssoToken
     * @return
     */
    @RequestMapping("/sso/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, String ssoToken, String redirect) {
    	//验证ssoToken，如果token不存在或已过期则不能注销，必须先登录
        if(stringRedisTemplate.hasKey(ssoToken)) {
        	//清除cookie中的数据
        	CookieUtil.create(response, "sso-token", null, false, 0);
        	//根据sso-token从redis中查询出当前token对应的所有的应用url
        	Set<String> urls = setOps.members(ssoToken);
        	//将所有待删除的url放入session中，准备在监听器进行批量会话删除
        	request.getSession().setAttribute("urls", urls);
            //删除redis中相关的token
            stringRedisTemplate.delete(ssoToken);
            //让session失效，触发session失效监听器取执行批量登出
            request.getSession().invalidate();
        }
        return "redirect:" + redirect;
    }
}
