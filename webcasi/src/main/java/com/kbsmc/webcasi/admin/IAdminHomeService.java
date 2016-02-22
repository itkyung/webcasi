package com.kbsmc.webcasi.admin;

import java.util.List;

import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.HelpContents;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;

public interface IAdminHomeService {
	List<QuestionGroup> findQuestionGroup(Category category, CheckupMaster master);
	void createQuestionGroup(QuestionGroup group);
	void saveQuestionGroup(QuestionGroup group);
	QuestionGroup loadQuestionGroup(String id);
	
	List<Question>  findQuestion(QuestionGroup group, Question question, String itemId, int depth);
	void createQuestion(Question question);
	void saveQuestion(Question question);
	Question loadQuestion(String id);
	
	List<QuestionItem> findQuestionItem(Question question);
	void createQuestionItem(QuestionItem item);
	void saveQuestionItem(QuestionItem item);
	QuestionItem loadQuestionItem(String id);
	
	void createHelpContents(HelpContents group);
	void saveHelpContents(HelpContents group);
	HelpContents loadHelpContents(String id);
	
	int findParentItemCount(String itemId);
}
