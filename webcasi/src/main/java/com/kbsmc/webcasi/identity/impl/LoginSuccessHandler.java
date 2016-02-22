package com.kbsmc.webcasi.identity.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.InstanceWrapper;
import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.Role;
import com.kbsmc.webcasi.identity.entity.User;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	private Log log = LogFactory.getLog(LoginSuccessHandler.class);
	
	
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) throws IOException,
			ServletException {
		//  로그인이 성공하면 해당 사용자의 추자정보를 session에 남길수 있다.
		// 또한 로그인이 성공하면 특정 URL이나 또는 session에 저장된 redirect url로 보낼수 있다.
		// 로그인에 성공한 사용자의 권한에 따라서 일반 메인으로 보낼지 Admin메인 또는 간호사 메인으로 보낼지 결정한다.
		
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		ILogin login = (ILogin)applicationContext.getBean("userService");
		User currentUser = login.getCurrentUser();
		
//		try{
//			if(login.isInRole(currentUser, Role.USER_ROLE)){
//				login.syncRecentReserveInfo(currentUser);
//			}
//		}catch(Exception e){
//			log.error("Sync reserve info : ",e);
//			response.sendRedirect(request.getContextPath() + "/error/serverProcessingError");
//			return;
//		}
		login.updateLastLoginDate(currentUser);
		
		if(login.isInRole(currentUser, Role.USER_ROLE)){
			ICheckupInstanceService ciService = (ICheckupInstanceService)applicationContext.getBean("CheckupInstanceService");
			
			InstanceWrapper wrapper = ciService.syncReserveNo(currentUser);
			if(wrapper.isNeedHistoryInit()){
				//과거 문진이력을 초기화할 필요가 있을경우에만 한다.
				ciService.initHistoryCode(currentUser);
				if(wrapper.getInstance() != null){
					try{
						//과거 문진이력을 초기화하지 않는다..
						
					//	ciService.initOcsHistoryCode(wrapper.getInstance());
					}catch(Exception e){
						log.error("", e);
					}
				}
				
			}
		}
		
		String targetUrl = null;
		if(login.isInRole(currentUser, Role.USER_ROLE)){
			targetUrl = "/member/home";
		}else if(login.isInRole(currentUser, Role.NURSE_ROLE)){
			targetUrl = "/nurse/home";
		}else if(login.isInRole(currentUser, Role.ADMIN_ROLE)){
			targetUrl = "/admin/goMasterList";
		}
		
		response.sendRedirect(request.getContextPath() + "/loading?nextPage=" + targetUrl);
	}

	
	
}
