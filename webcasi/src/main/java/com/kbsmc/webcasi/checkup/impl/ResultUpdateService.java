package com.kbsmc.webcasi.checkup.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceDAO;
import com.kbsmc.webcasi.checkup.ICheckupMasterDAO;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.checkup.IResultUpdateService;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.InstanceProgress;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionEmbeddedItem;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.identity.impl.UserService;
import com.kbsmc.webcasi.ui.QuestionSubmitModel;

@Service
public class ResultUpdateService implements IResultUpdateService {
	@Autowired private ICheckupInstanceDAO dao;
	@Autowired private ICheckupMasterDAO masterDao;
	@Autowired private ICheckupMasterService masterService;
	@Autowired private IUserDAO userDao;
	@Autowired private IUserService userService;
	
	/**
	 * 해당 값을 변경하고 이 값의 변경에 의해서 ocs에 영향을 받아야할 모든 result를 리스트로 만들어서 리턴한다.
	 * 즉. 하위에 2,3단계 질문들이 영향을 받아서 미결코드로 처리되야하는 경우등을 말한다.
	 */
	@Override
	public List<QuestionResult> updateResult(User user, CheckupInstance instance,
			QuestionSubmitModel submitModel) throws Exception {
		Question question = masterDao.loadQuestion(submitModel.getQuestionId());
	
		QuestionType type = QuestionType.valueOf(submitModel.getType());
		boolean isCreate = false;
		
		QuestionResult result = dao.findResult(user, instance, question,submitModel.getQuestionItemId(),submitModel.getItemGroup());
		if(result == null){
			result = new QuestionResult();
			result.setOwner(user);
			result.setQuestion(question);
			result.setInstacne(instance);
			
			isCreate = true;
		}
		
		List<QuestionResult> results = new ArrayList<QuestionResult>();
		results.add(result);
		
		switch(type){
		case CHECK:
		case CHECK_VER:
		case CHECK_SUBJ:
		case CHECK_SUBJ_SUBJ:
		case CHECK_SUBJ_1:
		case CHECK_SUBJ_RADIO_SUBJ:
			results.addAll(updateCheckValue(result, submitModel));
			break;
		case RADIO:
		case RADIO_HOR:
		case RADIO_IMAGE:
		case RADIO_RADIO:
		case RADIO_SUBJ_1:
		case RADIO_SUBJ_HOUR_MINUTE:
			results.addAll(updateRadioValue(result, submitModel));
			break;
		case OBJ_RADIO:
		case OBJ_RADIO_SUBJ:
			results.addAll(updateObjValue(result, submitModel));
			break;
		case SUBJECTIVE:
		case SUBJECTIVE_YEAR:
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_MONTH_DATE_RANGE:
		case SUBJECTIVE_YEAR_MONTH:
		case SUBJECTIVE_YEAR_MONTH_DAY:
		case SUBJECTIVE_YEAR_MONTH_RANGE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:
		case TEXT_AREA:
			updateSubValue(result, submitModel);
			break;
		}
		
		if(isCreate){
			dao.createResult(result);
			if(isNavigationEffect(question)){
				updateTotalCount(instance,question,submitModel);
			}
			if(question.isRequired())
				updateProgress(result,true);
			else if(question.getDepth() == 1){
				updateLastQuestion(result);
			}
		}else{
			if(isNavigationEffect(question)){
				updateTotalCount(instance,question,submitModel);
				if(question.isRequired())
					updateProgress(result,true);
			}
			dao.saveResult(result);
		}
		
		return results;
	}

	/**
	 * 질문의 유형이 radio와 checkbox유형일때에만 처리한다.
	 * 하위 질문도 찾아서 inactive처리한다.
	 * 
	 */
	@Transactional
	@Override
	public List<QuestionResult> cancelResult(User user, CheckupInstance instance,
			QuestionSubmitModel submitModel) throws Exception {
		List<QuestionResult> results = new ArrayList<QuestionResult>();
		
		Question question = masterDao.loadQuestion(submitModel.getQuestionId());
		
		QuestionType type = QuestionType.valueOf(submitModel.getType());
		
		if(type.equals(QuestionType.TAB)){
			//탭 질문이면 해당 질문의 하위의 모든 답변을 다 초기화한다.
			List<QuestionResult> childResults = dao.findResult(user, instance, question, null);
			for(QuestionResult result : childResults){
				result.setActive(false);
				dao.saveResult(result);
				results.add(result);
			}
			
			for(QuestionItem item : question.getChildItems()){
				if(item.isActive() == false) continue;
				if(item.isExistChildQuestion()){
					//result가 inactive가 되면서 하위 질문이 존재하면 하위 질문의 result값도 다 inactive처리를 해야한다.
					results.addAll(clearChildQuestions(user,instance,item));
				}
			}
			
		}else{
			
			QuestionResult result = dao.findResult(user, instance, question,submitModel.getQuestionItemId(),submitModel.getItemGroup());
			if(result != null){
				switch(type){
				case CHECK:
				case CHECK_VER:
				case CHECK_SUBJ:
				case CHECK_SUBJ_SUBJ:
				case CHECK_SUBJ_1:
				case CHECK_SUBJ_RADIO_SUBJ:
					result.setActive(false);
					break;
				case RADIO:
				case RADIO_HOR:
				case RADIO_IMAGE:
				case RADIO_RADIO:
				case RADIO_SUBJ_1:
				case RADIO_SUBJ_HOUR_MINUTE:
					result.setActive(false);
				}
				dao.saveResult(result);
				
				if(question.isRequired())
					updateProgress(result,false);
				
			}
			
			results.add(result);
			
			if(submitModel.getQuestionItemId() != null && !"".equals(submitModel.getQuestionItemId())){
				QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
				
				if(item.isExistChildQuestion()){
					//result가 inactive가 되면서 하위 질문이 존재하면 하위 질문의 result값도 다 inactive처리를 해야한다.
					if(result != null)
						results.addAll(clearChildQuestions(result.getOwner(),result.getInstacne(),item));
				}
			
			}
		}
		
		return results;
	}


