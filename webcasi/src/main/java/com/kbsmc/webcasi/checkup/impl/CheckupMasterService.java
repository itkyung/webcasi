package com.kbsmc.webcasi.checkup.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.CheckupMasterStatus;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.checkup.ICheckupMasterDAO;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.common.ClassClone;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.identity.entity.User;

@Service
public class CheckupMasterService implements ICheckupMasterService {
	
	@Autowired private ICheckupMasterDAO dao;

	@Transactional(readOnly=true)
	public Category findUniqueCategory(CheckupMaster master, CategoryType type) {
		return dao.findUniqueCategory(master, type);
	}

	@Transactional(readOnly=true)
	public List<QuestionGroup> findQuestionGroup(Category category,Gender gender) {
		return dao.findQuestionGroup(category,gender);
	}

	/**
	 * 해당 QuestionGroup에 속하는 질문들을 가져온다.
	 */
	@Transactional(readOnly=true)
	public List<Question> findQuestions(QuestionGroup group,int depth,Gender gender) {
		return dao.findQuestions(group, depth, gender);
	}

	@Transactional(readOnly=true)
	public QuestionGroup loadQuestionGroup(String groupId) {
		return dao.loadQuestionGroup(groupId);
	}

	@Transactional(readOnly=true)
	public List<Question> findChildQuestions(String questionItemId,
			Gender gender,int depth,boolean todayIsReserveDate) {
		if(todayIsReserveDate){
			return dao.findChildQuestions(questionItemId, gender,depth);
		}else{
			return dao.findOnlyPreChildQuestions(questionItemId, gender,depth);
		}
		
	}

	@Transactional(readOnly=true)
	@Override
	public QuestionItem loadQuestionItem(String questionItemId) {
		
		return dao.loadQuestionItem(questionItemId);
	}

	@Transactional(readOnly=true)
	@Override
	public CheckupMaster loadCheckupMaster(String masterId) {
		return dao.loadCheckupMaster(masterId);
	}
	
	@Transactional(readOnly=true)
	@Override
	public Category findNextCategory(QuestionGroup group,boolean isSamsung,boolean skipNutrition,boolean needSleepTest,boolean needStessTest) {
		Category current = group.getCategory();
		Category next = null;
		List<Category> categories = dao.findCategory(group.getMaster());
		for(int i=0; i < categories.size(); i++){
			Category row = categories.get(i);
			if(row.getId().equals(current.getId())){
				if(i == categories.size()-1){
					next = null;
				}else{
					next = categories.get(i+1);
					if(!isSamsung && next.getType().equals(CategoryType.HEALTH_AGE)){
						//삼성임직원이 아닌경우에는 건강나이를 하지 않는다.
						next = null;
						continue;
					}
					if(!needSleepTest && next.getType().equals(CategoryType.SLEEP)){
						next = null;
						continue;
					}
					if(!needStessTest && next.getType().equals(CategoryType.STRESS)){
						next = null;
						continue;
					}
					if(skipNutrition && next.getType().equals(CategoryType.NUTRITION)){
						next = null;
						continue;
					}
					
				}
				
			}
		}
		
		return next;
	}
	
	@Override
	public Category findNextCategory(Category category, boolean isSamsung,
			boolean skipNutrition, boolean needSleepTest, boolean needStessTest) {
		Category next = null;
		List<Category> categories = dao.findCategory(category.getMaster());
		for(int i=0; i < categories.size(); i++){
			Category row = categories.get(i);
			if(row.getId().equals(category.getId())){
				if(i == categories.size()-1){
					next = null;
				}else{
					next = categories.get(i+1);
					if(!isSamsung && next.getType().equals(CategoryType.HEALTH_AGE)){
						//삼성임직원이 아닌경우에는 건강나이를 하지 않는다.
						next = null;
						continue;
					}
					if(!needSleepTest && next.getType().equals(CategoryType.SLEEP)){
						next = null;
						continue;
					}
					if(!needStessTest && next.getType().equals(CategoryType.STRESS)){
						next = null;
						continue;
					}
					if(skipNutrition && next.getType().equals(CategoryType.NUTRITION)){
						next = null;
						continue;
					}
					
				}
				
			}
		}
		
		return next;
	}
	
	

