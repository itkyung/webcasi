package com.kbsmc.webcasi.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.WebPath;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.checkup.IOCSInterfaceService;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.User;

@Controller
@RequestMapping(value=WebPath.NURSE_PATH)
public class NurseHomeController {
	private static final Logger log = LoggerFactory.getLogger(NurseHomeController.class);
	
	@Autowired private IUserService userService;
	@Autowired private ICheckupInstanceService service;
	@Autowired private ICheckupMasterService masterService;
	@Autowired private IOCSInterfaceService ocsInterface;
	
	private int pageSize = 150;
	
	@RequestMapping(value="home")
	public String home(@RequestParam(value="acptDate",required=false) Date acptDate,
			@RequestParam(value="patno",required=false) String patno,
			@RequestParam(value="name",required=false) String name,
			@RequestParam(value="resno",required=false) String resno,
			@RequestParam(value="page",required=false) Integer page,
			ModelMap map){
		
		acptDate = "".equals(acptDate) ? null : acptDate;
		patno = "".equals(patno) ? null : patno;
		name = "".equals(name) ? null : name;
		resno = "".equals(resno) ? null : resno;
		if(page == null) page = 1;
		
		//간호사 메인
		if(acptDate != null || patno != null || name != null || resno != null){
			
			int start = (page.intValue()-1) * pageSize;
			int limit = pageSize;
			
			List<CheckupInstance> results = service.findInstance(acptDate, patno, resno, name, start, limit);
			map.addAttribute("results",results);
		}
		
		map.addAttribute("currentPage",page);
		map.addAttribute("acptDate",acptDate);
		map.addAttribute("patno",patno);
		map.addAttribute("resno",resno);
		map.addAttribute("name",name);
		
		
		return "nurse/home";
	}
	
	
	
	
	@RequestMapping(value="/category/{categoryType}")
	public String category(@PathVariable String categoryType,
			@RequestParam("patientNo") String patientNo,
			@RequestParam("instanceId") String instanceId,
			ModelMap map) throws IOException{
		User currentUser = userService.findPatient(patientNo);
		
		CheckupInstance instance = service.loadInstance(instanceId);
		
		Category category = masterService.findUniqueCategory(instance.getMaster(), CategoryType.valueOf(categoryType));
			
		List<QuestionGroup> groups = masterService.findQuestionGroup(category, currentUser.getGender());
		QuestionGroup firstGroup = groups.get(0);
			
		return "redirect:/nurse/view/" + patientNo + "?instanceId=" + instanceId + "&questionGroupId=" + firstGroup.getId();
	}
	
	private PatientInfo makePatient(User user,CheckupInstance instance){
		PatientInfo info = new PatientInfo();
		
		info.setPatient(user);
		info.setPatNo(userService.findPatientNo(user));
		info.setSocialNumber(user.getResno());
		info.setAge(CommonUtils.getAge(user.getResno()));
		info.setReserveDate(instance.getReserveDate());
		info.setAcptDate(instance.getAcptDate());
		
		return info;
	}
	
