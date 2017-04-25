package com.bounter.sso.client.utility;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public class AppSessionContainer {
	private static volatile ConcurrentHashMap<String, HttpSession> appSessions = null;
	
	public static ConcurrentHashMap<String, HttpSession> getAppSessions() {
		if(appSessions == null) {
            synchronized(AppSessionContainer.class) {
                 if(appSessions == null) {
                	 appSessions = new ConcurrentHashMap<>();
                  }
             }
        }
        return appSessions;
	}
}
