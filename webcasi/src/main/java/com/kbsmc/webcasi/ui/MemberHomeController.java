package com.kbsmc.webcasi.ui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.WebPath;
import com.kbsmc.webcasi.checkup.AskType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ZipCode;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.entity.User;

/**
 * 로그인이후의 메인페이지등을 처리함.
 * @author bizwave
 *
 */
@Controller
@RequestMapping(value=WebPath.MEMBER_PATH)
public class MemberHomeController {
	private static final Logger logger = LoggerFactory.getLogger(MemberHomeController.class);
			
	@Autowired private ILogin login;
	@Autowired private ICheckupInstanceService service;
	
	private Log log = LogFactory.getLog(MemberHomeController.class);
	
	private DateFormat fm = new SimpleDateFormat("yyyy");
	
	@RequestMapping(value="home",method=RequestMethod.GET)
	public String home(ModelMap map){
		User currentUser = login.getCurrentUser();
	
		map.put("currentUser", currentUser);
		map.put("currentYear", fm.format(new Date()));
		boolean canStart = true;
		InstanceStatus cStatus = InstanceStatus.COMPLETED;
		boolean needRequest = false;
		
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		if(instance == null){
			cStatus = InstanceStatus.CLOSED;
		}else{
			switch(instance.getStatus()){
			case CLOSED:
				cStatus = InstanceStatus.CLOSED;
				break;
			case READY:
				cStatus = InstanceStatus.READY;
				//예약일 2주전이 아니면 예약을 시작하지 못하게 한다.
				if(!CommonUtils.isInFromToday(instance.getReserveDate(), 14)){
					canStart = false;
				}
				if(service.findResultRequest(instance) == null){
					needRequest = true;
				}
				break;
			case IN_PROGRESS:
				if(service.findResultRequest(instance) == null){
					needRequest = true;
				}
				//임시 
				//needProtection = false;
				cStatus = InstanceStatus.IN_PROGRESS;
				QuestionGroup lastGroup = service.findLastQuestionGroup(instance);
				String url =  "/member/checkup/questionGroup/" + lastGroup.getId() + "?showMsg=true&needRequest=" + needRequest;
				
				//최종 문진을 진행했던 페이지로 이동한다.
				return "redirect:" + url;
			case FIRST_COMPLETED:
				if(service.findResultRequest(instance) == null){
					needRequest = true;
				}
				cStatus = InstanceStatus.FIRST_COMPLETED;
				break;
			default :
				cStatus = InstanceStatus.COMPLETED;
				break;
			}
		}
		
		//임시 needProtection
		//needProtection = false;
		
		map.put("currentStatus", cStatus.name());
		map.put("canStart",canStart);
		map.put("needRequest",needRequest);
		
		return "member/home";
	}
	
	@RequestMapping(value="goHome",method=RequestMethod.GET)
	public String goHome(ModelMap map){
		User currentUser = login.getCurrentUser();
	
		map.put("currentUser", currentUser);
		
		InstanceStatus cStatus = InstanceStatus.COMPLETED;
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		if(instance == null){
			cStatus = InstanceStatus.CLOSED;
		}else{
			cStatus = instance.getStatus();
		}
		
		map.put("currentStatus", cStatus.name());
		
		return "member/home";
	}
	
	@RequestMapping(value="categoryGate",method=RequestMethod.GET)
	public String categoryGate(@RequestParam("categoryType") String categoryType,ModelMap map){
		User currentUser = login.getCurrentUser();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		if(instance == null
				|| instance.getStatus().equals(InstanceStatus.CLOSED)
				|| instance.getStatus().equals(InstanceStatus.FIRST_COMPLETED)
				|| instance.getStatus().equals(InstanceStatus.NURSE_CHECK)
				|| instance.getStatus().equals(InstanceStatus.COMPLETED)){
			
			map.put("currentStatus",instance.getStatus());
			return "/member/statusError";
		}
		
		//TODO 정상적인 화면으로 Redirect한다. 
		
		
		
		
		return "";
	}
	
