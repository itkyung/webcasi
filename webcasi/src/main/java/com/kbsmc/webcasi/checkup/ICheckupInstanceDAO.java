package com.kbsmc.webcasi.checkup;

import java.util.Date;
import java.util.List;

import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.OCSHistoryCode;
import com.kbsmc.webcasi.entity.OCSWebResult;
import com.kbsmc.webcasi.entity.OCSWebResultProgress;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.entity.User;

public interface ICheckupInstanceDAO {
	
	void createInstance(CheckupInstance instance);
	void saveInstance(CheckupInstance instance);
	
	CheckupInstance loadInstance(String id);
	CheckupInstance findInstance(User user,String reserveNo);
	CheckupInstance findInstance(User user,CheckupMaster master,InstanceStatus... states);
	List<CheckupInstance> findInstance(boolean existAcptDate,InstanceStatus... states);
	CheckupInstance findInstance(User user,Date acptDate,InstanceStatus status);
	
	QuestionResult findResult(User user,CheckupInstance instance,Question question,String objectiveValue,String itemGroup);
	List<QuestionResult> findResult(User user,CheckupInstance instance,Question question);
	
	
	List<QuestionResult> findResult(User user,CheckupInstance instance,Question question,QuestionResult exclusiveResult);
	List<QuestionResult> findResult(User user,CheckupInstance instance, QuestionGroup group,int depth);
	List<QuestionResult> findResult(User user,CheckupInstance instance, Category category);
	
	List<CheckupInstance> findInstance(String startDate,String endDate);
	

	
	void createResult(QuestionResult result);
	void saveResult(QuestionResult result);
	String getIdOcsResult(String patientNo,String version,String askCode, boolean realtime,Date acptDate);
	String getIdOcsResult(CheckupInstance instance,String askCode);
	OCSWebResult findOcsResult(String patientNo,String version,String askCode, boolean realtime,Date acptDate);
	OCSWebResult findOcsResult(CheckupInstance instance,String askCode);
	void removeOcsResult(OCSWebResult result);
	OCSWebResult loadOcsResult(String id);
	void createOcsResult(OCSWebResult result);
	void saveOcsResult(OCSWebResult result);
	
	void updateRemoteSu2qustt(String patno,Date acptDate,String askCode,String answer,String version) ;
	void removeRemoteSu2qustt(String patno,Date acptDate,String askCode,String answer,String version) ;
	
	void createNurseResult(NurseCheckResult result);
	void saveNurseResult(NurseCheckResult result);
	NurseCheckResult findNurseResult(Question question,CheckupInstance instance,User patient);
	
	OCSWebResultProgress findWebResultProgress(CheckupInstance instance,User user,QuestionGroup group);
	List<OCSWebResultProgress> findWebResultProgress(CheckupInstance instance);
	
	void createOcsResult(OCSWebResultProgress progress);
	void saveOcsResult(OCSWebResultProgress progress);
	
	void initSu1fcwtt(String patientNo,Date acptDate);
	void initSu1fsrst(String patientNo,Date acptDate);
	void initSu1fssmt(String patientNo,Date acptDate);
	void initSu1fskmt(String patientNo,Date acptDate);
	
	void executeProcedure(String patientNo,Date acptDate) throws Exception;
	
	
	long getCalory(String patientNo,Date acptDate);
	//boolean existHistory(String ocsCode);
	
	OCSHistoryCode findOcsHistory(User user,String askCode);
	void initOcsHistoryCode(User user,String patno,Date reserveDate);
	
	CheckupInstance findInstanceByReserveDateStr(User user,String reserveDateStr);
	List<NurseCheckResult> findNurseResult(CheckupInstance instance,QuestionGroup group, int depth);
	List<NurseCheckResult> findNurseResult(CheckupInstance instance,Category category);
	
	void completeCheckupInstance();
	List<CheckupInstance> findHistory(User user,boolean isNullAcptDate);
	List<CheckupInstance> findInstance(Date acptDate,List<String> patnos,int start,int limit);
	
	int countRequiredResult(CheckupInstance instance);
	
	ParentProtection loadProtection(String id);
	void saveProtection(ParentProtection protection);
	void createProtection(ParentProtection protection);
	ParentProtection findProtection(CheckupInstance instace);
	
	ResultRequest loadResultResult(String id);
	void saveResultRequest(ResultRequest request);
	void createResultRequest(ResultRequest request);
	ResultRequest findResultRequest(CheckupInstance instace);
	
	
	List<ZipCode> searchZipCode(String dongName);
	void initHistoryCode(User owner);
	
}
