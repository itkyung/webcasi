
package com.kbsmc.webcasi.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.GuideBag;
import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.WebPath;
import com.kbsmc.webcasi.checkup.AgreeType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.checkup.IOCSInterfaceService;
import com.kbsmc.webcasi.checkup.IResultUpdateService;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.UserCheckObj;
import com.kbsmc.webcasi.identity.entity.EmbeddedPatientNo;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.identity.impl.UserService;

@Controller
@RequestMapping(value=WebPath.MEMBER_PATH + "/checkup")
public class MemberCheckupController {

	private Log log = LogFactory.getLog(MemberCheckupController.class);
	
	@Autowired private ILogin login;
	@Autowired private ICheckupInstanceService service;
	@Autowired private ICheckupMasterService masterService;
	@Autowired private IOCSInterfaceService ocsInterface;
	@Autowired private IResultUpdateService updateService;
	@Autowired private IUserService userService;
	
	private List<GuideBag> guides;
	
	/**
	 * Category Type을 선택하면 해당 카테고리 타입의 첫번째 question group을 얻어서 redirect한다. 
	 * @param categoryType
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/category/{categoryType}")
	public String category(@PathVariable String categoryType,
			@RequestParam(value="needRequest",required=false) Boolean needRequest,
			ModelMap map) throws IOException{
		User currentUser = login.getCurrentUser();
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		try{
			Category category = masterService.findUniqueCategory(instance.getMaster(), CategoryType.valueOf(categoryType));
			
			List<QuestionGroup> groups = masterService.findQuestionGroup(category, currentUser.getGender());
			QuestionGroup firstGroup = groups.get(0);
			
			String firstQuestionGroupUrl = "/member/checkup/questionGroup/" + firstGroup.getId();  //firstGroup.getGroupType().getTemplate() + "/" + firstGroup.getId();
			map.addAttribute("firstUrl", firstQuestionGroupUrl);
			map.addAttribute("needRequest",needRequest == null ? false : needRequest);
			
			if(categoryType.equals(CategoryType.NUTRITION.name())){
				map.addAttribute("nutritionUpdated",instance.getNutritionFlagUpdated());
				map.addAttribute("skipNutrition",instance.isSkipNutrition());
				String nextUrl = null;
				if(instance.isSkipNutrition()){
					Category currentCategory = masterService.findUniqueCategory(instance.getMaster(),CategoryType.NUTRITION);
					Category nextCategory = masterService.findNextCategory(currentCategory,instance.isSamsungEmployee(),true,instance.isNeedSleepTest(),instance.isNeedStressTest());
					
					if(nextCategory == null){
						 nextUrl = WebPath.MEMBER_PATH + "/checkup/static/thankyou";
					}else{
						List<QuestionGroup> groups1 = masterService.findQuestionGroup(nextCategory, currentUser.getGender());
						if(groups.size() > 0){
							QuestionGroup firstGroup1 = groups1.get(0);
							 nextUrl  = WebPath.MEMBER_PATH +  "/checkup/questionGroup/" + firstGroup1.getId();
						}
					}
				}else{
					 nextUrl = firstQuestionGroupUrl;
				}
				map.addAttribute("nextPageUrl",nextUrl);
			}
			return "/member/checkup/intro/" + category.getType().name();
			
		}catch(Exception e){
			log.error("Get Category error :",e);
			return "/checkup/error";
		}
	}
	
	@RequestMapping(value="/category/intro2/{categoryType}")
	public String categoryIntro2(@PathVariable String categoryType,ModelMap map) throws IOException{
		User currentUser = login.getCurrentUser();
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		try{
			Category category = masterService.findUniqueCategory(instance.getMaster(), CategoryType.valueOf(categoryType));
			
			List<QuestionGroup> groups = masterService.findQuestionGroup(category, currentUser.getGender());
			QuestionGroup firstGroup = groups.get(0);
			
			String firstQuestionGroupUrl = "/member/checkup/questionGroup/" + firstGroup.getId();  //firstGroup.getGroupType().getTemplate() + "/" + firstGroup.getId();
			map.addAttribute("firstUrl", firstQuestionGroupUrl);
			
			map.addAttribute("skipNutrition",instance.isSkipNutrition());
			
			return "/member/checkup/intro/" + category.getType().name() + "_2";
			
		}catch(Exception e){
			log.error("Get Category error :",e);
			return "/checkup/error";
		}
	}
	
	@RequestMapping(value="/syncQuestionGroup/{questionGroupId}",produces = "application/json;charset=utf-8")
	@ResponseBody
	public String syncQuestionGroup(
			@PathVariable("questionGroupId") String questionGroupId) throws IOException{
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		User currentUser = login.getCurrentUser();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		QuestionGroup group = masterService.loadQuestionGroup(questionGroupId);
		
		String patno = userService.findPatientNo(currentUser);
		log.info("------ Call Syncquestion Group ---[" + currentUser.getName() + "][" + patno + "]");
		
		try{
			ocsInterface.syncOcsWebDb(instance, currentUser, group);
			submitModel.setSuccess(true);
		}catch(Exception e){
			log.error("Exception :",e);
			submitModel.setSuccess(false);
			submitModel.setMsg(e.getMessage());
		}
		
		return CommonUtils.toJson(submitModel);
		
	}
	
	/**
	 * 해당 QuestionGroup의 페이지를 보여준다.  
	 * @param categoryType
	 * @param questionGroupId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/questionGroup/{questionGroupId}")
	public String questionGroup(
			@PathVariable("questionGroupId") String questionGroupId,
			@RequestParam(value="showMsg",required=false) Boolean showMsg,
			@RequestParam(value="needRequest",required=false) Boolean needRequest,
			ModelMap map) throws IOException{
		User currentUser = login.getCurrentUser();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		map.put("instanceId", instance.getId());
		
		QuestionGroup group = masterService.loadQuestionGroup(questionGroupId);
		map.put("questionGroup",group);
		if(showMsg != null)
			map.put("showMsg",showMsg);
		
		map.put("needRequest",needRequest == null ? false : needRequest);
		
		//해당 페이지 그룹에 대해서 과거 문진이력데이타를 가져온다.
		//이 코드를 Background로 돌린다. 처음에  instance 생성시에 만든다.
		/*try{
			ocsInterface.initOcsWebResultFromHistory(instance, currentUser, group);	
		}catch(Exception e){
			log.error("Error : ",e);
			return "error/serverProcessingError";
		}*/
		
