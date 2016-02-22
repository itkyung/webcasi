package com.kbsmc.webcasi.checkup;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.identity.entity.User;

public interface ICheckupMasterService {
	Category findUniqueCategory(CheckupMaster master,CategoryType type);
	List<QuestionGroup> findQuestionGroup(Category category,Gender gender);
	List<QuestionGroup> findQuestionGroup(Category category,Gender gender,boolean skipNutrition,boolean skipStress);

	List<Question> findQuestions(QuestionGroup group,int depth,Gender gender);
	List<Question> findQuestions(QuestionGroup group,int depth,Gender gender,boolean todayIsReserveDate);
	QuestionGroup loadQuestionGroup(String groupId);
	QuestionGroup findFirstQuestionGroup(CheckupMaster master,String categoryType);
	QuestionGroup findQuestionGroup(CheckupMaster master,QuestionGroupType type);
	
	List<Question> findChildQuestions(String questionItemId,Gender gender,int depth,boolean todayIsReserveDate);
	
	QuestionItem loadQuestionItem(String questionItemId);
	CheckupMaster loadCheckupMaster(String masterId);
	Category findNextCategory(QuestionGroup group,boolean isSamsung,boolean skipNutrition,boolean needSleepTest,boolean needStessTest);
	Category findNextCategory(Category category,boolean isSamsung,boolean skipNutrition,boolean needSleepTest,boolean needStessTest);
	
	Question loadQuestion(String questionId);
	
	
	boolean isExistSameVersion(String id, int version);
	boolean isExistActive(String id);
	
	List<CheckupMaster> getMaters();
	void saveMaster(CheckupMaster checkupMaster);
	void cloneMaster(String id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	CheckupMaster activeMaster(String id);
	CheckupMaster inactiveMaster(String id);
	int getMaxVersion();
	
	boolean isLastQuestionInCategory(Question question,User user);
}
