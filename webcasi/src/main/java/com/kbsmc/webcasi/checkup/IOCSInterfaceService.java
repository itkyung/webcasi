package com.kbsmc.webcasi.checkup;

import java.util.Date;

import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.OCSHistoryCode;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.entity.User;

public interface IOCSInterfaceService {
	
	void initOcsHistoryCode(CheckupInstance instance);
	void updateOcsRow(User user,String patientNo,String version,String askCode,String answer,boolean active,Date acptDate,CheckupInstance instance);
	void initFromOCSHistory(User user,CheckupInstance instance,Question question,QuestionItem item,String ocsAskCode,String noAnswerCode,String patientNo,OCSHistoryCode su2qustt) throws Exception;
	void initOcsResultSubRowFromHistory(Question question,String patientNo,String version,Date acptDate,User user,CheckupInstance instance,boolean useNoAnswerCode) throws Exception;
	void initOcsWebResultFromHistory(Question question,String patientNo,String version,Date acptDate,User user,CheckupInstance instance,boolean useNoAnswerCode) throws Exception;
	void initOcsWebResultFromHistory(CheckupInstance instance, User user,QuestionGroup group) throws Exception;
	void initOcsWebResult(CheckupInstance instance, User user, Category category)  throws Exception;
	
	
	void updateOcsWebDb(User user,CheckupInstance instance,QuestionResult result);
	void deleteOcsWebDb(User user,CheckupInstance instance,QuestionResult result);
	void updateOcsWebDb(User user,CheckupInstance instance,NurseCheckResult result);
	void syncOcsWebDb(CheckupInstance instance, User user,
			QuestionGroup group) throws Exception;
	void syncAllOcsWebDb(CheckupInstance instance) throws Exception;
	
	void syncParentProtection(ParentProtection protection);
	void syncResultRequest(ResultRequest request);
}
