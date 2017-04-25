package com.bounter.sso.client.listener;

import java.util.Map;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.bounter.sso.client.utility.AppSessionContainer;

@WebListener
public class LoginListener implements HttpSessionListener {
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		//在创建session时维护一份所有应用的session列表
		AppSessionContainer.getAppSessions().put(se.getSession().getId(), se.getSession());
		System.out.println("Session with id : " + se.getSession().getId() + " has been created !");
		Map<String, HttpSession> sessions = AppSessionContainer.getAppSessions();
		System.out.println("AppSessionContainer里保存的session：");
		for(String key : sessions.keySet()) {
			System.out.println(key);
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se){
		System.out.println(se.getSession().getId() + " has been invalidate !");
	}

}