	private void updateTotalCount(CheckupInstance instance,Question question,QuestionSubmitModel submitModel){
		boolean skipNutrition = false;
		if(question.isNavigationNutritionFlag()){
			skipNutrition = skipNutrition(question,submitModel);
		}else{
			skipNutrition = instance.isSkipNutrition();
		}
		boolean skipStress = false;
		if(question.isNavigationStressFlag()){
			skipStress = skipStress(question,submitModel);
		}else{
			skipStress = instance.isSkipStress();
		}
		
		int totalCount = countTotalQuestion(instance.getMaster(), instance.getOwner(),skipNutrition, skipStress(question,submitModel),instance);
		
		instance.setSkipNutrition(skipNutrition);
		instance.setSkipStress(skipStress);
		instance.setTotalQuestionCount(totalCount);
		dao.saveInstance(instance);
	}
	

	private boolean skipNutrition(Question question,QuestionSubmitModel submitModel){
		if(question.isNavigationNutritionFlag()){
			QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
			if(item.getSortOrder() == 0){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	
	private boolean skipStress(Question question,QuestionSubmitModel submitModel){
		if(question.isNavigationStressFlag()){
			QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
			if(item.getSortOrder() == 0){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	
	private boolean isNavigationEffect(Question question){
		return question.isNavigationNutritionFlag() || question.isNavigationStressFlag() ? true : false;
	}
	

	
	private List<QuestionResult> updateCheckValue(QuestionResult result,QuestionSubmitModel submitModel){
		List<QuestionResult> results = new ArrayList<QuestionResult>();
		
		QuestionType type = QuestionType.valueOf(submitModel.getType());
		result.setType(type);
		result.setObjectiveValue(submitModel.getQuestionItemId());
		result.setActive(submitModel.getOnOffFlag().equals("on") ? true : false);
		boolean allCompleted = false;
		if(submitModel.getItemGroup() != null && !"".equals(submitModel.getItemGroup())){
			result.setItemGroup(NutritionItemType.valueOf(submitModel.getItemGroup()));
		}
		
		QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
		
		switch(type){
		case CHECK:
		case CHECK_VER:
			allCompleted = true;
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			
			if(result.isActive())
				result.setOcsValue(item.getOcsAnswer());
			else
				result.setOcsValue(item.getOcsNoAnswerCode());
			break;
		case CHECK_SUBJ:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsAskCode2(item.getOcsAskCode2());
			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			
			if(result.isActive()){
				result.setOcsValue(item.getOcsAnswer());
				if(submitModel.getStrValue() != null){
					result.setStrValue(submitModel.getStrValue());
					//if(!"".equals(result.getStrValue()))
				}
				//체크 + 주관식일 경우에는 우측에 text에 값이 없어도 완료처리됨.
				allCompleted = true;
			}else{
				result.setOcsValue(item.getOcsNoAnswerCode());
				result.setStrValue(item.getOcsNoAnswerCode2());
				allCompleted = true;
				
			}
			
			break;
		case CHECK_SUBJ_1:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsAskCode2(item.getOcsAskCode2());
			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			
			if(result.isActive()){
				result.setOcsValue(item.getOcsAnswer());
				if(submitModel.getStrValue() != null){
					result.setStrValue(submitModel.getStrValue());
					if(!"".equals(result.getStrValue()))
						allCompleted = true;
				}
			}else{
				result.setOcsValue(item.getOcsNoAnswerCode());
				result.setStrValue(item.getOcsNoAnswerCode2());
				allCompleted = true;
			}
			break;			
		case CHECK_SUBJ_SUBJ:
			result.setOcsAskCode(item.getOcsAskCode());		//check
			result.setOcsAskCode2(item.getOcsAskCode2());	//subj1
			result.setOcsAskCode3(item.getOcsAskCode3());	//subj2
			
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2()); //subj1
			result.setOcsNoAnswerCode2(item.getOcsNoAnswerCode3()); //subj2
			
			if(result.isActive()){
				result.setOcsValue(item.getOcsAnswer());		//check value
				if(submitModel.getStrValue() != null){
					result.setStrValue(submitModel.getStrValue());
				}
				if(submitModel.getStrValue2() != null){
					result.setStrValue2(submitModel.getStrValue2());
				}
				if(result.getStrValue() != null && result.getStrValue2() != null && !"".equals(result.getStrValue()) && !"".equals(result.getStrValue2())){
					allCompleted = true;
				}
			}else{
				result.setOcsValue(item.getOcsNoAnswerCode());
				result.setStrValue(item.getOcsNoAnswerCode2());
				result.setStrValue2(item.getOcsNoAnswerCode3());
				allCompleted = true;
				
			}
			break;
		case CHECK_SUBJ_RADIO_SUBJ:
			result.setOcsAskCode(item.getOcsAskCode());		//check
			result.setOcsAskCode2(item.getOcsAskCode2());	//subj1
			result.setOcsAskCode3(item.getOcsAskCode3());	//subj2
			
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2());	//subj1
			result.setOcsNoAnswerCode2(item.getOcsNoAnswerCode3()); //subj2
			
			if(result.isActive()){
				result.setOcsValue(item.getOcsAnswer());		//check value
				if(submitModel.getStrValue() != null){
					result.setStrValue(submitModel.getStrValue());
				}
				if(submitModel.getStrValue2() != null){
					result.setStrValue2(submitModel.getStrValue2());
				}
				if(submitModel.getEmbededItemId() != null){
					result.setObjectiveValue2(submitModel.getEmbededItemId());
					for(QuestionEmbeddedItem eItem : item.getChildItems()){
						if(eItem.getKey().equals(submitModel.getEmbededItemId())){
							result.setOcsAskCode4(eItem.getOcsAskCode());
							result.setOcsValue2(eItem.getOcsAnswer());
							result.setOcsObjectiveNoAnswerCode2(eItem.getOcsNoAnswerCode());
						}
					}
				}
				if(result.getStrValue() != null && result.getStrValue2() != null && result.getObjectiveValue2() != null &&
						!"".equals(result.getStrValue()) && !"".equals(result.getStrValue2()) && !"".equals(result.getObjectiveValue2())){
					allCompleted = true;
				}
			}else{
				result.setOcsValue(item.getOcsNoAnswerCode());
				result.setStrValue(item.getOcsNoAnswerCode2());
				result.setStrValue2(item.getOcsNoAnswerCode3());
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					if(eItem.getKey().equals(submitModel.getEmbededItemId())){
						result.setOcsAskCode4(eItem.getOcsAskCode());
						result.setOcsValue2(eItem.getOcsNoAnswerCode());
						result.setOcsObjectiveNoAnswerCode2(eItem.getOcsNoAnswerCode());
					}
				}
			}
			break;
		
		}
		
		submitModel.setAllCompleted(allCompleted);
		
		if(result.isActive() == false && item.isExistChildQuestion()){
			//result가 inactive가 되면서 하위 질문이 존재하면 하위 질문의 result값도 다 inactive처리를 해야한다.
			results.addAll(clearChildQuestions(result.getOwner(),result.getInstacne(),item));
		}
		
		return results;
	}
	
	private List<QuestionResult> updateRadioValue(QuestionResult result,QuestionSubmitModel submitModel){
		List<QuestionResult> results = new ArrayList<QuestionResult>();
		QuestionType type = QuestionType.valueOf(submitModel.getType());
		result.setType(type);
		result.setObjectiveValue(submitModel.getQuestionItemId());
		
		//Radio이면 같은 Question에 해당하는 답변이 이미 존재하면 해당 답변을 inactive처리를 먼저 한다.
		results.addAll(inactiveAllResult(result.getOwner(), result.getInstacne(),result.getQuestion(), result, submitModel.getItemGroup()));
		
		result.setActive(true);
		if(submitModel.getItemGroup() != null && !"".equals(submitModel.getItemGroup())){
			result.setItemGroup(NutritionItemType.valueOf(submitModel.getItemGroup()));
		}
		QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
		
		boolean allCompleted = false;
		
		switch(type){
		case RADIO:
		case RADIO_HOR:
		case RADIO_IMAGE:
			allCompleted = true;
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsValue(item.getOcsAnswer());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			break;
		case RADIO_RADIO:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsValue(item.getOcsAnswer());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			if(submitModel.getEmbededItemId() != null){
				result.setObjectiveValue2(submitModel.getEmbededItemId());
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					if(eItem.getKey().equals(submitModel.getEmbededItemId())){
						result.setOcsAskCode2(eItem.getOcsAskCode());
						result.setOcsValue2(eItem.getOcsAnswer());
						result.setOcsObjectiveNoAnswerCode2(eItem.getOcsNoAnswerCode());
					}
				}
				allCompleted = true;
			}
			break;
		case RADIO_SUBJ_1:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsValue(item.getOcsAnswer());
			result.setOcsAskCode2(item.getOcsAskCode2());
			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			
			if(submitModel.getStrValue() != null){
				result.setStrValue(submitModel.getStrValue());
				if(!"".equals(result.getStrValue()))
					allCompleted = true;
			}
			break;
		case RADIO_SUBJ_HOUR_MINUTE:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsValue(item.getOcsAnswer());
			result.setOcsAskCode2(item.getOcsAskCode2());
			result.setOcsAskCode3(item.getOcsAskCode3());
			
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2());
			result.setOcsNoAnswerCode2(item.getOcsNoAnswerCode3());
			
			
			if((submitModel.getStrValue() == null || "".equals(submitModel.getStrValue())) && 
					(result.getStrValue() == null || "".equals(result.getStrValue())) ){
				submitModel.setStrValue("00");	//여기서는 시간의 값은 default로 00으로 입력되게 한다.
			}
			
			if(submitModel.getStrValue() != null)
				result.setStrValue(submitModel.getStrValue());
			if(submitModel.getStrValue2() != null)
				result.setStrValue2(submitModel.getStrValue2());
			if(result.getStrValue() != null && result.getStrValue2() != null &&
					!"".equals(result.getStrValue()) && !"".equals(result.getStrValue2())){
				allCompleted = true;
			}
			break;
		}
		submitModel.setAllCompleted(allCompleted);
		
		return results;
		
	}
	
	private List<QuestionResult> updateObjValue(QuestionResult result,QuestionSubmitModel submitModel){
		List<QuestionResult> results = new ArrayList<QuestionResult>();
		QuestionType type = QuestionType.valueOf(submitModel.getType());
		result.setType(type);
		result.setObjectiveValue(submitModel.getQuestionItemId());
		
		//results.addAll(inactiveAllResult(result.getOwner(), result.getInstacne(),result.getQuestion(), result, submitModel.getItemGroup()));
		
		result.setActive(true);
		boolean allCompleted = false;
		
		QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
		
		switch(type){
		case OBJ_RADIO:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsValue(item.getOcsAnswer());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			
			if(submitModel.getEmbededItemId() != null){
				result.setObjectiveValue2(submitModel.getEmbededItemId());
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					if(eItem.getKey().equals(submitModel.getEmbededItemId())){
						result.setOcsAskCode2(eItem.getOcsAskCode());
						result.setOcsValue2(eItem.getOcsAnswer());
						result.setOcsObjectiveNoAnswerCode2(eItem.getOcsNoAnswerCode());
					}
				}
				allCompleted = true;
			}
			break;
		case OBJ_RADIO_SUBJ:
			result.setOcsAskCode(item.getOcsAskCode());
			result.setOcsValue(item.getOcsAnswer());
			result.setOcsAskCode2(item.getOcsAskCode2());	//subj

			result.setOcsNoAnswerCode(item.getOcsNoAnswerCode2());
			result.setOcsObjectiveNoAnswerCode(item.getOcsNoAnswerCode());
			
			if(submitModel.getEmbededItemId() != null){
				result.setObjectiveValue2(submitModel.getEmbededItemId());
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					if(eItem.getKey().equals(submitModel.getEmbededItemId())){
						result.setOcsAskCode3(eItem.getOcsAskCode());
						result.setOcsValue2(eItem.getOcsAnswer());
						result.setOcsObjectiveNoAnswerCode2(eItem.getOcsNoAnswerCode());
					}
				}
			}
			if(submitModel.getStrValue() != null)
				result.setStrValue(submitModel.getStrValue());
			
			if(result.getObjectiveValue2() != null && result.getStrValue() != null &&
					!"".equals(result.getObjectiveValue2()) && !"".equals(result.getStrValue())){
				allCompleted = true;
			}
			break;
		}
		
