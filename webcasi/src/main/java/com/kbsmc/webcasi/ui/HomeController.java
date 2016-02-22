package com.kbsmc.webcasi.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.GuideBag;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.User;

/**
 * Handles requests for the application home page.
 * 로그인 이전의 메인페이지와 인트로 페이지를 처리함.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	@Autowired private ICheckupMasterService masterService;
	@Autowired private ICheckupInstanceService service;
	@Autowired private IUserService userService;
	
	private List<GuideBag> guides;
	
	/**
	 * 이 페이지는 Intro페이지 이다. 
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String intro(
			@RequestParam(value="loginFailure",required=false) Boolean loginFailure,
			Locale locale, HttpServletRequest request, Model model) {
		
		
		if(loginFailure != null){
			model.addAttribute("loginFailure",true);
		}
		return "intro";
	}
	
	@RequestMapping(value = "/loading")
	public String loading(@RequestParam("nextPage") String nextPage, HttpServletRequest request,Model model){
		
		model.addAttribute("nextPage",nextPage);
		
		return "loginSuccessLoading";
	}
	
	@RequestMapping(value = "/an/home", method = RequestMethod.GET)
	public String anonymousHome(Model model){
		
		
		
		
		return "anonymous/home";
	}
	
	@RequestMapping(value="/vContent")
	public String nurseWebViewerGate(
			@RequestParam("oPatno") String patno,
			@RequestParam("oHopedate") String hopeDate,
			@RequestParam("oBuscd") String buscd,
			ModelMap model){
		
		User currentUser = userService.findPatient(patno);
		CheckupInstance instance = service.findInstanceByReserveDateStr(currentUser, hopeDate);
		if(instance == null){
			//만약에 instance가 없는경우에는 ocs예약정보가 변경되엇을 확률이 잇기 때문에 Ocs 예약정보를 동기화를 한후에 다시 찾는다.
			service.syncReserveNo(currentUser);
			instance = service.findInstanceByReserveDateStr(currentUser, hopeDate);
		}
		
		if(instance == null){
			model.addAttribute("msg","수진번호와 예약날짜에 해당하는 문진 데이타가 존재하지 않습니다.");
			return "/error/nurseError";
		
		}
		String url = "/nurse/view/" + patno + "?hopeDate=" + hopeDate + "&instanceId=" + instance.getId();
		
		if(buscd.equals("1")){
			//체크리스트.
			
		}else if(buscd.equals("2")){
			//치과설문.
			QuestionGroup group = masterService.findQuestionGroup(instance.getMaster(), QuestionGroupType.NORMAL_DENTAL);
			url += "&questionGroupId=" + group.getId();
			
		}else if(buscd.equals("3")){
			//부인과 설문.
			if(currentUser.getGender().equals(Gender.MALE)){
				model.addAttribute("msg","부인과 설문은 여성만 진행할수 있습니다.");
				return "/error/nurseError";
			}
			QuestionGroup group = masterService.findQuestionGroup(instance.getMaster(), QuestionGroupType.NORMAL_WOMAN);
			url += "&questionGroupId=" + group.getId();
		}
		
		return "redirect:" + url;
	}
	

	@RequestMapping(value="/guide/viewGuide")
	public String viewGuide(ModelMap model){
		
		return "redirect:/guide/1/00"; 
	}
	
	@RequestMapping(value="/guide/{imageName1}/{imageName2}")
	public String guide(@PathVariable("imageName1") String imageName1,
			@PathVariable("imageName2") String imageName2,
			ModelMap model){
		String imageName = "page" + imageName1 + "_" + imageName2 + ".gif";
		
		model.addAttribute("guideImage", imageName);
		
		if(this.guides == null){
			makeGuideSteps();
		}
		
		model.addAttribute("guideSteps",this.guides);
		
		
		int currentStep = Integer.parseInt(imageName2);
		int currentPath = Integer.parseInt(imageName1);
		
		GuideBag currentGuide = findGuide(imageName1);
		
		if(currentStep > 0){
			model.addAttribute("viewNavigation",true);
			
			
			
			if(currentGuide.getTotalStepCount() - 1 == currentStep){
				//해당 path의 마지막.
				if(currentGuide.getPath() < 8){
					GuideBag nextGuide = findNextGuide(currentGuide);
					
					String nextPath = String.format("%d",nextGuide.getPath());
					model.addAttribute("nextUrl","/guide/" +  nextPath + "/00");
				}else{
					//맨 마지막인경우.
					
				}
			}else if(currentGuide.getTotalStepCount() - 1 > currentStep){
				String nextStep = String.format("%02d",currentStep + 1);
				model.addAttribute("nextUrl","/guide/" +  imageName1 + "/" + nextStep);
				
			}
			
			String preUrl = "/guide/" + imageName1;
			String preStep = String.format("%02d",currentStep - 1);
			model.addAttribute("preUrl",preUrl + "/" + preStep);
			
		}else{
			model.addAttribute("nextUrl","/guide/" +  imageName1 + "/01");
		
		}
		
		model.addAttribute("_currentPath",currentGuide.getPath());
		
		
		
		
		return "/member/checkup/static/guide/guideTemplate";
	}
	
	private GuideBag findGuide(String path){
		for(GuideBag bag : this.guides){
			if(bag.getPath()  == Integer.parseInt(path)){
				return bag;
			}
		}
		return null;
	}
	
	private GuideBag findNextGuide(GuideBag cur){
		for(int i=0 ; i < this.guides.size(); i++){
			GuideBag bag = this.guides.get(i);
			if(bag.equals(cur)){
				if(i < this.guides.size()-1){
					return this.guides.get(i+1);
				}
			}
		}
		return null;
	}
	
	private void makeGuideSteps(){
		this.guides = new ArrayList<GuideBag>();
		
		//String[] titles = {"검사 전 유의사항-오전안내","검사 전 유의사항-오후안내","대장내시경-오전 안내","대장내시경-오후 안내","PET-CT 안내","객담 세포병리 검사 안내","종합건진 안내","EMAIL 결과발송 안내"};
		String[] titles = {"검사 전 유의사항-오전안내","검사 전 유의사항-오후안내",null,null,null,null,"종합건진 안내","EMAIL 결과발송 안내"};
		
		for(int i = 0; i <= 7; i++){
			GuideBag item = new GuideBag();
			item.setTitle(titles[i]);
			item.setPath(i+1);
			item.setStep(0);
			item.setUrl("/guide/" + (i+1) + "/00");
			
			switch(i){
			case 0:
				item.setTotalStepCount(6);
				break;
			case 1:
				item.setTotalStepCount(6);
				break;				
			case 2:
				item.setTotalStepCount(3);
				break;
			case 3:
				item.setTotalStepCount(3);
				break;
			case 4:
				item.setTotalStepCount(3);
				break;
			case 5:
				item.setTotalStepCount(2);
				break;
			case 6:
				item.setTotalStepCount(11);
				break;
			case 7:
				item.setTotalStepCount(2);
				break;

			}
			
			if(item.getTitle() == null) continue;
			
			this.guides.add(item);
		}
	}
}
