package com.kbsmc.webcasi.admin;

import java.util.List;

import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionItem;

public interface IQuestionItemDAO {
	
	void createQuestionItem(QuestionItem item);
	void saveQuestionItem(QuestionItem item);
	QuestionItem loadQuestionItem(String id);
	List<QuestionItem> findQuestionItem(Question question);
}