	/**
	 * 간호사의 WebViewer에서 보는 환자의 체크리스트 화면.
	 * @param patientNo
	 * @return
	 */
	@RequestMapping(value="/view/{patientNo}",method=RequestMethod.GET)
	public String viewCheckup(
			@PathVariable("patientNo") String patientNo,
			@RequestParam(value="hopeDate",required=false) String hopeDate,
			@RequestParam(value="questionGroupId",required=false) String questionGroupId,
			@RequestParam(value="instanceId",required=false) String instanceId,
			ModelMap map){
		User currentUser = userService.findPatient(patientNo);
		
		CheckupInstance instance = null;
		if(instanceId != null)
			instance = service.loadInstance(instanceId);
		else
			instance = service.findInstanceByReserveDateStr(currentUser, hopeDate);
		
		//만약에 instance가 없는경우에는 ocs예약정보가 변경되엇을 확률이 잇기 때문에 Ocs 예약정보를 동기화를 한후에 다시 찾는다.
		if(instance == null){
			service.syncReserveNo(currentUser);
			instance = service.findInstanceByReserveDateStr(currentUser, hopeDate);
		}else{
			service.syncAcptDate(instance);	//surempt테이블의 acptDate가 업데이트 되었는지 확인해서 sync한다.
		}
		
		map.put("patientInfo", makePatient(currentUser, instance));
		
		map.put("history", service.findHistory(currentUser));
		
		map.put("instanceId", instance.getId());
		map.put("patientNo",patientNo);
		
		map.addAttribute("_samsungEmployee",instance.isSamsungEmployee());
		map.addAttribute("_needStressTest",instance.isNeedStressTest());
		map.addAttribute("_needSleepTest",instance.isNeedSleepTest());
		map.addAttribute("_goNutrition",!instance.isSkipNutrition());
		
		
		List<QuestionGroup> groups = null;
		QuestionGroup group = null;
		if(questionGroupId == null){
			Category category = masterService.findUniqueCategory(instance.getMaster(), CategoryType.CHECK_LIST);
			groups = masterService.findQuestionGroup(category, currentUser.getGender());
			group = groups.get(0);
			map.put("_currentCategoryType",category.getType().name());
		}else{
			group = masterService.loadQuestionGroup(questionGroupId);
			
			Category category = group.getCategory();
			groups = masterService.findQuestionGroup(category, currentUser.getGender(),instance.isSkipNutrition(),instance.isSkipStress());
			map.put("_currentCategoryType",category.getType().name());
		}
		
		map.put("questionGroup",group);
		map.put("questionGroups",groups);
		
		boolean todayIsReserveDate = true;
		//간호사 뷰에서는 항상 모든 질문이 다 보여야한다.
		List<Question> questions = masterService.findQuestions(group, 1, currentUser.getGender(),todayIsReserveDate);
		map.put("questions",questions);
		
		
		List<QuestionResult> results = service.findResult(currentUser, instance, group, 1);
		map.put("resultMap", ResultMaker.makeResultMap(results));
		map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
		
		//HACK 식생활 습관만을 위한 유형.
		if(group.getGroupType().equals(QuestionGroupType.NUTRITION_3)){
			//현재 그룹의 2단계 질문의 결과값도 같이 가져와서 itemGroup으로 따로 만든다. 
			List<QuestionResult> results2 = service.findResult(currentUser,  instance,group, 2);
			map.put("frequencyResultMap", ResultMaker.makeResultMap(results2,NutritionItemType.FREQUENCY));
			map.put("quantityResultMap",ResultMaker.makeResultMap(results2,NutritionItemType.QUANTITY));
		}
				
		String template = group.getGroupType().getTemplate();
		
		NavigationInfo navi = makeNavigation(group, groups, currentUser, false,patientNo,instance.getId()); 
		
		map.put("navigationInfo",navi);
		
		map.put("previewFlag",true);
		map.put("nurseViewFlag", true);
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		
		if(group.getNurseEditable() != null && group.getNurseEditable()){
			map.put("nurseEditable",true);
		}else{
			map.put("nurseEditable",false);
		}
		
		List<NurseCheckResult> nurseResults = service.findNurseResult(instance, group, 1);
		map.put("nurseResultMap",ResultMaker.makeNurseResult(nurseResults));
		
		map.put("needProtection",false);
		
		return template;
	}
	