		submitModel.setAllCompleted(allCompleted);
		
		return results;
	}
	
	private void updateSubValue(QuestionResult result,QuestionSubmitModel submitModel){
		QuestionType type = QuestionType.valueOf(submitModel.getType());
		result.setType(type);
		result.setActive(true);
		if(submitModel.getItemGroup() != null && !"".equals(submitModel.getItemGroup())){
			result.setItemGroup(NutritionItemType.valueOf(submitModel.getItemGroup()));
		}
		boolean allCompleted = false;
		
		switch(type){
		case SUBJECTIVE:
		case TEXT_AREA:
			if(submitModel.getQuestionItemId() != null && !"".equals(submitModel.getQuestionItemId())){
				result.setObjectiveValue(submitModel.getQuestionItemId());
				QuestionItem item = masterDao.loadQuestionItem(submitModel.getQuestionItemId());
				result.setOcsAskCode(item.getOcsAskCode());
				result.setOcsNoAnswerCode(item.getOcsNoAnswerCode());
			}else{
				result.setOcsAskCode(result.getQuestion().getOcsAskCode());
				result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			}
			
			result.setStrValue(submitModel.getStrValue());
			
			
			if(!"".equals(result.getStrValue()) || !result.getQuestion().isRequired())
				allCompleted = true;
			break;
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:			
			result.setOcsAskCode(result.getQuestion().getOcsAskCode());
			result.setOcsAskCode2(result.getQuestion().getOcsAskCode2());
			result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			result.setOcsNoAnswerCode2(result.getQuestion().getOcsNoAnswerCode2());
			
			if((submitModel.getStrValue2() == null || "".equals(submitModel.getStrValue2())) &&
					(result.getStrValue2() == null || "".equals(result.getStrValue2()))){
				submitModel.setStrValue2("00");	//분의 값은 default로 00으로 입력되게 한다.
			}
			
			if(submitModel.getStrValue() != null)
				result.setStrValue(submitModel.getStrValue());
			if(submitModel.getStrValue2() != null)
				result.setStrValue2(submitModel.getStrValue2());
			
			if(result.getStrValue() != null && result.getStrValue2() != null &&
					!"".equals(result.getStrValue()) && !"".equals(result.getStrValue2())){
				allCompleted = true;
			}
			
			if(!result.getQuestion().isRequired()){
				allCompleted = true;
			}
			break;
		case SUBJECTIVE_MONTH_DATE_RANGE:
			result.setOcsAskCode(result.getQuestion().getOcsAskCode());
			result.setOcsAskCode2(result.getQuestion().getOcsAskCode2());
			result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			result.setOcsNoAnswerCode2(result.getQuestion().getOcsNoAnswerCode2());
			
			if(submitModel.getStrValue() != null)
				result.setStrValue(submitModel.getStrValue());
			if(submitModel.getStrValue2() != null)
				result.setStrValue2(submitModel.getStrValue2());
			
			if(result.getStrValue() != null && result.getStrValue2() != null &&
					!"".equals(result.getStrValue()) && !"".equals(result.getStrValue2())){
				allCompleted = true;
			}
			if(!result.getQuestion().isRequired()){
				allCompleted = true;
			}
			break;			
		case SUBJECTIVE_YEAR:
			result.setOcsAskCode(result.getQuestion().getOcsAskCode());
			result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			
			allCompleted = true;
			result.setStrValue(submitModel.getStrValue());
			if(!result.getQuestion().isRequired()){
				allCompleted = true;
			}
			break;
		case SUBJECTIVE_YEAR_MONTH:
			allCompleted = true;
			result.setOcsAskCode(result.getQuestion().getOcsAskCode());
			result.setOcsAskCode2(result.getQuestion().getOcsAskCode2());
			result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			result.setOcsNoAnswerCode2(result.getQuestion().getOcsNoAnswerCode2());
			
			result.setDateValue(submitModel.getDateValue());
			if(!result.getQuestion().isRequired()){
				allCompleted = true;
			}
			break;
		case SUBJECTIVE_YEAR_MONTH_DAY:
			allCompleted = true;
			result.setOcsAskCode(result.getQuestion().getOcsAskCode());
			result.setOcsAskCode2(result.getQuestion().getOcsAskCode2());
			result.setOcsAskCode3(result.getQuestion().getOcsAskCode3());
			
			result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			result.setOcsNoAnswerCode2(result.getQuestion().getOcsNoAnswerCode2());
			result.setOcsNoAnswerCode3(result.getQuestion().getOcsNoAnswerCode3());
			
			
			result.setDateValue(submitModel.getDateValue());
			if(!result.getQuestion().isRequired()){
				allCompleted = true;
			}
			break;		
		case SUBJECTIVE_YEAR_MONTH_RANGE:
			result.setOcsAskCode(result.getQuestion().getOcsAskCode());
			result.setOcsAskCode2(result.getQuestion().getOcsAskCode2());
			result.setOcsNoAnswerCode(result.getQuestion().getOcsNoAnswerCode());
			result.setOcsNoAnswerCode2(result.getQuestion().getOcsNoAnswerCode2());
			
			if(submitModel.getStrValue() != null)
				result.setStrValue(submitModel.getStrValue());
			if(submitModel.getStrValue2() != null)
				result.setStrValue2(submitModel.getStrValue2());
			
			if(result.getStrValue() != null && result.getStrValue2() != null &&
					!"".equals(result.getStrValue()) && !"".equals(result.getStrValue2())){
				allCompleted = true;
			}
			if(!result.getQuestion().isRequired()){
				allCompleted = true;
			}
			break;
		}
		submitModel.setAllCompleted(allCompleted);
	}
	

	/**
	 * 다른 result를 초기화할때에 하위 질문들이 존재하면 그 하위 질문들도 다 초기화해야함.
	 * 여기서는 radio에 대해서만 처리한다.
	 *  List<QuestionResult> 를 리턴해서 ocsWebDb의 값도 update될수 있도록 한다.
	 *  주의할것은 radio유형의 경우에 ocsCode값이 같기 때문에 return결과값에 포함시키는부분을 고려해야한다.
	 * 
	 * @param user
	 * @param instance
	 * @param question
	 * @param exclusiveResult
	 * @param itemGroup
	 */
	@Transactional
	private List<QuestionResult> inactiveAllResult(User user,CheckupInstance instance,Question question,QuestionResult exclusiveResult, String itemGroup){
		List<QuestionResult> inactiveResults = new ArrayList<QuestionResult>();
		List<QuestionResult> results = dao.findResult(user,instance, question, exclusiveResult);
		
		Map<String,QuestionResult> resultMap = new HashMap<String,QuestionResult>();
		
		for(QuestionResult result : results){
			if(itemGroup != null){
				NutritionItemType itemGroupType = NutritionItemType.valueOf(itemGroup);
				if(itemGroupType.equals(result.getItemGroup())){
					result.setActive(false);
					dao.saveResult(result);
				}
			}else{
				result.setActive(false);
				dao.saveResult(result);
			}
			if(result.getObjectiveValue() != null && !"".equals(result.getObjectiveValue()))
				resultMap.put(result.getObjectiveValue(), result);
		}
		
		//라디오의 경우에는 ocsCode가 같은 질문에 속한 item끼리는 같기 때문에 return result에서는 제외시킨다.
		switch(question.getType()){
		case CHECK:
		case CHECK_SUBJ:
		case CHECK_SUBJ_1:
		case CHECK_SUBJ_SUBJ:
		case CHECK_VER:
			inactiveResults.addAll(results);
			break;
		case RADIO_SUBJ_1:
		case RADIO_SUBJ_HOUR_MINUTE:
		case OBJ_RADIO_SUBJ :
		case OBJ_RADIO:
			//라디오에 또 다른 주관식값이 설정되어있는 경우에는 직접 OCS의 값을 여기서 초기화시켜준다.
			//그 이유는 ocsCode가 같은값이 다시 초기화되는 문제가 있기 때문이다.
			for(QuestionResult result : results){
				result.setIgnoreUpdateObjectiveValue(true);	//OCS WebDb에 저장시에 objectiveValue는 update안하게 한다.
				inactiveResults.add(result);
			}
			break;
		}
		
		
		for(QuestionItem item : question.getChildItems()){
			if(item.isActive() == false) continue;
			//하위 질문이 존재할경우 그 질문도 초기화함.
			//그중에서 QuestionResult에 있는 값들만 하위질문을 초기화한다.(기존에 있었던것들만 처리한다.)
			if(item.isExistChildQuestion() && !item.getId().equals(exclusiveResult.getObjectiveValue()) 
					&& resultMap.containsKey(item.getId())){
				if(itemGroup != null){
					NutritionItemType itemGroupType = NutritionItemType.valueOf(itemGroup);
					if(itemGroupType.equals(item.getItemGroup())){
						inactiveResults.addAll(clearChildQuestions(user, instance, item));
					}
				}else{
					inactiveResults.addAll(clearChildQuestions(user, instance, item));
				}
			}
			
		}
		
		return inactiveResults;
	}
	
	/**
	 * 해당 instance에서 해당 item하위에 존재하는 모든 질문의 result를 inactive처리한다.
	 * 질문 유형이 subjective관련된 부분이 있으면 다 null로 바꾸어준다.
	 * @param user
	 * @param instance
	 * @param item
	 * @return
	 */
	@Transactional
	private List<QuestionResult> clearChildQuestions(User user,CheckupInstance instance,QuestionItem item){
		List<QuestionResult> inactiveResults = new ArrayList<QuestionResult>();
		
		int currentDepth = item.getParentQuestion().getDepth();
		
		if(currentDepth == 1){
			inactiveResults.addAll(clearQuestionResults(user,instance,item.getParentQuestion().getParentGroup(),2,item,true));
			inactiveResults.addAll(clearQuestionResults(user,instance,item.getParentQuestion().getParentGroup(),3,item,false));
		}else if(currentDepth == 2){
			inactiveResults.addAll(clearQuestionResults(user,instance,item.getParentQuestion().getParentGroup(),3,item,true));
		}
		
//		List<Question> childQuestions = masterDao.findChildQuestions(item.getId(), user.getGender(), currentDepth+1);
//		
//		for(Question child : childQuestions){
//			if(child.isActive() == false) continue;
//			//여기서 해당 질문이 있는 depth의 하위모든 질문결과를 받아서 다 초기화한다.
//			
//			inactiveResults.addAll(clearChildQuestions(user,instance,child));
//		}
//		
		return inactiveResults;
	}
	
	private List<QuestionResult> clearQuestionResults(User user,CheckupInstance instance,QuestionGroup group,int depth,QuestionItem parentItem,boolean direct){
		List<QuestionResult> inactiveResults = new ArrayList<QuestionResult>();
		List<QuestionResult> allResults = dao.findResult(user, instance, group, depth);
		
		for(QuestionResult result : allResults){
			if(isChild(result,parentItem,depth,direct)){
				//해당 item에 직접적으로 속해있는 질문들만 초기화한다. 
				result.setActive(false);
				result.setStrValue(null);
				result.setStrValue2(null);
				result.setDateValue(null);
				dao.saveResult(result);
			}
		}
		inactiveResults.addAll(allResults);
		return inactiveResults;
	}
	
	private boolean isChild(QuestionResult result,QuestionItem parentItem,int depth,boolean direct){
		
		if(direct){
			List<Question> questions = masterDao.findChildQuestions(parentItem.getId(), result.getOwner().getGender(), depth);
			if(questions.contains(result.getQuestion())){
				return true;
			}
		}else{
			List<Question> questions = masterDao.findChildQuestions(parentItem.getId(), result.getOwner().getGender(), depth-1);
			for(Question question : questions){
				for(QuestionItem childItem : question.getChildItems()){
					List<Question> subQuestions = masterDao.findChildQuestions(childItem.getId(), result.getOwner().getGender(), depth);
					if(subQuestions.contains(result.getQuestion())){
						return true;
					}
				}
			}
		}
//		
//		if(depth == 2){
//			if((result.getQuestion().getParentItems() != null && result.getQuestion().getParentItems().contains(parentItem.getId())) ||
//					(result.getQuestion().getParentQuestion() != null && result.getQuestion().getParentQuestion().equals(parentItem.getParentQuestion()))){
//				return true;
//			}
//		}else{
//			List<Question> questions = masterDao.findChildQuestions(parentItem.getId(), result.getOwner().getGender(), depth);
//			if(questions.contains(result.getQuestion())){
//				return true;
//			}
//		}
		return false;
	}
	
	private List<QuestionResult> clearChildQuestions(User user,CheckupInstance instance,Question child){
		List<QuestionResult> inactiveResults = new ArrayList<QuestionResult>();
		
		if(child.getType().equals(QuestionType.TABLE)){
			//테이블 유형일 경우에는 해당 질문그룹에 속한 모든 질문의 답변을 한번에 초기화한다.
			List<QuestionResult> allResults = dao.findResult(user, instance, child.getParentGroup(), 0);
			for(QuestionResult aResult : allResults){
				aResult.setActive(false);
				aResult.setStrValue(null);
				aResult.setStrValue2(null);
				aResult.setDateValue(null);
				dao.saveResult(aResult);
			}
			inactiveResults.addAll(allResults);
			return inactiveResults;
		}
		
		QuestionResult qResult = dao.findResult(user, instance, child,null,null);
		if(qResult != null){
			qResult.setActive(false);
			qResult.setStrValue(null);
			qResult.setStrValue2(null);
			qResult.setDateValue(null);
			dao.saveResult(qResult);
			inactiveResults.add(qResult);
		}
		
		for(QuestionItem subItem : child.getChildItems()){
			if(subItem.isActive() == false) continue;
			String itemGroup = subItem.getItemGroup() == null ? null : subItem.getItemGroup().name();
			QuestionResult result = dao.findResult(user, instance, child,subItem.getId(),itemGroup);
			if(result != null){
				result.setActive(false);
				result.setStrValue(null);
				result.setStrValue2(null);
				result.setDateValue(null);
				dao.saveResult(result);
				inactiveResults.add(result);
				
				if(subItem.isExistChildQuestion()){
					inactiveResults.addAll(clearChildQuestions(user, instance, subItem));
				}
			}
		}	
		
		//Tab처럼 질문에 질문이 붙어있는 경우도 처리한다.
		for(Question subQuestion : child.getChildQuestions()){
			if(subQuestion.isActive() == false) continue;
			inactiveResults.addAll(clearChildQuestions(user,instance,subQuestion));
		}
		
		return inactiveResults;
	}
	
	/**
	 * 이때에 각 카테고리별로 진행여부를 확인해서 surempt테이블에 진도율을 업데이트한다.
	 */
	@Transactional
	@Override
	public void updateLastQuestion(QuestionResult result) {
		CheckupInstance instance = result.getInstacne();
		//기존에 저장되어있는 질문의 마지막 질문그룹과 카테고리를 보고 업데이트할수 있을지 결정해서 업데이트한다.
		if(compareQuestionId(instance.getLastQuestionId(),result.getQuestion().getId()) > 0){
			instance.setLastQuestionId(result.getQuestion().getId());
			//해당 질문이 소속 카테고리의 마지막 질문그룹의 마지막질문인지 여부를 확인한다.
			if(masterService.isLastQuestionInCategory(result.getQuestion(),result.getOwner())){
				Category category = result.getQuestion().getParentGroup().getCategory();
				int progressValue = -1;
				switch(category.getType()){
				case CHECK_LIST : 
					progressValue = 1;
					break;
				case HEALTH_CHECKUP:
					progressValue = 2;
					break;
				case MENTAL_HEALTH:
					progressValue = 3;
					break;
				case NUTRITION :
					progressValue = 4;
					break;
				case HEALTH_AGE:
					progressValue = 5;
					break;
				case SLEEP:
					progressValue = 6;
					break;
				case STRESS:
					progressValue = 7;
					break;
				}
				if(progressValue != -1){
					userDao.updateOcsProgress(result.getInstacne().getPatno(), result.getInstacne().getReserveDate(), progressValue);
				}
			}
			
		}
	}

	
	
	
	/**
	 * 진도율을 계산해서 업데이트한다.
	 * 1Depth질문에 대한 대답일때에만 진도율을 업데이트한다.
	 * 그 이외일경우에는 그냥 마지막 질문만 업데이트한다.
	 * @param result
	 * @return
	 */
	public void updateProgress(QuestionResult result,boolean plus){
		CheckupInstance instance = result.getInstacne();
		if(instance.getStatus().equals(InstanceStatus.FIRST_COMPLETED) || 
				instance.getStatus().equals(InstanceStatus.COMPLETED)){
			return;
		}
		
		User owner = result.getOwner();
		if(result.getQuestion().getDepth() == 1){
			List<InstanceProgress> resultIds = instance.getUserResultProgress();
			if(resultIds == null){
				resultIds = new ArrayList<InstanceProgress>();
				instance.setUserResultProgress(resultIds);
			}
			
			boolean isExist = false;
			
			for(InstanceProgress p : resultIds){
				if(p.getQuestionId().equals(result.getQuestion().getId())){
					isExist = true;
					break;
				}	
			}
			
			if(!isExist){
				double totalCount = 0;
				//totalCount = instance.getTotalQuestionCount();	//동의서는 진도율에서 빠진다.
				totalCount = countTotalQuestion(instance.getMaster(),instance.getOwner(),instance.isSkipNutrition(), instance.isSkipStress(),instance);
				
				//currentCount를 SQL로 DB에서 얻어온다.
				double currentCount = dao.countRequiredResult(instance);
				//이미 DB에 반영이 되어있다.
//				if(plus){
//					currentCount = currentCount + 1;
//				}else{
//					currentCount = currentCount - 1;
//				}
				double t = currentCount/totalCount;
				int progress = (int)(t * 100);
				if(progress >= 100) progress = 100;
				instance.setProgress(progress);
				instance.setResultCount((int)currentCount);
				InstanceProgress newProgress = new InstanceProgress();
				newProgress.setInstance(instance);
				newProgress.setCreateDate(new Date());
				newProgress.setQuestionId(result.getQuestion().getId());
				
				instance.getUserResultProgress().add(newProgress);
			}
		}
		
		updateLastQuestion(result);
		
		if(instance.getProgress() == 100){
			instance.setStatus(InstanceStatus.FIRST_COMPLETED);
			
			
		}else{
			if(instance.getStatus().equals(InstanceStatus.READY)){
				instance.setStatus(InstanceStatus.IN_PROGRESS);
			}
		}
		
		dao.saveInstance(instance);
	}
	

	
	/**
	 * old와 new의 순서를 비교한다.
	 * 
	 * @param oldQuestionId
	 * @param newQuestionId
	 * @return old가 new보다 뒤에 존재하는 질문이면 -1, 앞에 존재하는 질문이면 1, 같으면 0
	 */
	private int compareQuestionId(String oldQuestionId,String newQuestionId){
		if(oldQuestionId == null) return 1;
		if(oldQuestionId.equals(newQuestionId)) return 0;
		
		Question oldQ = masterDao.loadQuestion(oldQuestionId);
		Question newQ = masterDao.loadQuestion(newQuestionId);
		QuestionGroup oldQGroup = oldQ.getParentGroup();
		QuestionGroup newQGroup = newQ.getParentGroup();
		Category oldCategory = oldQGroup.getCategory();
		Category newCategory = newQGroup.getCategory();
		
		if(oldCategory.equals(newCategory)){
			if(oldQGroup.equals(newQGroup)){
				//같은 그룹에 존재할경우.
				return oldQ.getSortOrder() > newQ.getSortOrder() ? -1 : 1;
			}else{
				return oldQGroup.getSortOrder() > newQGroup.getSortOrder() ? -1 : 1;
			}
		}else{
			return oldCategory.getSortOrder() > newCategory.getSortOrder() ? -1 : 1;
		}
	}
	
	/**
	 * 사용자의 상태와 영양설문 진행여부 그리고 스트레스 측정도구를 진행할지 여부에 따라서 1Depth 질문의 갯수를 가져온다.
	 */
	@Override
	public int countTotalQuestion(CheckupMaster master,User user, boolean skipNutrition,
			boolean skipStress,CheckupInstance instance) {
		//1.일단 해당 성별에 해당하는 모든 질문을 count한다.
		int totalCount = masterDao.countQuestions(master,user.getGender(), 1);
		
		//2.instance의 상태에 따라서 카테고리 스킵할 카테고리의 갯수를 구한다.
		if(!instance.isSamsungEmployee()){
			Category healthAge = masterDao.findUniqueCategory(master, CategoryType.HEALTH_AGE);
			int mentalCount = masterDao.countQuestions(master, user.getGender(), 1, healthAge);
			totalCount = totalCount - mentalCount;
		}
		if(!instance.isNeedSleepTest()){
			Category sleepCategory = masterDao.findUniqueCategory(master, CategoryType.SLEEP);
			int sleepCount = masterDao.countQuestions(master, user.getGender(), 1, sleepCategory);
			totalCount = totalCount - sleepCount;
		}
		if(!instance.isNeedStressTest()){
			Category stressCategory = masterDao.findUniqueCategory(master, CategoryType.STRESS);
			int stressCount = masterDao.countQuestions(master, user.getGender(), 1, stressCategory);
			totalCount = totalCount - stressCount;			
		}
		//3.스트레스 측정도구의 값에 따라서 스트레스 측정도구 페이지의 질문 갯수를 구한다.
		if(skipStress){
			List<QuestionGroup> sgList = masterDao.findStressTestGroup(master);
			for(QuestionGroup sg : sgList){
				if(!sg.isActive()) continue;
				int stressGroupCount = masterDao.countQuestions(master, user.getGender(), 1, sg);
				totalCount = totalCount - stressGroupCount;
			}
		}
		
		//4.영양설문 그룹 값에 따라서 영양설문 그룹의 질문 갯수를 구한다. 
		if(skipNutrition){
			Category nutritionCategory = masterDao.findUniqueCategory(master, CategoryType.NUTRITION);
			int nutritionCount = masterDao.countQuestions(master, user.getGender(), 1, nutritionCategory);
			totalCount = totalCount - nutritionCount;
		}
		
		
		
		
		return totalCount;
	}

	@Transactional
	@Override
	public void completeInstance(CheckupInstance instance) {
		instance.setStatus(InstanceStatus.FIRST_COMPLETED);
		instance.setProgress(100);
		
		dao.saveInstance(instance);
		
		userDao.updateOcsProgress(userService.findPatientNo(instance.getOwner()), instance.getReserveDate(), 0);
	}

	@Transactional
	@Override
	public void updateOcsWebstat(CheckupInstance instance, int progress) {
		userDao.updateOcsProgress(userService.findPatientNo(instance.getOwner()), instance.getReserveDate(), progress);
		
	}

	
}