	@RequestMapping(value="/resultRequestForm",method=RequestMethod.GET)
	public String resultRequestForm(ModelMap map){
		try{
		User currentUser = login.getCurrentUser();
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		ResultRequest request = service.loadOrCreateRequest(instance, null);
		
		map.addAttribute("resultRequest",request);
		
		String[] emails = {"naver.com","daum.net","hanmail.net","hotmail.com","nate.com","yahoo.com","dreamwiz.com","korea.com","gmail.com","samsung.com"};
		
		List<String> emailArray = new ArrayList<String>();
		emailArray.addAll(Arrays.asList(emails));
		
		String email = request.getEmail();
		if(email != null){
			String[] split = email.split("@");
			String prefix = split[0];
			String postfix = split[1];
			if(!emailArray.contains(postfix)){
				map.addAttribute("useDirectEmail",true);
			}else{
				map.addAttribute("useDirectEmail",false);
			}
			map.addAttribute("emailPrefix",prefix);
			map.addAttribute("emailPostfix",postfix);
		}else{
			map.addAttribute("useDirectEmail",true);
		}
		emailArray.add("직접입력");
		map.addAttribute("emailDatas",emailArray);
		}catch(Exception e){
			log.error("Error :",e);
			
		}
		return "/member/resultRequestForm";
	}
	
	@RequestMapping(value="/saveResultRequest",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String saveResultRequest(@ModelAttribute("protectionModel") ProtectionModel model,
			BindingResult result) throws IOException{
		if("".equals(model.getId())){
			model.setId(null);
		}
		CheckupInstance instance = service.loadInstance(model.getInstanceId());
		ResultRequest request = service.loadOrCreateRequest(instance, model.getId());
		try{
			request.setCellPhone(model.getCellPhone());
			request.setCompanyPhone(model.getCompanyPhone());
			request.setZipCode(model.getZipCode());
			request.setZpAddress(model.getZpAddress());
			request.setAddress(model.getAddress());
			request.setAskType(AskType.valueOf(model.getAskType()));
			request.setEmailAgree(model.getEmailAgree());
			
			
			String email;
			email = model.getPrefix() + "@";
			if(model.getPostfix().equals("직접입력")){
				email = email + model.getEmailPostfixInput();
			}else{
				email = email + model.getPostfix();
			}
			request.setEmail(email);
			service.saveResultRequest(request);
			
			model.setId(request.getId());
			model.setSuccess(true);
		}catch(Exception e){
			model.setSuccess(false);
			log.error("error : ",e);
		}
		
		return CommonUtils.toJson(model);
	}
	
	
	@RequestMapping(value="/protectionForm",method=RequestMethod.GET)
	public String protectionForm(ModelMap map){
		User currentUser = login.getCurrentUser();
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		ParentProtection protection = service.loadOrCreateProtection(instance, null);
		
		map.addAttribute("protection",protection);
		
		return "/member/protectionForm";
	}
	
	@RequestMapping(value="/zipCodeForm")
	public String zipCodeForm(ModelMap map){
		
		
		
		return "/member/zipCodeForm";
	}
	
	@RequestMapping(value="/searchZipCode",produces = "application/json;charset=utf-8")
	@ResponseBody
	public String searchZipcode(@RequestParam("dong") String dong){
		
		List<ZipCode> results = service.searchZipCode(dong);
		
		return CommonUtils.toJson(results);
	}

	@RequestMapping(value="/saveProtection",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String saveProtection(@ModelAttribute("protectionModel") ProtectionModel model,
			BindingResult result) throws IOException{
		if("".equals(model.getId())){
			model.setId(null);
		}
		CheckupInstance instance = service.loadInstance(model.getInstanceId());
		ParentProtection protection = service.loadOrCreateProtection(instance, model.getId());
		try{
			
			protection.setSmsAgree(model.getSmsAgree());
			protection.setParentPhone(model.getParentPhone());
			service.saveProtection(protection);
			
			model.setId(protection.getId());
			model.setSuccess(true);
		}catch(Exception e){
			model.setSuccess(false);
			log.error("error : ",e);
		}
		
		return CommonUtils.toJson(model);
	}
	
}
