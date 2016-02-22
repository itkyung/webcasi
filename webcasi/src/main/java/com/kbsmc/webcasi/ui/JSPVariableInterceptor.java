package com.kbsmc.webcasi.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ICheckupMasterDAO;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.entity.User;

/**
 * JSP 파일에서 공통적으로 필요한 Variable을 만들어서 리턴한다.
 * 
 * @author bizwave
 *
 */
public class JSPVariableInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired private ILogin login;
	@Autowired private ICheckupMasterService masterService;
	@Autowired private ICheckupInstanceService instanceService;
	
	private DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			ModelMap model = modelAndView.getModelMap();
			String uri = request.getRequestURI();
		    String contextPath = request.getContextPath();
		    String subUri = uri.substring(contextPath.length());
		    model.addAttribute("_isMobile",CommonUtils.isGalaxyNote(request));
		    
		    
		    if(subUri.startsWith("/member")){
		    	User currentUser = login.getCurrentUser();
		    	model.addAttribute("_currentUser", currentUser);
		    	CheckupInstance instance = instanceService.findProgressInstance(currentUser);
		    	
		    	
		    	//질문의 경로를 찾는다.
		    	if(subUri.startsWith("/member/checkup")){
		    		boolean needCategoryPath = false;
		    		String path = null;
		    		
		    		if(instance != null){
		    			String currentCategoryType = null;
		    			String currentCategoryId = null;
		    			String currentQuestionGroupId = null;
		    			int lastSortOrder = -1;
		    			int currentSortOrder = -1;
		    			if(subUri.startsWith("/member/checkup/category/intro2")){
		    				String categoryType = subUri.substring(32);
			    			 Category category = masterService.findUniqueCategory(instance.getMaster(), CategoryType.valueOf(categoryType));
			    			 path = category.getTitle();
			    			 needCategoryPath = true;
			    			 currentCategoryType = category.getType().name();
			    			 currentSortOrder = category.getSortOrder();
		    			}else if(subUri.startsWith("/member/checkup/category")){
			    			 String categoryType = subUri.substring(25);
			    			 Category category = masterService.findUniqueCategory(instance.getMaster(), CategoryType.valueOf(categoryType));
			    			 path = category.getTitle();
			    			 needCategoryPath = true;
			    			 currentCategoryType = category.getType().name();
			    			 currentSortOrder = category.getSortOrder();
			    		}else if(subUri.startsWith("/member/checkup/questionGroup")){
			    			String questionGroupId = subUri.substring(30);
			    			QuestionGroup group = masterService.loadQuestionGroup(questionGroupId);
				    		path = group.getCategory().getTitle() +" > " + group.getTitle();
			    			needCategoryPath = true;
			    			currentCategoryType = group.getCategory().getType().name();
			    			currentQuestionGroupId = questionGroupId;
			    			currentCategoryId = group.getCategory().getId();
			    			currentSortOrder = group.getCategory().getSortOrder();
			    		}
			    		
		    			if(subUri.startsWith("/member/checkup/static/guide")){
		    				model.addAttribute("_guidePage",true);
		    				model.addAttribute("_path","건진안내문");
		    			}else{
		    				model.addAttribute("_currentCategoryType",currentCategoryType);
		    			}
		    			
			    		
			    		model.addAttribute("_currentCategoryId",currentCategoryId);
			    		model.addAttribute("_currentQuestionGroupId",currentQuestionGroupId);
			    		model.addAttribute("_currentSortOrder",currentSortOrder);
			    		
			    		model.addAttribute("_hopeDate",fm.format(instance.getReserveDate()));
			    		
			    		if(needCategoryPath){
			    			model.addAttribute("_path",path);
			    		}
			    		model.addAttribute("_progress",instance.getProgress());
			    		String lastQuestionId = instance.getLastQuestionId();
			    		String lastCategoryId = null;
			    		int lastQuestionGroupSortOrder = -1;
			    		if(lastQuestionId != null){
			    			Question lastQuestion = masterService.loadQuestion(lastQuestionId);
			    			QuestionGroup lastGroup = lastQuestion.getParentGroup();
			    			Category lastCategory = lastGroup.getCategory();
			    			lastSortOrder = lastCategory.getSortOrder();
			    			lastCategoryId = lastCategory.getId();
			    			lastQuestionGroupSortOrder = lastGroup.getSortOrder();
			    		}
			    		model.addAttribute("_lastCategoryId",lastCategoryId);
			    		model.addAttribute("_lastSortOrder",lastSortOrder);
			    		model.addAttribute("_lastQuestionGroupSortOrder",lastQuestionGroupSortOrder);
			    		
			    		model.addAttribute("_samsungEmployee",instance.isSamsungEmployee());
			    		model.addAttribute("_needStressTest",instance.isNeedStressTest());
			    		model.addAttribute("_needSleepTest",instance.isNeedSleepTest());
		    		}else{
		    			model.addAttribute("_samsungEmployee",currentUser.isSamsungEmployee());
			    		model.addAttribute("_needStressTest",currentUser.isNeedStressTest());
			    		model.addAttribute("_needSleepTest",currentUser.isNeedSleepTest());
		    		}
			    	
			    }else{
			    	int lastSortOrder = -1;
	    			int currentSortOrder = -1;
	    			int lastQuestionGroupSortOrder = -1;
			    	if(instance != null){
			    		model.addAttribute("_hopeDate",fm.format(instance.getReserveDate()));
			    		model.addAttribute("_samsungEmployee",instance.isSamsungEmployee());
			    		model.addAttribute("_needStressTest",instance.isNeedStressTest());
			    		model.addAttribute("_needSleepTest",instance.isNeedSleepTest());
			    		
			    		String lastQuestionId = instance.getLastQuestionId();
			    		String lastCategoryId = null;
			    		
			    		if(lastQuestionId != null){
			    			Question lastQuestion = masterService.loadQuestion(lastQuestionId);
			    			QuestionGroup lastGroup = lastQuestion.getParentGroup();
			    			Category lastCategory = lastGroup.getCategory();
			    			lastSortOrder = lastCategory.getSortOrder();
			    			lastCategoryId = lastCategory.getId();
			    			lastQuestionGroupSortOrder = lastGroup.getSortOrder();
			    		}
			    		
			    	}else{
			    		model.addAttribute("_samsungEmployee",currentUser.isSamsungEmployee());
			    		model.addAttribute("_needStressTest",currentUser.isNeedStressTest());
			    		model.addAttribute("_needSleepTest",currentUser.isNeedSleepTest());
			    	}
			    	model.addAttribute("_currentSortOrder",currentSortOrder);
			    	model.addAttribute("_lastSortOrder",lastSortOrder);
			    	model.addAttribute("_lastQuestionGroupSortOrder",lastQuestionGroupSortOrder);
			    	model.addAttribute("_path","Main");
			    	
			    }
		    }
		      
		}
		
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		return super.preHandle(request, response, handler);
	}
	
	
	
}
