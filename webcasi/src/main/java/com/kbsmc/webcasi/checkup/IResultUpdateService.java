package com.kbsmc.webcasi.checkup;

import java.util.List;

import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.ui.QuestionSubmitModel;

public interface IResultUpdateService {
	List<QuestionResult> updateResult(User user,CheckupInstance instance,QuestionSubmitModel submitModel) throws Exception;
	List<QuestionResult> cancelResult(User user,CheckupInstance instance,QuestionSubmitModel submitModel) throws Exception;
	void updateProgress(QuestionResult result,boolean plus);
	int countTotalQuestion(CheckupMaster master,User user,boolean skipNutrition,boolean skipStress,CheckupInstance instance);
	void updateLastQuestion(QuestionResult result);
	void completeInstance(CheckupInstance instance);
	void updateOcsWebstat(CheckupInstance instance,int progress);
}
