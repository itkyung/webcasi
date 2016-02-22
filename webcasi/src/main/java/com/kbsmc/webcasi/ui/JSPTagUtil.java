package com.kbsmc.webcasi.ui;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.entity.User;

public class JSPTagUtil {
	private static ApplicationContext applicationContext;
	private static ILogin login;
	
	public static void init(ServletContext context){
		if(applicationContext == null){
			applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
			login = (ILogin)applicationContext.getBean("userService");
		}
	}
	
	public static User getCurrentUser(){
		return login.getCurrentUser();
	}
	
	public static String getCurrentUserName(){
		User user = login.getCurrentUser();
		if(user == null){
			return "익명";
		}else{
			return user.getName();
		}
	}
	
	
    public static String getSubstring(String source,int length){
    	if(source != null){
    		if(source.length() > length){
    			return source.substring(0, length-1) + "..";
    		}else{
    			return source;
    		}
    	}
    	return null;
    }
    
    public static boolean isAm(String time){
    	if(time == null || "".equals(time)) return true;
    	int timeInt = Integer.parseInt(time);
    	if(timeInt >= 0 && timeInt <= 11){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 24시 기준을 오전,오후 값으로 바꾼다.
     * 00 ~ 23까지만 존재한다.
     * @param hour
     * @return
     */
    public static String parseToAmPm(String hour){
    	if(hour == null || "".endsWith(hour)) return "";
    	int timeInt = Integer.parseInt(hour);
    	if(isAm(hour)){
    		return hour;
    	}else{
    		if(timeInt == 12){
    			return "12";
    		}
    		if(timeInt > 23){
    			return "23";	//이 경우는 존재하면 안된다.
    		}
    		return "" + (timeInt - 12);
    	}
    	
    }
}