		Category category = group.getCategory();
		List<QuestionGroup> groups = masterService.findQuestionGroup(category, currentUser.getGender(),instance.isSkipNutrition(),instance.isSkipStress());
		map.put("questionGroups",groups);
		map.put("categoryTypeName",category.getType().name());
		
		if(group.getNurseEditable() != null && group.getNurseEditable()){
			//간호사 체크리스트와 관련된 질문그룹일 경우.
			Date today = new Date();
			//오늘이 검진당일인지 아닌지 여부를 판단한다.
			//여기서 검진당일 + 검진 전일 + 검진 전전일까지를 today로 판단해서 보여준다.
			boolean todayIsReserveDate = CommonUtils.isInRangeDay(today, instance.getReserveDate(), 2, true);
			
			List<Question> questions = masterService.findQuestions(group, 1, currentUser.getGender(),todayIsReserveDate);
			map.put("questions",questions);
			
		}else{
			List<Question> questions = masterService.findQuestions(group, 1, currentUser.getGender());
			map.put("questions",questions);
		}
		
		List<QuestionResult> results = service.findResult(currentUser, instance, group, 1);
		map.put("resultMap", ResultMaker.makeResultMap(results));
		map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
		
		String template = group.getGroupType().getTemplate();
		//HACK 식생활 습관만을 위한 유형.
		if(group.getGroupType().equals(QuestionGroupType.NUTRITION_3)){
			//현재 그룹의 2단계 질문의 결과값도 같이 가져와서 itemGroup으로 따로 만든다. 
			List<QuestionResult> results2 = service.findResult(currentUser,  instance,group, 2);
			map.put("frequencyResultMap", ResultMaker.makeResultMap(results2,NutritionItemType.FREQUENCY));
			map.put("quantityResultMap",ResultMaker.makeResultMap(results2,NutritionItemType.QUANTITY));
			map.put("whetherResultMap", ResultMaker.makeResultMap(results2,NutritionItemType.WHETHER));
		}
		
		//NavigationInfo navi = makeNavigation(group, groups, currentUser, instance.isSkipNutrition());
		NavigationInfo navi = makeNavigation(group, groups, currentUser, false,instance); 
		//네비게이션을 돌아다닐때에는 영양설문 카테고리로는 이동가능해야한다. 그래야 영양설문을 진행할지 여부를 다시 선택이 가능하다.
		map.put("navigationInfo",navi);
		
