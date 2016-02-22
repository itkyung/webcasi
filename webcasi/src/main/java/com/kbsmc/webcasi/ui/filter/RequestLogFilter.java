package com.kbsmc.webcasi.ui.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.entity.User;

public class RequestLogFilter implements Filter {
	
	private WebApplicationContext springContext;
	private Log log = LogFactory.getLog(RequestLogFilter.class);
	private ILogin login = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		 springContext = 
			        WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		 login = (ILogin)springContext.getBean("userService");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		long startTime = new Date().getTime();
		boolean success = true;
		HttpServletRequest sr = (HttpServletRequest)request;
		String uri = sr.getRequestURI();
	    String contextPath = sr.getContextPath();
	    String subUri = uri.substring(contextPath.length());
		
		try {
			chain.doFilter(request, response);
		}catch(ServletException e){
			success = false;
			throw e;
		}catch(IOException e){
			success = false;
			throw e;
		}catch(RuntimeException re){
			success = false;
			throw re;
		}
		finally {
			if(subUri.startsWith("/member")){
				//사용자에 대한 request만 남긴다. 
				long endTime = new Date().getTime();
				if(login == null)
					login = (ILogin)springContext.getBean("userService");
				
				User currentUser = login.getCurrentUser();
				if(currentUser != null){
					long duration = endTime - startTime;
					//브라우저 정보.
					String patno = login.findPatientNo(currentUser);
					String agent = sr.getHeader("User-Agent");
					StringBuffer params = new StringBuffer();
					try{
						if(sr.getMethod() != null && sr.getMethod().equalsIgnoreCase("POST")){
							Map map = sr.getParameterMap();
							for(Object key : map.keySet()){
								Object objValue = map.get(key);
								if(objValue != null && objValue instanceof String){
									String value = (String)map.get(key);
									params.append(key + value + "&");
								}else if(objValue != null && objValue.getClass().isArray()){
									Object[] values = (Object[])objValue;
									
									if(values.length > 0 && values[0] instanceof String){
										String value = (String)values[0];
										params.append(key + "=" + value + "&");
									}
								}
							}
							
						}else if(sr.getMethod() != null && sr.getMethod().equalsIgnoreCase("GET")){
							params.append(sr.getQueryString() == null ? "" : sr.getQueryString());
						}
					}catch(Exception e){
						success = false;
					}
					//수진자번호,경과시간,브라우저 정보,호출 url,파라미터들....
					log.info(patno + "," + duration + "ms," + success + "," + agent + "," + subUri + "," + params.toString());
					
				}
			}
		}


	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
