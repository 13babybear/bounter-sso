package com.bounter.sso.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.bounter.sso.utility.RestHttpClient;

@WebListener
public class LogoutListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		//遍历URL集合，进行批量登出
		for(String url : urls) {
			url += "/sso/batchLogout";
			RestHttpClient.sendHttpGetRequest(url);
		}
	}

}
