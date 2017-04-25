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

import com.bounter.sso.client.utility.AppSessionContainer;
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
        
        //返回的url
        String redirect = null;
        
        //获取session
        HttpSession session = request.getSession();
        String sessionSSOToken = null;
        if(session != null) {
        	sessionSSOToken = (String) session.getAttribute("sso-token");
        }
        //先判断是否是登出，如果是则执行注销操作
        if(request.getServletPath().endsWith("/logout")){
        	//清除本地session
        	if(session != null) {
        		//从session容器中删除该session
        		AppSessionContainer.getAppSessions().remove(session.getId());
        		session.invalidate();
        	}
        	//重定向到SSO服务器执行登出,登出成功后返回到该应用主页
        	redirect = request.getRequestURL().toString();
        	redirect = redirect.substring(0,redirect.indexOf("/sso/logout"));
        	response.sendRedirect("http://www.sso.com:18080/sso/logout?ssoToken=" + sessionSSOToken + "&redirect=" + redirect);
        	return;
        }
        
        //对所有应用进行批量登出
        if(request.getServletPath().endsWith("/batchLogout")){
//        	filterChain.doFilter(request,response);
        	String jsessionid = request.getParameter("jsessionid");
        	//从应用会话容器中获取知道jessionid的会话
        	HttpSession appSession = AppSessionContainer.getAppSessions().get(jsessionid);
            if(appSession != null) {
            	//从session容器中删除该session
        		AppSessionContainer.getAppSessions().remove(appSession.getId());
            	//注销该会话
            	appSession.invalidate();
            }
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
        redirect = request.getRequestURL() + "?jsessionid=" + session.getId();
        response.sendRedirect("http://www.sso.com:18080/sso/login?redirect=" + redirect);
    }

    @Override
    public void destroy() {

    }
}
