package com.bounter.app1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by admin on 2017/4/16.
 */
@Controller
public class AppController {

	@RequestMapping("/")
    public String home() {
        return "index";
    }
	
    @RequestMapping("/app1")
    public String protectedResource() {
        return "index";
    }
    
    /**
     * 接收SSO服务器发送的批量登出命令,进行简单登出
     * @return
     */
//    @RequestMapping("/app1/batchLogout")
//    public void batchLogout(HttpSession session, String jsessionid) {
//    	//从应用会话容器中获取知道jessionid的会话
//    	HttpSession appSession = AppSessionContainer.getAppSessions().get(jsessionid);
//        if(appSession != null) {
//        	//从session容器中删除该session
//    		AppSessionContainer.getAppSessions().remove(appSession.getId());
//        	//注销该会话
//        	appSession.invalidate();
//        }
//    }
    
}
