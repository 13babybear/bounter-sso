package com.bounter.sso.client.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bounter.sso.client.utility.ResponseObject;
import com.bounter.sso.client.utility.RestHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by admin on 2017/4/16.
 * SSO登录登出过滤器
 */
public class SSOFilter implements Filter {
	private ObjectMapper mapper;
    
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
		mapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取session
        HttpSession session = request.getSession();
        String sessionSSOToken = null;
        if(session != null) {
        	sessionSSOToken = (String) session.getAttribute("sso-token");
        }
        //先判断是否是登出，如果是则执行注销操作
        //TODO:可以放到应用自己的控制器中登出
        if(request.getServletPath().endsWith("/sso/logout")){
        	//清除本地session
        	if(session != null) {
        		session.invalidate();
        	}
        	//重定向到SSO服务器执行登出
        	response.sendRedirect("http://www.sso.com:18080/sso/logout?ssoToken=" + sessionSSOToken);
        	return;
        }
        
        //根据session判断是否已经本地登录
        if (sessionSSOToken != null) {
        	//sso-token有值，已经本地登录过
            filterChain.doFilter(request,response);
            return;
        }

        //如果请求中存在Token，则去SSO Server校验Token
        String ssoToken = request.getParameter("sso-token");
        if(ssoToken != null) {
        	//rest 请求地址
            String ssoUrl = "http://www.sso.com:18080/sso/authenticate?ssoToken=" + ssoToken;
            String resultStr = "";
			try {
				//Http Get请求,访问SSO服务器进行token验证
				resultStr = RestHttpClient.sendHttpGetRequest(ssoUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
            ResponseObject<String> retObj = mapper.readValue(resultStr, new TypeReference<ResponseObject<String>>() {});
            //sso-token验证成功
            if(retObj.isSuccess()) {
                //在本地session中设置登录成功
                session.setAttribute("sso-token",ssoToken);
                //重定向回当前应用，防止在浏览器上显示ssoToken
                response.sendRedirect(request.getRequestURL().toString());
                return;
            }
        }

        //跳转到sso server进行登录
        response.sendRedirect("http://www.sso.com:18080/sso/login?redirect=" + request.getRequestURL());
    }

    @Override
    public void destroy() {

    }
}