	@Transactional(readOnly=true)
	@Override
	public Question loadQuestion(String questionId) {
		return dao.loadQuestion(questionId);
	}

	@Override
	public List<CheckupMaster> getMaters() {
		return dao.getMaters();
	}

	@Override
	public void cloneMaster(String id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Date now = new Date();
		CheckupMaster checkupMaster = dao.loadCheckupMaster(id);
		ClassClone classClone = new ClassClone();
		CheckupMaster clonedMaster = (CheckupMaster) classClone.clone(checkupMaster);
		
		List<Category> categories = clonedMaster.getCategorys();
		// master 초기화 및 저장
		clonedMaster.setId(null);
		clonedMaster.setCreateDate(now);
		clonedMaster.setUpdateDate(now);
		clonedMaster.setStatus(CheckupMasterStatus.SAVED);
		Integer newVersion = dao.getMaxVersion()+1;
		clonedMaster.setVersion(newVersion.toString());
		clonedMaster.setCategorys(null);
		dao.saveMaster(clonedMaster);
		//category 초기화
		for(Category category: categories) {
			category.setId(null);
		}

	}

	@Transactional(readOnly=true)
	@Override
	public List<QuestionGroup> findQuestionGroup(Category category,
			Gender gender, boolean skipNutrition, boolean skipStress) {
		if(category.getType().equals(CategoryType.NUTRITION)){
			//영양설문일 경우 
			List<QuestionGroup> groups = new ArrayList<QuestionGroup>();
			
			if(skipNutrition){
				//첫번째 그룹만을 리턴시킨다.
			//	List<QuestionGroup> result = dao.findQuestionGroup(category, gender);
			//	groups.add(result.get(0));
				
			}else{
				groups.addAll(dao.findQuestionGroup(category, gender));
			}
			return groups;
		}else{
			return dao.findQuestionGroup(category, gender, skipStress);
		}
	}

	@Override
	public void saveMaster(CheckupMaster checkupMaster) {
		dao.saveMaster(checkupMaster);
	}

	@Override
	public CheckupMaster activeMaster(String id) {
		return dao.activeMaster(id);		
	}

	@Override
	public CheckupMaster inactiveMaster(String id) {
		return dao.inactiveMaster(id);
	}

	@Override
	public boolean isExistSameVersion(String id, int version) {
		int count = dao.countSameVersion(id, version);
		if(count > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isExistActive(String id) {
		int count = dao.countActive(id);
		if(count > 0) {
			return true;
		} else {
			return false;
		}
	}	

	@Override
	public QuestionGroup findFirstQuestionGroup(CheckupMaster master,String categoryType) {
		Category category = dao.findUniqueCategory(master, CategoryType.valueOf(categoryType));
		List<QuestionGroup> groups = dao.findQuestionGroup(category, Gender.ALL);
		if(groups.size() > 0){
			return groups.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<Question> findQuestions(QuestionGroup group, int depth,
			Gender gender, boolean todayIsReserveDate) {
		if(todayIsReserveDate){
			//검진당일이면 모든 질문을 다 리턴한다.
			return dao.findQuestions(group, depth, gender);
		}else{
			return dao.findOnlyPreQuestions(group, depth, gender);
		}
	}	

	@Override
	public int getMaxVersion() {
		return dao.getMaxVersion();
	}

	@Transactional(readOnly=true)
	@Override
	public QuestionGroup findQuestionGroup(CheckupMaster master,
			QuestionGroupType type) {
		
		return dao.findQuestionGroup(master, type);
	}

	@Override
	public boolean isLastQuestionInCategory(Question question, User user) {
		List<QuestionGroup> groups = dao.findQuestionGroup(question.getParentGroup().getCategory(), user.getGender());
		if(groups.size() > 0){
			QuestionGroup lastGroup = groups.get(groups.size()-1);
			if(lastGroup.equals(question.getParentGroup())){
				//해당 질문이 속한 그룹이 그 Category의 마지막 질문그룹인경우.
				return true;
			}
		}
		return false;
	}
	
	
	
}
