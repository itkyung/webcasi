package com.kbsmc.webcasi.checkup;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.ui.QuestionSubmitModel;

public interface ICheckupInstanceService {
	InstanceWrapper syncReserveNo(User user);
	CheckupInstance findProgressInstance(User user);
	CheckupInstance findInstance(User user,InstanceStatus... states);
	CheckupInstance findInstanceByReserveDateStr(User user,String reserveDateStr);
	CheckupInstance findActiveInstance(User user);	//Complete되지 않은 해당 사용자의 마지막 instance를 찾는다.
	
	CheckupInstance loadInstance(String instanceId);

	void doSubmitAndClear(String questionId,User user,String[] selectedItemIds,boolean useNoAnswerCode) throws Exception;
	void doSubmitAndClear(String questionItemId,User user,boolean toggleFlag,boolean useNoAnswerCode) throws Exception;
	void doSubmit(QuestionSubmitModel submitModel,User user) throws Exception;
	void doSubmitFromNurse(QuestionSubmitModel submitModel,User user) throws Exception;
	void doCancel(QuestionSubmitModel submitModel,User user) throws Exception;
	
	QuestionResult findResult(User user,CheckupInstance instance,Question question,String questionItemId,String itemGroup);
	List<QuestionResult> findResult(User user,CheckupInstance instance,QuestionGroup group,int depth);
	List<NurseCheckResult> findNurseResult(CheckupInstance instance,QuestionGroup group,int depth);
	QuestionGroup findLastQuestionGroup(CheckupInstance instance);
	

	
	void updateAgree(AgreeType type,CheckupInstance instance);
	void updateNutritionFlow(CheckupInstance instance,boolean skipNutrition);
	
	void updateNurseCheck(String instanceId,String questionId,String embededItemId) throws Exception;
	
	long calculateCalory(CheckupInstance instance,User user) throws Exception;
	void syncAcptDate(CheckupInstance instance);
	
	List<ProgressEntity> getProgress(User user);
	
	//acptDate가 null이 아닌 히스토리를 찾는다.
	List<CheckupInstance> findHistory(User user);
	List<CheckupInstance> findInstance(Date acptDate,String patno,String resno,String name, int start, int limit);
	void modifyPackage(CheckupInstance instance);
	
	ParentProtection loadOrCreateProtection(CheckupInstance instance,String id);
	void saveProtection(ParentProtection protection);
	ParentProtection findProtection(CheckupInstance instance);
	
	ResultRequest findResultRequest(CheckupInstance instance);
	ResultRequest loadOrCreateRequest(CheckupInstance instance,String id);
	void saveResultRequest(ResultRequest request);
	
	List<ZipCode> searchZipCode(String dongName);
	void initHistoryCode(User owner);
	void initOcsHistoryCode(CheckupInstance instance);
	
	List<CheckupInstance> syncAndSearchInstance(String startDate,String endDate);
}
