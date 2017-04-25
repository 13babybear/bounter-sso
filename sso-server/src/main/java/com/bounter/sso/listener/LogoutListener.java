package com.bounter.sso.listener;

import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.stereotype.Component;

import com.bounter.sso.utility.RestHttpClient;

@WebListener
public class LogoutListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {}

	@Override
	public void sessionDestroyed(HttpSessionEvent se){
		//从session中获取所有待删除的url
		Set<String> urls = (Set<String>) se.getSession().getAttribute("urls");
		//遍历URL集合，进行批量登出
		for(String url : urls) {
			url = url.substring(0,url.indexOf("?")) + "/batchLogout" + url.substring(url.indexOf("?"));
			try {
				//通知所有应用进行注销
				RestHttpClient.sendHttpGetRequest(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
