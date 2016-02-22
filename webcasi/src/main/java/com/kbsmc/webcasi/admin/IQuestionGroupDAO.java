package com.kbsmc.webcasi.admin;

import java.util.List;

import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.QuestionGroup;

public interface IQuestionGroupDAO {
	
	void createQuestionGroup(QuestionGroup group);
	void saveQuestionGroup(QuestionGroup group);
	QuestionGroup loadQuestionGroup(String id);
	List<QuestionGroup> findQuestionGroup(Category category, CheckupMaster master);
}
