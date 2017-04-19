package com.bounter.sso.client.filter;

import com.alibaba.fastjson.JSONObject;
import com.bounter.sso.client.utility.RestTemplateUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by admin on 2017/4/16.
 */
public class SSOFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取session,判断是否已经本地登录
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("uid");
        if (userName != null) {
            filterChain.doFilter(request,response);
            return;
        }

        //如果请求中存在Token，则去SSO Server校验Token
        String ssoToken = request.getParameter("sso-token");
        if(ssoToken != null) {
            String jSessionId = request.getParameter("jSessionId");
            String ssoUrl = "http://www.sso.com:18080/sso/authenticate?sso-token=" + ssoToken;
            //验证时带上服务器的JSESSIONID
            String resultStr =  RestTemplateUtil.get(request,ssoUrl,null);
            JSONObject retObj = JSONObject.parseObject(resultStr);
            //sso-token验证成功
            if("success".equals(retObj.getString("ret"))) {
                //将用户信息保存到本地session中
                session.setAttribute("uid",retObj.getString("uid"));
                filterChain.doFilter(request,response);
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
