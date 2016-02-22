package com.kbsmc.webcasi.admin;

import java.util.List;

import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;

public interface IQuestionDAO {

	void createQuestion(Question question);
	void saveQuestion(Question question);
	Question loadQuestion(String id);
	List<Question> findQuestion(QuestionGroup group, Question question, String itemId, int depth);
	int findParentItemCount(String itemId);
}
