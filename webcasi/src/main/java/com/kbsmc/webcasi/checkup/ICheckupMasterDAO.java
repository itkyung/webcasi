package com.kbsmc.webcasi.checkup;

import java.util.List;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.CheckupMasterStatus;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;

public interface ICheckupMasterDAO {
	List<CheckupMaster> searchMaster(CheckupMasterStatus status);
	
	Category findUniqueCategory(CheckupMaster master, CategoryType type);
	List<Category> findCategory(CheckupMaster master);
	List<QuestionGroup> findQuestionGroup(Category category,Gender gender);
	List<QuestionGroup> findQuestionGroup(Category category,Gender gender,boolean skipStress);
	List<Question> findQuestions(QuestionGroup group,int depth,Gender gender);
	List<Question> findQuestions(QuestionGroup group,Gender gender);
	List<Question> findQuestions(QuestionGroup group);
	List<Question> findOnlyPreQuestions(QuestionGroup group,int depth,Gender gender);	//검진당일이 아닌경우에 볼수있는 질문만 리턴한다.
	List<Question> findQuestions(Category category,Gender gender);
	
	QuestionGroup loadQuestionGroup(String groupId);
	List<QuestionGroup> findStressTestGroup(CheckupMaster master);
	
	
	
	List<Question> findChildQuestions(String questionItemId,Gender gender,int depth);
	List<Question> findOnlyPreChildQuestions(String questionItemId,Gender gender,int depth);	//검진당일이 아닌경우에 볼수있는 질문만 리턴한다.
	
	CheckupMaster loadCheckupMaster(String masterId);
	
	QuestionItem loadQuestionItem(String questionItemId);
	Question loadQuestion(String questionId);
	
	List<CheckupMaster> getMaters();
	void saveMaster(CheckupMaster checkupMaster);
	void saveMasterOnly(CheckupMaster checkupMaster);
	CheckupMaster loadMaster(String id);
	CheckupMaster activeMaster(String id);
	CheckupMaster inactiveMaster(String id);
	int getMaleQuestionCount(String masterId);
	int getFemaleQuestionCount(String masterId);
	
	int countSameVersion(String id, Integer version);
	int countActive(String id);
	
	int countQuestions(CheckupMaster master,Gender gender,int depth);
	int countQuestions(CheckupMaster master,Gender gender,int depth,Category category);
	int countQuestions(CheckupMaster master,Gender gender,int depth,QuestionGroup group);
	int getMaxVersion();
	QuestionGroup findQuestionGroup(CheckupMaster master,
			QuestionGroupType type);
	
	
}