		map.put("previewFlag",false);
		map.put("nurseViewFlag", false);
		map.put("nurseEditable", false);
		
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return template;
	}
	
	
	/**
	 * 2Depth에 해당하는 질문들을 가져온다.
	 */
	@RequestMapping(value="/secondDepth/{questionItemId}")
	public String secondDepth(
			@PathVariable("questionItemId") String questionItemId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam(value="nurseViewFlag",required=false) Boolean nurseViewFlag,
			ModelMap map) throws IOException{
		
		map.put("itemId",questionItemId);
		map.put("previewFlag", previewFlag==null ? false : previewFlag);
		map.put("nurseViewFlag", nurseViewFlag==null ? false : nurseViewFlag);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
		
		if(previewFlag == null || previewFlag == false){
			User currentUser = login.getCurrentUser();
			
			CheckupInstance instance = service.findProgressInstance(currentUser);
			map.put("instanceId", instance.getId());
			
			Date today = new Date();
			//오늘이 검진당일인지 아닌지 여부를 판단한다.
			//여기서 검진당일 + 검진 전일 + 검진 전전일까지를 today로 판단해서 보여준다.
			boolean todayIsReserveDate = CommonUtils.isInRangeDay(today, instance.getReserveDate(), 2, true);
			
			List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),2,todayIsReserveDate);
			map.put("questions",questions);
			
			List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 2);
			map.put("resultMap", ResultMaker.makeResultMap(results));
			map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
			map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		}else{
			List<Question> questions = masterService.findChildQuestions(questionItemId, Gender.ALL,2,true);
			map.put("questions",questions);
			map.put("patientAge",-1);
		}
		
		
		map.put("nurseEditable", false);
		
		return "/member/checkup/secondDepth";
	}
	
	@RequestMapping(value="/thirdDepth/{questionItemId}")
	public String thirdDepth(
			@PathVariable("questionItemId") String questionItemId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam(value="nurseViewFlag",required=false) Boolean nurseViewFlag,
			ModelMap map) throws IOException{
		
		map.put("itemId",questionItemId);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
		
		if(previewFlag == null || previewFlag == false){
			User currentUser = login.getCurrentUser();
			CheckupInstance instance = service.findProgressInstance(currentUser);
			map.put("instanceId", instance.getId());
			

			Date today = new Date();
			//오늘이 검진당일인지 아닌지 여부를 판단한다.
			//여기서 검진당일 + 검진 전일 + 검진 전전일까지를 today로 판단해서 보여준다.
			boolean todayIsReserveDate = CommonUtils.isInRangeDay(today, instance.getReserveDate(), 2, true);
			List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),3,todayIsReserveDate);
			map.put("questions",questions);
			
			List<QuestionResult> results = service.findResult(currentUser, instance,item.getParentQuestion().getParentGroup(), 3);
			map.put("resultMap", ResultMaker.makeResultMap(results));
			map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
			map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		}else{
			List<Question> questions = masterService.findChildQuestions(questionItemId, Gender.ALL,3,true);
			map.put("questions",questions);
			map.put("patientAge",-1);
		}
		
		map.put("previewFlag", previewFlag==null ? false : previewFlag);
		map.put("nurseViewFlag", nurseViewFlag==null ? false : nurseViewFlag);
		map.put("nurseEditable", false);
		
		return "/member/checkup/thirdDepth";
	}
	
	@RequestMapping(value="/nutrition/toggleNoneFlag",produces = "application/json;charset=utf-8")
	@ResponseBody
	public String toggleNoneFlag(
			@RequestParam("questionItemId") String questionItemId,
			@RequestParam("toggleFlag") boolean toggleFlag
			) throws IOException{
		User currentUser = login.getCurrentUser();
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		try{
			service.doSubmitAndClear(questionItemId, currentUser, toggleFlag,false);
			log.info("Call Toggle : " + toggleFlag + "," + questionItemId);
			submitModel.setSuccess(true);
		}catch(Exception e){
			log.error("error :",e);
			submitModel.setMsg(e.getMessage());
			submitModel.setSuccess(false);
		}
		
		return CommonUtils.toJson(submitModel);
	}
			
	
	
	
	@RequestMapping(value="/nutrition/secondDepth/{questionId}")
	public String nutritionSecondDepth(
			@PathVariable("questionId") String questionId,
			@RequestParam("selectedItemIds") String selectedItemIds,ModelMap map) throws IOException{
		User currentUser = login.getCurrentUser();
		String[] itemIds = selectedItemIds.split(",");
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		//우선 현재 질문에 대해서 기존에 선택한걸 초기화하고 이걸로 다시 설정한다.
		try{
			service.doSubmitAndClear(questionId, currentUser, itemIds,false);
			
			Question question = masterService.loadQuestion(questionId);
			map.addAttribute("secondInfo",makeNutritionSecond(itemIds,2));
			
			List<QuestionResult> results = service.findResult(currentUser, instance,question.getParentGroup(), 2);
			map.put("frequencyResultMap", ResultMaker.makeResultMap(results,NutritionItemType.FREQUENCY));
			map.put("quantityResultMap",ResultMaker.makeResultMap(results,NutritionItemType.QUANTITY));
			map.put("monthResultMap",ResultMaker.makeResultMap(results,NutritionItemType.MONTH));
			map.put("whetherResultMap", ResultMaker.makeResultMap(results,NutritionItemType.WHETHER));
			
			map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		}catch(Exception e){
			map.addAttribute("error",e.getMessage());
		}
		
		map.put("previewFlag", false);
		map.put("nurseViewFlag", false);
		
		return "/member/checkup/nutrition/secondDepth";
	}
	
	private NutritionSecond makeNutritionSecond(String[] itemIdStrs,int depth){
		NutritionSecond secondInfo = new NutritionSecond();
		User currentUser = login.getCurrentUser();
		
		List<String> itemIds = new ArrayList<String>();
		List<Question> questions = new ArrayList<Question>();
		Map<String,NutritionSecondItem> itemMap = new HashMap<String,NutritionSecondItem>();
		
		for(String itemId : itemIdStrs){
			if(itemId != null && !itemId.equals("")){
				QuestionItem item = masterService.loadQuestionItem(itemId);
				List<Question> childs = masterService.findChildQuestions(itemId, currentUser.getGender(),depth,true);
				if(childs.size() > 0){
					NutritionSecondItem secondItem = new NutritionSecondItem();
					Question question = childs.get(0);
					secondItem.setQuestionId(question.getId());
					secondItem.setItemId(itemId);
					secondItem.setItemTitle(item.getTitle());
					secondItem.setQuestionDesc(question.getDescription());
					
					for(QuestionItem subItem : question.getChildItems()){
						if(subItem.isActive() == false) continue;
						if(subItem.getItemGroup() != null){
							switch(subItem.getItemGroup()){
							case FREQUENCY:
								secondItem.getFrequencyItems().add(subItem);
								break;
							case QUANTITY:
								secondItem.getQuantityItems().add(subItem);
								break;
							case MONTH:
								secondItem.getMonthItems().add(subItem);
								break;
							case WHETHER:
								secondItem.getWhetherItems().add(subItem);
							}
							
						}
					}
					itemMap.put(itemId, secondItem);
					questions.add(question);
					itemIds.add(itemId);
				}
			}
		}
		
		secondInfo.setItemIds(itemIds);
		secondInfo.setItemInfos(itemMap);
		secondInfo.setTopQuestionTitle(questions.get(0).getTitle());
		
		
		return secondInfo;
	}
	
	
	
	/**
	 * 질문의 결과를 반영한다. 
	 * @param submitModel
	 * @param result
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/doSubmit",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String doSubmit(@ModelAttribute("questionSubmitModel") QuestionSubmitModel submitModel,
			BindingResult result) throws IOException{
		User currentUser = login.getCurrentUser();
		log.info("sumited : " + submitModel.getQuestionId());
		
		try{
			service.doSubmit(submitModel, currentUser);
			submitModel.setSuccess(true);
		}catch(Exception e){
			log.error("doSubmitError : ",e);
			submitModel.setMsg(e.getMessage());
			submitModel.setSuccess(false);
		}
		
		String json = CommonUtils.toJson(submitModel);
		return json;
	}
	
	/**
	 * Radio와 checkbox item을 선택한것을 취소한다.
	 * result를 inactive처리하고 ocs result도 미결코드로 전환한다. -->미결코드 전환은 여기서 하지 않는다.
	 * 
	 * @param itemId
	 * @param questionId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/doCancel",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String doCancel(@RequestParam(value="questionItemId",required=false) String itemId,
			@RequestParam("questionId") String questionId,
			@RequestParam("type") String type
			) throws IOException{
		User currentUser = login.getCurrentUser();
		
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		try{
			submitModel.setQuestionId(questionId);
			submitModel.setQuestionItemId(itemId);
			submitModel.setType(type);
			
			service.doCancel(submitModel, currentUser);
			submitModel.setSuccess(true);
		}catch(Exception e){
			log.error("doCancelError : ",e);
			submitModel.setMsg(e.getMessage());
			submitModel.setSuccess(false);
		}
		
		String json = CommonUtils.toJson(submitModel);
		return json;
		
	}
	
	/**
	 * 페이지 네비게이션 정보를 얻어온다.
	 * @param currentGroup
	 * @param groups
	 * @return
	 */
	private NavigationInfo makeNavigation(QuestionGroup currentGroup,List<QuestionGroup> groups,User user,boolean skipNutrition,CheckupInstance instance){
		NavigationInfo navi = new NavigationInfo();
		
		QuestionGroup preGroup=null;
		QuestionGroup nextGroup=null;
		Category nextCategory=null;
		
		for(int i=0; i < groups.size(); i++){
			QuestionGroup row = groups.get(i);
			if(row.getId().equals(currentGroup.getId())){
				if(i == 0 && groups.size() > 1){
					//해당 카테고리의 맨 처음인 경우임.
					//여기에는 이전은 없음.
					nextGroup = groups.get(i+1);
					preGroup = null;
				}else if(i == 0 && groups.size() == 1){	
					//맨 처음이면서 마지막인경우
					nextGroup = null;
					preGroup = null;
					navi.setLastQuestionGroupInCategory(true);
				}else if(i == groups.size()-1){
					//해당 카테고리의 마지막인경우.
					preGroup = groups.get(i-1);
					nextGroup = null;
					navi.setLastQuestionGroupInCategory(true);
				}else{
					//일반적인 중간의 경우 
					preGroup = groups.get(i-1);
					nextGroup = groups.get(i+1);
				}
				break;
			}
		}
		
		if(preGroup != null){
			navi.setPreUrl("/member/checkup/questionGroup/" + preGroup.getId());
		}
		
		if(navi.isLastQuestionGroupInCategory()){
			//다음 카테고리를 얻어와서 해당 카테고리의 맨 처음으로 보낸다.
			nextCategory = masterService.findNextCategory(currentGroup,instance.isSamsungEmployee(),skipNutrition,instance.isNeedSleepTest(),instance.isNeedStressTest());
			
			if(nextCategory == null){
				navi.setLastQuestionGroup(true);
				navi.setNextUrl(WebPath.MEMBER_PATH + "/checkup/static/thankyou");
			}else{
				navi.setNextUrl(WebPath.MEMBER_PATH + "/checkup/category/" + nextCategory.getType().name());
			}
		}else{
			if(nextGroup != null)
				navi.setNextUrl("/member/checkup/questionGroup/" + nextGroup.getId());
		}
		
		return navi;
	}
	
	@RequestMapping("/custom/drink2Depth/{questionItemId}")
	public String drink2Depth(
			@PathVariable("questionItemId") String questionItemId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam(value="nurseViewFlag",required=false) Boolean nurseViewFlag,
			ModelMap map) throws IOException{
		
		User currentUser = login.getCurrentUser();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),2,true);
		map.put("questions",questions);
		
		map.put("itemId",questionItemId);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
		
		List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 2);
		map.put("resultMap", ResultMaker.makeResultMap(results));
		map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
		
		map.put("previewFlag", previewFlag==null ? false : previewFlag);
		map.put("nurseViewFlag", nurseViewFlag==null ? false : nurseViewFlag);
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return "/member/checkup/custom/drink2Depth";
	}
	
	@RequestMapping(value="/custom/drink3Depth/{questionId}")
	public String drink3Depth(
			@PathVariable("questionId") String questionId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam(value="nurseViewFlag",required=false) Boolean nurseViewFlag,
			@RequestParam("selectedItemIds") String selectedItemIds,ModelMap map) throws IOException{
	
		User currentUser = login.getCurrentUser();
		String[] itemIds = selectedItemIds.split(",");
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		//우선 현재 질문에 대해서 기존에 선택한걸 초기화하고 이걸로 다시 설정한다.
		try{
			service.doSubmitAndClear(questionId, currentUser, itemIds,true);
			Question question = masterService.loadQuestion(questionId);
			map.addAttribute("secondInfo",makeNutritionSecond(itemIds,3));
			
			List<QuestionResult> results = service.findResult(currentUser, instance,question.getParentGroup(), 3);
			map.put("frequencyResultMap", ResultMaker.makeResultMap(results,NutritionItemType.FREQUENCY));
			map.put("quantityResultMap",ResultMaker.makeResultStringMap(results,NutritionItemType.QUANTITY));
			
			
		}catch(Exception e){
			map.addAttribute("error",e.getMessage());
		}
		map.put("previewFlag", previewFlag==null ? false : previewFlag);
		map.put("nurseViewFlag", nurseViewFlag==null ? false : nurseViewFlag);
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return "/member/checkup/custom/drink3Depth";
	}
	
	@RequestMapping("/custom/family2Depth/{questionItemId}")
	public String family2Depth(
			@PathVariable("questionItemId") String questionItemId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam(value="nurseViewFlag",required=false) Boolean nurseViewFlag,
			ModelMap map) throws IOException{
		
		map.put("itemId",questionItemId);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
		
		Question topQuestion = null;
		User currentUser = null;
		
		if(previewFlag == null || previewFlag == false){
			currentUser = login.getCurrentUser();
			CheckupInstance instance = service.findProgressInstance(currentUser);
			
			List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 2);
			map.put("resultMap", makeSimpleStrResultMap(results));
			//좌측 아아템에 대한 답변들.
			
		
			List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),2,true);
			map.put("questions",questions);
			
			topQuestion = questions.get(0);
			map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		}else{
			List<Question> questions = masterService.findChildQuestions(questionItemId, Gender.ALL,2,true);
			map.put("questions",questions);
			
			topQuestion = questions.get(0);
			map.put("patientAge",-1);
		}
		
		map.put("subItems", makeFamilySubItems(topQuestion,currentUser));
		map.put("previewFlag", previewFlag==null ? false : previewFlag);
		map.put("nurseViewFlag", nurseViewFlag==null ? false : nurseViewFlag);
		
		return "/member/checkup/custom/family2Depth";
	}
	
	private Map<String,List<FamilySubItem>> makeFamilySubItems(Question topQuestion,User currentUser){
		Map<String,List<FamilySubItem>> map = new HashMap<String,List<FamilySubItem>>();
		
		for(QuestionItem item : topQuestion.getChildItems()){
			if(item.isActive() == false) continue;
			List<Question> subQuestions = masterService.findChildQuestions(item.getId(), Gender.ALL, 3,true);
			if(subQuestions.size() > 0){
				Question subQuestion = subQuestions.get(0);
				List<FamilySubItem> subItems = map.get(item.getId());
				if(subItems == null){
					subItems = new ArrayList<FamilySubItem>();
					map.put(item.getId(), subItems);
				}
				
				Map<String,QuestionResult> resultMap = new HashMap<String,QuestionResult>();
				if(currentUser != null){
					CheckupInstance instance = service.findProgressInstance(currentUser);
					List<QuestionResult> results = service.findResult(currentUser, instance,topQuestion.getParentGroup(), 3);
					for(QuestionResult result : results){
						if(result.isActive()){
							resultMap.put(result.getObjectiveValue(),result);
						}
					}
				}
				//총 8개의 column이 순서대로 있어야한다. 존재하지 않으면 빈것을 만들어서 리턴한다.
				Map<String,QuestionItem> itemMap = new HashMap<String,QuestionItem>();
				
				for(QuestionItem subItem : subQuestion.getChildItems()){
					if(subItem.isActive() == false) continue;
					itemMap.put(subItem.getTitle(),subItem);
				}
				
				for(int idx = 0; idx < 8; idx++){
					switch(idx){
					case 0 : 
						if(itemMap.containsKey("부")){
							insertFamilySubItem(itemMap.get("부"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
					case 1 :
						if(itemMap.containsKey("모")){
							insertFamilySubItem(itemMap.get("모"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
					case 2:
						if(itemMap.containsKey("형제자매")){
							insertFamilySubItem(itemMap.get("형제자매"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
					case 3:
						if(itemMap.containsKey("자녀")){
							insertFamilySubItem(itemMap.get("자녀"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
						
					case 4:
						if(itemMap.containsKey("친조부모")){
							insertFamilySubItem(itemMap.get("친조부모"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
						
					case 5:
						if(itemMap.containsKey("4촌이내(친가)")){
							insertFamilySubItem(itemMap.get("4촌이내(친가)"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
						
					case 6:
						if(itemMap.containsKey("외조부모")){
							insertFamilySubItem(itemMap.get("외조부모"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;
						
					case 7:
						if(itemMap.containsKey("4촌이내(외가)")){
							insertFamilySubItem(itemMap.get("4촌이내(외가)"),resultMap,subItems);
						}else{
							subItems.add(makeEmptyCol());
						}
						break;			
					}
					
				}
			}
		}
		
		return map;
	}
	
	private FamilySubItem makeEmptyCol(){
		FamilySubItem subItem = new FamilySubItem();
		subItem.setActive(false);
		return subItem;
	}
	
	private void insertFamilySubItem(QuestionItem subItem,Map<String,QuestionResult> resultMap,List<FamilySubItem> subItems){
		FamilySubItem subItemWrapper = new FamilySubItem();
		subItemWrapper.setItem(subItem);
		subItemWrapper.setActive(true);
		
		if(resultMap.containsKey(subItem.getId())){
			subItemWrapper.setChecked(true);
			subItemWrapper.setStrValue(resultMap.get(subItem.getId()).getStrValue());
		}else{
			subItemWrapper.setChecked(false);
		}
		subItems.add(subItemWrapper);
	}
	
	private Map<String,String> makeSimpleStrResultMap(List<QuestionResult> results){
		Map<String, String> map = new HashMap<String,String>();
		
		for(QuestionResult result : results){
			QuestionType type = result.getType();
			boolean isContinue = false;
			
			switch(type){
			case SUBJECTIVE:
			case SUBJECTIVE_HOUR_MINUTE:
			case SUBJECTIVE_HOUR_MINUTE_RANGE:	
			case SUBJECTIVE_MONTH_DATE_RANGE:
			case SUBJECTIVE_YEAR:
			case SUBJECTIVE_YEAR_MONTH:
			case SUBJECTIVE_YEAR_MONTH_DAY:
			case SUBJECTIVE_YEAR_MONTH_RANGE:
				isContinue = true;
			}
			
			
			if(isContinue) continue;
			
			map.put(result.getObjectiveValue(),result.getStrValue());
			
		}
		
		return map;
	}
	
	@RequestMapping("/help/{questionGroupId}")
	public String help(
			@PathVariable("questionGroupId") String questionGroupId,ModelMap map) throws IOException{
		
		QuestionGroup group = masterService.loadQuestionGroup(questionGroupId);
		map.addAttribute("questionGroup", group);
		map.addAttribute("helpContents", group.getHelp());
		
		return "/member/checkup/help";
	}
	
	@RequestMapping("/static/{template}")
	public String staticPage(
			@PathVariable("template") String template,ModelMap map) throws IOException{
		
		
		return "/member/checkup/static/" + template;
	}
	
	
	@RequestMapping(value="/updateAgreeFlag",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updateAgreeFlag(@RequestParam("agreeFlag") String agreeFlag,HttpServletResponse response){
		User currentUser = login.getCurrentUser();
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		service.updateAgree(AgreeType.valueOf(agreeFlag), instance);
		
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		submitModel.setSuccess(true);
		
		String json = CommonUtils.toJson(submitModel);
		return json;
	}
	
	/**
	 * 영양설문을 진행할지 여부를 업데이트한다.
	 * 이값에 따라서 전체 질문수및 진행률을 재조정하고 Next URL을 리턴한다.
	 * @param flag
	 * @return
	 */
	@RequestMapping(value="/updateNutritionFlow/{flag}",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updateNutritionFlow(@PathVariable("flag") String flag){
		User currentUser = login.getCurrentUser();
		
		QuestionSubmitModel model = new QuestionSubmitModel();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		String ocsAskCode = "KEH_45";
		String ocsAnswer = "";
		if(flag.equals("true")){
			ocsAnswer= "1";
			//영양설문 진행.
			service.updateNutritionFlow(instance, false);
			//model.setNextUrl(WebPath.MEMBER_PATH  + "/checkup/category/intro2/NUTRITION");
			QuestionGroup firstGroup = masterService.findFirstQuestionGroup(instance.getMaster(), CategoryType.NUTRITION.name());
			model.setNextUrl(WebPath.MEMBER_PATH +  "/checkup/questionGroup/" + firstGroup.getId());
			
		}else{
			ocsAnswer= "0";
			//영양설문 안함.
			service.updateNutritionFlow(instance, true);
			//여기서 영양에 해당하는 모든 질문에 미결코드를 넣어야한다.
			try{
				ocsInterface.initOcsWebResult(instance, currentUser, masterService.findUniqueCategory(instance.getMaster(), CategoryType.NUTRITION));
			}catch(Exception e){
				log.error("init noAnswerCode : ",e);
			}
			
			Category currentCategory = masterService.findUniqueCategory(instance.getMaster(),CategoryType.NUTRITION);
			Category nextCategory = masterService.findNextCategory(currentCategory,instance.isSamsungEmployee(),true,instance.isNeedSleepTest(),instance.isNeedStressTest());
			
			if(nextCategory == null){
				model.setNextUrl(WebPath.MEMBER_PATH + "/checkup/static/thankyou");
			}else{
				//ocs webstat를 업데이트한다. 영양이 완료된것으로처리함.
				updateService.updateOcsWebstat(instance, 4);
				
				model.setNextUrl(WebPath.MEMBER_PATH + "/checkup/category/" + nextCategory.getType().name());
//				List<QuestionGroup> groups = masterService.findQuestionGroup(nextCategory, currentUser.getGender());
//				if(groups.size() > 0){
//					QuestionGroup firstGroup = groups.get(0);
//					model.setNextUrl(WebPath.MEMBER_PATH +  "/checkup/questionGroup/" + firstGroup.getId());
//				}
			}
		}
		
		model.setSuccess(true);
		model.setProgressRate(instance.getProgress());
		
		/**
		 * OCS WebDB에 업데이트한다.
		 */
		List<EmbeddedPatientNo> nos = currentUser.getPatientNos();
		EmbeddedPatientNo no = nos.get(nos.size()-1);
		ocsInterface.updateOcsRow(currentUser, no.getPatientNo(), instance.getMaster().getVersion(), ocsAskCode, ocsAnswer, true, instance.getReserveDate(),instance);
		
		return CommonUtils.toJson(model);
	}
	
	@RequestMapping(value="/calculateCalory",produces = "application/json;charset=utf-8")
	@ResponseBody
	public String calculateCalory(){
		User currentUser = login.getCurrentUser();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		
		try{
			long calory = service.calculateCalory(instance, currentUser);
			submitModel.setSuccess(true);
			submitModel.setCalory(calory);
		}catch(Exception e){
			log.error("calory error : ",e);
			submitModel.setSuccess(false);
			submitModel.setMsg(e.getMessage());
		}
		
		return CommonUtils.toJson(submitModel);
	}
	
	/**
	 * 동의서에 이미 동의처리를 안한사람인지를 보고 어디로 갈지 결정한다.
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/static/thankyou")
	public String thankyou(Model model){
		User currentUser = login.getCurrentUser();
		String nextUrl = "";
		if(currentUser.getNeedAgree() != null && currentUser.getNeedAgree()){
			nextUrl = "/member/checkup/static/agreeStep1";
		}else{
			nextUrl = "/member/checkup/static/viewGuide";
		}
		
		model.addAttribute("nextUrl",nextUrl);
		
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		if(instance.getStatus().equals(InstanceStatus.IN_PROGRESS) || 
				instance.getStatus().equals(InstanceStatus.FIRST_COMPLETED)){
			updateService.completeInstance(instance);
			//여기서 다시한번 ocsDB와 interface한다.
			try{
				//일단 주석처리함. 
				//ocsInterface.syncAllOcsWebDb(instance);
			}catch(Exception e){
				log.error("Error : ",e);
			}
		}
		
		
		return "/member/checkup/static/thankyou";
	}
	
	
	
	@RequestMapping(value="/static/agreeStep3")
	public String agreeStep3(Model model){
		User currentUser = login.getCurrentUser();
		CheckupInstance instance = service.findProgressInstance(currentUser);
		
		if(instance.getAgreeFlag() != null){
			model.addAttribute("firstAgreed",instance.getAgreeFlag());
		}
		
		if(instance.getAgreeFlag2() != null){
			model.addAttribute("secondAgreed",instance.getAgreeFlag2());
		}
		
		return "/member/checkup/static/agreeStep3";
	}
	
	@RequestMapping(value="viewProgress")
	public String viewProgress(Model model){
		
		model.addAttribute("progressInfo",service.getProgress(login.getCurrentUser()));
		
		return "/member/checkup/progress";
	}
	
	@RequestMapping(value="/static/viewGuide")
	public String viewGuide(ModelMap model){
		
		return "redirect:/member/checkup/static/guide/1/00"; 
	}
	
	@RequestMapping(value="/static/guide/{imageName1}/{imageName2}")
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
					model.addAttribute("nextUrl","/member/checkup/static/guide/" +  nextPath + "/00");
				}else{
					//맨 마지막인경우.
					model.addAttribute("nextUrl","/member/checkup/static/last");
				}
			}else if(currentGuide.getTotalStepCount() - 1 > currentStep){
				String nextStep = String.format("%02d",currentStep + 1);
				model.addAttribute("nextUrl","/member/checkup/static/guide/" +  imageName1 + "/" + nextStep);
				
			}
			
			String preUrl = "/member/checkup/static/guide/" + imageName1;
			String preStep = String.format("%02d",currentStep - 1);
			model.addAttribute("preUrl",preUrl + "/" + preStep);
			
		}else{
			model.addAttribute("nextUrl","/member/checkup/static/guide/" +  imageName1 + "/01");
		
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
			item.setUrl("/member/checkup/static/guide/" + (i+1) + "/00");
			
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
//			case 7:
//				item.setTotalStepCount(2);
//				break;
//			case 8:
//				item.setTotalStepCount(11);
//				break;
//			case 9:
//				item.setTotalStepCount(2);
//				break;
//			
			}
			if(item.getTitle() == null) continue;
			this.guides.add(item);
		}
	}
	
	/**
	 * 영양문진에서 응답여부의 값을 입력받는 함수.
	 * 사용안함. 이것말고 이미 영양에서 응답여부를 입력받는 함수가 있는데 그곳에 추가하면됨.
	 * @return
	 */
	@Deprecated
	@RequestMapping(value="/goSkipNutritionCode", method=RequestMethod.POST, produces="application/json; charset=utf-8")
	@ResponseBody
	public String goSkipNutritionCode(){
		
		return null;
		
	}
	
}