	@RequestMapping(value="/secondDepth/{questionItemId}")
	public String secondDepth(@PathVariable("questionItemId") String questionItemId,
			@RequestParam("instanceId") String instanceId,ModelMap map) throws IOException{
		
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		String patientNo = userService.findPatientNo(currentUser);
		
		map.put("previewFlag", true);
		map.put("nurseViewFlag", true);
		map.put("instanceId",instanceId);
		map.put("patientNo",patientNo);
		map.put("itemId",questionItemId);
	
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
			
		List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),2,true);
		map.put("questions",questions);
		
		List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 2);
		map.put("resultMap", ResultMaker.makeResultMap(results));
		map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
			
		QuestionGroup group = questions.get(0).getParentGroup();
		map.put("nurseEditable", group.getNurseEditable() == null ? false : group.getNurseEditable());
		
		List<NurseCheckResult> nurseResults = service.findNurseResult(instance, group, 2);
		map.put("nurseResultMap",ResultMaker.makeNurseResult(nurseResults));
		
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return "/member/checkup/secondDepth";
	}
	
	@RequestMapping(value="/thirdDepth/{questionItemId}")
	public String thirdDepth(@PathVariable("questionItemId") String questionItemId,
			@RequestParam("instanceId") String instanceId,ModelMap map) throws IOException{
		
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		String patientNo = userService.findPatientNo(currentUser);
		
		map.put("previewFlag", true);
		map.put("nurseViewFlag", true);
		map.put("instanceId",instanceId);
		map.put("patientNo",patientNo);
		map.put("itemId",questionItemId);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
			
		List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),3,true);
		map.put("questions",questions);
		
		List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 3);
		map.put("resultMap", ResultMaker.makeResultMap(results));
		map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
			
		QuestionGroup group = questions.get(0).getParentGroup();
		map.put("nurseEditable", group.getNurseEditable() == null ? false : group.getNurseEditable());
		
		List<NurseCheckResult> nurseResults = service.findNurseResult(instance, group, 3);
		map.put("nurseResultMap",ResultMaker.makeNurseResult(nurseResults));
		
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return "/member/checkup/thirdDepth";
	}
	
	@RequestMapping("/custom/drink2Depth/{questionItemId}")
	public String drink2Depth(
			@PathVariable("questionItemId") String questionItemId,
			@RequestParam("instanceId") String instanceId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			ModelMap map) throws IOException{
		
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		String patientNo = userService.findPatientNo(currentUser);
		
		List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),2,true);
		map.put("questions",questions);
		
		map.put("itemId",questionItemId);
		map.put("instanceId",instanceId);
		map.put("patientNo",patientNo);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
		
		List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 2);
		map.put("resultMap", ResultMaker.makeResultMap(results));
		map.put("resultSubjectiveMap", ResultMaker.makeSubjectiveMap(results));
		
		map.put("previewFlag", true);
		map.put("nurseViewFlag", true);
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return "/member/checkup/custom/drink2Depth";
	}
	
	@RequestMapping(value="/custom/drink3Depth/{questionId}")
	public String drink3Depth(
			@PathVariable("questionId") String questionId,
			@RequestParam("instanceId") String instanceId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam("selectedItemIds") String selectedItemIds,ModelMap map) throws IOException{
	
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		String patientNo = userService.findPatientNo(currentUser);
		String[] itemIds = selectedItemIds.split(",");
		map.put("patientNo",patientNo);
		
		try{
			Question question = masterService.loadQuestion(questionId);
			map.addAttribute("secondInfo",makeNutritionSecond(itemIds,3,currentUser));
			
			List<QuestionResult> results = service.findResult(currentUser, instance,question.getParentGroup(), 3);
			map.put("frequencyResultMap", ResultMaker.makeResultMap(results,NutritionItemType.FREQUENCY));
			map.put("quantityResultMap",ResultMaker.makeResultStringMap(results,NutritionItemType.QUANTITY));
			
			
		}catch(Exception e){
			map.addAttribute("error",e.getMessage());
		}
		map.put("previewFlag", true);
		map.put("nurseViewFlag", true);
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		return "/member/checkup/custom/drink3Depth";
	}
	
	private NutritionSecond makeNutritionSecond(String[] itemIdStrs,int depth,User currentUser){
		NutritionSecond secondInfo = new NutritionSecond();
		
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
	

	@RequestMapping("/custom/family2Depth/{questionItemId}")
	public String family2Depth(
			@PathVariable("questionItemId") String questionItemId,
			@RequestParam(value="previewFlag",required=false) Boolean previewFlag,
			@RequestParam("instanceId") String instanceId,
			ModelMap map) throws IOException{
		
		map.put("itemId",questionItemId);
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		String patientNo = userService.findPatientNo(currentUser);
		
		QuestionItem item = masterService.loadQuestionItem(questionItemId);
		map.put("questionId",item.getParentQuestion().getId());
		map.put("type",item.getType().name());
		
		Question topQuestion = null;
			
		List<QuestionResult> results = service.findResult(currentUser,  instance, item.getParentQuestion().getParentGroup(), 2);
		map.put("resultMap", makeSimpleStrResultMap(results));
		//좌측 아아템에 대한 답변들.
		
	
		List<Question> questions = masterService.findChildQuestions(questionItemId, currentUser.getGender(),2,true);
		map.put("questions",questions);
		
		topQuestion = questions.get(0);
		map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		
		
		map.put("subItems", makeFamilySubItems(topQuestion,currentUser,instance));
		map.put("previewFlag", true);
		map.put("nurseViewFlag", true);
		
		return "/member/checkup/custom/family2Depth";
	}
	
	@RequestMapping(value="/nutrition/secondDepth/{questionId}")
	public String nutritionSecondDepth(
			@PathVariable("questionId") String questionId,
			@RequestParam("selectedItemIds") String selectedItemIds,
			@RequestParam("instanceId") String instanceId,
			ModelMap map) throws IOException{
		
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		String patientNo = userService.findPatientNo(currentUser);
		String[] itemIds = selectedItemIds.split(",");
		
		
		try{
			//service.doSubmitAndClear(questionId, currentUser, itemIds,false);
			
			Question question = masterService.loadQuestion(questionId);
			map.addAttribute("secondInfo",makeNutritionSecond(itemIds,2,currentUser));
			
			List<QuestionResult> results = service.findResult(currentUser, instance,question.getParentGroup(), 2);
			map.put("frequencyResultMap", ResultMaker.makeResultMap(results,NutritionItemType.FREQUENCY));
			map.put("quantityResultMap",ResultMaker.makeResultMap(results,NutritionItemType.QUANTITY));
			map.put("monthResultMap",ResultMaker.makeResultMap(results,NutritionItemType.MONTH));
			map.put("whetherResultMap", ResultMaker.makeResultMap(results,NutritionItemType.WHETHER));
			
			map.put("patientAge",CommonUtils.getAge(currentUser.getResno()));
		}catch(Exception e){
			map.addAttribute("error",e.getMessage());
		}
		
		map.put("previewFlag", true);
		map.put("nurseViewFlag", true);
		
		return "/member/checkup/nutrition/secondDepth";
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
	
	private Map<String,List<FamilySubItem>> makeFamilySubItems(Question topQuestion,User currentUser,CheckupInstance instance){
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
	/**
	 * 페이지 네비게이션 정보를 얻어온다.
	 * @param currentGroup
	 * @param groups
	 * @return
	 */
	private NavigationInfo makeNavigation(QuestionGroup currentGroup,List<QuestionGroup> groups,User user,boolean skipNutrition,String patientNo,String instanceId){
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
			navi.setPreUrl("/nurse/view/" + patientNo + "?questionGroupId=" + preGroup.getId() + "&instanceId="+instanceId);
		}
		
		if(navi.isLastQuestionGroupInCategory()){
			//다음 카테고리를 얻어와서 해당 카테고리의 맨 처음으로 보낸다.
			navi.setLastQuestionGroup(true);
		}else{
			navi.setNextUrl("/nurse/view/" + patientNo + "?questionGroupId=" + nextGroup.getId() + "&instanceId="+instanceId);
		}
		
		return navi;
	}
	
	
	@RequestMapping(value="/doSubmit",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String doSubmit(@ModelAttribute("questionSubmitModel") QuestionSubmitModel submitModel,
			BindingResult result) throws IOException{
		CheckupInstance instance = service.loadInstance(submitModel.getInstanceId());
		User currentUser = instance.getOwner();
		
		try{
			service.doSubmitFromNurse(submitModel, currentUser);
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
	 * result를 inactive처리하고 ocs result도 미결코드로 전환한다.
	 * 
	 * @param itemId
	 * @param questionId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/doCancel",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String doCancel(@RequestParam("questionItemId") String itemId,
			@RequestParam("questionId") String questionId,
			@RequestParam("type") String type,
			@RequestParam("instanceId") String instanceId
			) throws IOException{
		
		CheckupInstance instance = service.loadInstance(instanceId);
		User currentUser = instance.getOwner();
		
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		try{
			submitModel.setInstanceId(instanceId);
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
	
	@RequestMapping(value="/syncQuestionGroup/{questionGroupId}",produces = "application/json;charset=utf-8")
	@ResponseBody
	public String syncQuestionGroup(
			@PathVariable("questionGroupId") String questionGroupId,
			@RequestParam("instanceId") String instanceId) throws IOException{
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		CheckupInstance instance = service.loadInstance(instanceId);
		User currentUser = instance.getOwner();
		
		QuestionGroup group = masterService.loadQuestionGroup(questionGroupId);
		
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
	 * 간호사 화면에서 간호사가 체크한것을 업데이트한다.
	 * @param instanceId
	 * @param questionId
	 * @param embededItemId
	 * @param result
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/updateNurseCheck",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updateNurseCheck(@RequestParam("instanceId") String instanceId,
			@RequestParam("questionId") String questionId,
			@RequestParam(value="embededItemId",required=false) String embededItemId) throws IOException{
		QuestionSubmitModel model = new QuestionSubmitModel();
		try{
			service.updateNurseCheck(instanceId, questionId, embededItemId);
			model.setSuccess(true);
		}catch(Exception e){
			log.error("doSubmitError : ",e);
			model.setMsg(e.getMessage());
			model.setSuccess(false);
		}
		
		String json = CommonUtils.toJson(model);
		return json;
	}
	
	@RequestMapping(value="/calculateCalory",produces = "application/json;charset=utf-8")
	@ResponseBody
	public String calculateCalory(@RequestParam("instanceId") String instanceId){
		
		CheckupInstance instance = service.loadInstance(instanceId);
		
		User currentUser = instance.getOwner();
		
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
	
}
