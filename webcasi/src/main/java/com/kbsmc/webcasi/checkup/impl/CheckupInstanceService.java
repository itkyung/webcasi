package com.kbsmc.webcasi.checkup.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.CheckupMasterStatus;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.checkup.AgreeType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceDAO;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ICheckupMasterDAO;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.checkup.IOCSInterfaceService;
import com.kbsmc.webcasi.checkup.IResultUpdateService;
import com.kbsmc.webcasi.checkup.InstanceWrapper;
import com.kbsmc.webcasi.checkup.ProgressEntity;
import com.kbsmc.webcasi.checkup.Su2Qustt;
import com.kbsmc.webcasi.checkup.ZipCode;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.OCSWebResult;
import com.kbsmc.webcasi.entity.OCSWebResultProgress;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionEmbeddedItem;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.EmbeddedPatientNo;
import com.kbsmc.webcasi.identity.entity.LinkReserveData;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.ui.QuestionSubmitModel;

@Service("CheckupInstanceService")
public class CheckupInstanceService implements ICheckupInstanceService {
	private Log log = LogFactory.getLog(CheckupInstanceService.class);
	
	@Autowired private ICheckupInstanceDAO dao;
	@Autowired private ICheckupMasterDAO masterDao;
	@Autowired private ICheckupMasterService masterService;
	@Autowired private IOCSInterfaceService ocsInterface;
	@Autowired private IResultUpdateService resultUpdateService;
	@Autowired private IUserDAO userDao;
	@Autowired private IUserService userService;
	
	/**
	 * 1. 해당 사용자로 아직 complete가 되지 않은 instance를 찾아서 이것을 그냥 이용한다.
	 * 	  1.1 기존 instance가 있으면서 아직 instance의 acptDate가 null인 경우에는 surempt에 acptDate가 오늘날짜로 존재하는 예약이 있으면 instance의 acptDate를 업데이트.
	 *    1.2 만약 오늘자가 없으면 acptDate가 null이면서 가장 오늘자에서 가까운 예약일을 가지는 예약을 가져와서 예약날짜를 동기화시킨다.
	 * 2. 1번이 존재하지 않으면 surempt테이블을 확인해서 acptDate가 null이면서 hopeDate가 오늘또는 그 이후인 예약내역이 존재하면 새로운 instance를 생성한다.
	 * 		이때에 hodedate를 사용자의 reserveDate에도 업데이트한다.
	 * 
	 * 3. 2번이 존재하지 않으면 surempt에 acptDate가 오늘날짜로 존재하는 예약내역을 찾아서 instance를 생성한다. 이때에 hodedate를 사용자의 reserveDate에도 업데이트한다.
	 * 		-->이때에 checkupInstance내에 오늘자로 acptDate이면서 complete인게 있으면 생성시키지 않는다.
	 * 
	 * --> 새로운 instance가 생성될때에는 무조건 checkup_su2qustt에 해당 수진자번호의 모든것의 itfyn을 y로 바꾼다. 또 OCS에서 과거 히스토리를 얻어온다.
	 * --> 여기에서 사용자의 현재 업데이트된 패키지 정보와 active한 instance의 패키지 정보를 서로 비교해서 재조정한다.
	 */
	@Transactional
	public InstanceWrapper syncReserveNo(User user) {
		InstanceWrapper wrapper = new InstanceWrapper();
		wrapper.setNeedHistoryInit(false);
		
		CheckupMaster master = null;
		List<CheckupMaster> masters = masterDao.searchMaster(CheckupMasterStatus.ACTIVE);
		if(masters.size() > 0){
			master = masters.get(0);
		}
		if(master == null){
			return wrapper;
		}
		String patno = userService.findPatientNo(user);
		
		//1번. 
		CheckupInstance instance = dao.findInstance(user, master, InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
		if(instance != null){
			if(instance.isSamsungEmployee() != user.isSamsungEmployee() || 
					instance.isNeedSleepTest() != user.isNeedSleepTest() ||
					instance.isNeedStressTest() != user.isNeedStressTest()){
				modifyPackage(instance);
			}
			
			wrapper.setInstance(instance);
			
			
			if(instance.getAcptDate() == null){
			//1-1번 
				LinkReserveData reserveData = userDao.findReserveData(patno,new Date(),false);
				if(reserveData == null){
					//1.2번 오늘날짜에서 가장 가까운 예약일 예약정보를 얻는다.
					reserveData = userDao.findReserveData(patno, null, true);
				}
				
				if(reserveData != null){
					instance.setAcptDate(reserveData.getAcptDate());
					instance.setReserveDate(reserveData.getHopeDate());
					dao.saveInstance(instance);
					
					user.setLastReserveDate(instance.getReserveDate());
					user.setLastReserveNo(instance.getReserveNo());
					userDao.updateUser(user);
					
					return wrapper;
				}
			}
			
			
		}else{
			//2번 
			LinkReserveData reserve = userDao.findReserveData(patno,null,true);
			if(reserve != null){
				user.setLastReserveDate(reserve.getHopeDate());
				user.setLastReserveNo(reserve.getReserveNo());
				wrapper.setInstance(makeNewInstance(user,master,null));
				userDao.updateUser(user);
				wrapper.setNeedHistoryInit(true);
				return wrapper;
			}
		
			//3번 
			LinkReserveData todayReserve = userDao.findReserveData(patno,new Date(),false);
			if(todayReserve != null){
				CheckupInstance in = dao.findInstance(user, new Date(), InstanceStatus.COMPLETED);
				if(in != null){
					wrapper.setInstance(in);
					return wrapper;
				}
				
				user.setLastReserveDate(todayReserve.getHopeDate());
				user.setLastReserveNo(todayReserve.getReserveNo());
				wrapper.setInstance(makeNewInstance(user,master,todayReserve.getAcptDate()));
				userDao.updateUser(user);
				wrapper.setNeedHistoryInit(true);
				return wrapper;
			}
		}
		return wrapper;
	}

	/**
	 * 새로운 Instance를 생성.
	 * 이후에 checkup_su2qustt에 해당 수진자번호의 모든것의 itfyn을 y로 바꾼다
	 * 그리고 OCS에서 과거 히스토리를 얻어온다.
	 * @param user
	 * @param master
	 * @return
	 */
	private CheckupInstance makeNewInstance(User user,CheckupMaster master,Date acptDate){
		CheckupInstance ni = new CheckupInstance();
		ni.setMaster(master);
		ni.setOwner(user);
		ni.setReserveNo(user.getLastReserveNo());
		ni.setReserveDate(user.getLastReserveDate());
		ni.setProgress(0);
		ni.setStatus(InstanceStatus.READY);
		ni.setResultCount(0);
		ni.setTotalQuestionCount(resultUpdateService.countTotalQuestion(master,user,false,false,ni));
		ni.setPatno(userService.findPatientNo(user));
		if(acptDate != null)
			ni.setAcptDate(acptDate);
		
		ni.setSamsungEmployee(user.isSamsungEmployee());
		ni.setNeedSleepTest(user.isNeedSleepTest());
		ni.setNeedStressTest(user.isNeedStressTest());
		
		dao.createInstance(ni);
		
		
		
//		//처음 Instance를 생성시킬때에는 OCS에서 해당 환자의 과거데이타를 얻어와서 ocs history에 넣어두어야한다.
//		ocsInterface.initOcsHistoryCode(ni);
		//ocs의 surtempt에 webstat를 업데이트한다.
		resultUpdateService.updateOcsWebstat(ni, 9);
		
		return ni;
	}
	
	
	@Transactional(readOnly=true)
	public CheckupInstance findProgressInstance(User user) {
		
		return findInstance(user,InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
	}

	/**
	 * 질문에 대한 대답을 반영한다. 
	 */
	@Transactional
	public void doSubmit(QuestionSubmitModel submitModel, User user)
			throws Exception {
		Question question = null;
	
		try{
			question = masterDao.loadQuestion(submitModel.getQuestionId());
		
		}catch(Exception e){
			log.error("Question Load Error id = [" + submitModel.getQuestionId() + "] :",e);
			throw e;
		}
		
		
		CheckupInstance instance = null;
		if(submitModel.getInstanceId() != null && !"".equals(submitModel.getInstanceId())){
			instance = dao.loadInstance(submitModel.getInstanceId());
		}else{
			instance = dao.findInstance(user, question.getParentGroup().getMaster(),InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
		}
		
		
		if(instance != null){
			List<QuestionResult> results = null;
			synchronized(instance.getId()){
				//하나의 수진자는 동시에 하나의 결과만 업데이트가 가능하다.
				results = resultUpdateService.updateResult(user, instance, submitModel);
				submitModel.setProgressRate(results.get(0).getInstacne().getProgress());
				
				//OCS WebDB는 sync함수에서 한번에 업데이트처리한다.
//				for(QuestionResult result : results){
//					ocsInterface.updateOcsWebDb(user,instance,result);
//				}
				
				OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, question.getParentGroup());
				if(progress != null){
					//해당 질문그룹의 마지막 수정날짜를 업데이트한다.
					synchronized (progress) {
						progress.setLastUpdateDate(new Date());
						dao.saveOcsResult(progress);
					}
				}
			}
			
		}else{
			throw new Exception("문진진행데이타가 존재하지 않습니다.");
			
		}
	}
	
	@Transactional
	@Override
	public void doCancel(QuestionSubmitModel submitModel,User user) throws Exception {
		Question question = masterDao.loadQuestion(submitModel.getQuestionId());
		
		CheckupInstance instance = null;
		if(submitModel.getInstanceId() != null && !"".equals(submitModel.getInstanceId())){
			instance = dao.loadInstance(submitModel.getInstanceId());
		}else{
			instance = dao.findInstance(user, question.getParentGroup().getMaster(),InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
		}
		
		if(instance != null){
			List<QuestionResult> results = null;
			synchronized(instance.getId()){
				//하나의 수진자는 동시에 하나의 결과만 업데이트가 가능하다.
				results = resultUpdateService.cancelResult(user, instance, submitModel);
				submitModel.setProgressRate(results.get(0).getInstacne().getProgress());
				
				//OCS WebDB는 sync함수에서 한번에 업데이트처리한다.
//				for(QuestionResult result : results){
//					//ocsInterface.deleteOcsWebDb(user,instance,result);
//					ocsInterface.updateOcsWebDb(user, instance, result);
//				}
				
				OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, question.getParentGroup());
				if(progress != null){
					//해당 질문그룹의 마지막 수정날짜를 업데이트한다.
					progress.setLastUpdateDate(new Date());
					dao.saveOcsResult(progress);
				}
			}
			
			
		}else{
			throw new Exception("문진진행데이타가 존재하지 않습니다.");
			
		}
	}

	
	/**
	 * 영양문진에서 첫번째 Depth에 대한 submit을 한다.
	 * 음주의  탭 유형도 동일하다.
	 */
	
	@Transactional
	@Override
	public void doSubmitAndClear(String questionId, User user,String[] selectedItemIds,boolean useNoAnswerCode) throws Exception {
		Question question = masterDao.loadQuestion(questionId);
		CheckupInstance instance = dao.findInstance(user, question.getParentGroup().getMaster(),InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
		
		if(instance != null){
			boolean alreadyExist = clearResult(user,instance,question,useNoAnswerCode,selectedItemIds);
			QuestionResult lastResult = null;
			for(String itemId : selectedItemIds){
				if(itemId != null && !"".equals(itemId))
					lastResult = doSubmitItems(user,instance,question,itemId);
			}
			
			//if(!alreadyExist){
				//기존에 질문에 대답이 없었을 경우에는 진도율을 재계산한다.
				if(question.isRequired() && lastResult != null){
					resultUpdateService.updateProgress(lastResult,true);
				}else if(question.getDepth() == 1){
					resultUpdateService.updateLastQuestion(lastResult);
				}
			//}
			
			OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, question.getParentGroup());
			if(progress != null){
				//해당 질문그룹의 마지막 수정날짜를 업데이트한다.
				progress.setLastUpdateDate(new Date());
				dao.saveOcsResult(progress);
			}
		}else{
			throw new Exception("문진진행데이타가 존재하지 않습니다.");
		}
		return;
	}

	private QuestionResult doSubmitItems(User user,CheckupInstance instance,Question question,String itemId){
		QuestionResult result = new QuestionResult();
		result.setOwner(user);
		result.setQuestion(question);
		result.setInstacne(instance);
			
		result.setType(question.getType());
		result.setObjectiveValue(itemId);
		result.setActive(true);
		dao.createResult(result);
		return result;
	}
	
	/**
	 * 해당 사용자가 해당 질문에 대해서 대답한 결과를 다 초기화한다.
	 * 기존에 결과가 있었으면 true를 없었으면 false를 리턴한다.
	 * selectedItemIds 안에 존재하는 result는 삭제하지 않는다.
	 * @param user
	 * @param instance
	 * @param question
	 */
	private boolean clearResult(User user,CheckupInstance instance,Question question,boolean useNoAnswerCode,String[] selectedItemIds){
		boolean exist = false;
		List<QuestionResult> results = dao.findResult(user, instance, question);
		if(results.size() > 0){
			exist = true;
		}
		for(QuestionResult result : results){
			if(isExist(result, selectedItemIds)) continue;
			result.setActive(false);
			dao.saveResult(result);
			
			//OCS WebDB는 sync함수에서 한번에 업데이트처리한다.
			//ocsInterface.updateOcsWebDb(user, instance, result);
			
		}
		QuestionGroup group = question.getParentGroup();
		int currentDepth = question.getDepth();
		List<Question> childs =  masterDao.findQuestions(group, currentDepth+1, user.getGender());
		
		if(childs.size() == 0){
			return exist;
		}
		//질문 하위의 질문들도 다 초기화한다.
		for(Question child : childs){
			if(contains(child.getParentItems(),selectedItemIds)) continue;
			boolean subExist = clearResult(user,instance,child,useNoAnswerCode,selectedItemIds);
			if(subExist) exist = true;
		}
		
		return exist;
	}
	
	private boolean contains(List<String> ids,String[] selectedItemIds){
		for(String id : ids){
			for(String itemId : selectedItemIds){
				if(id.equals(itemId)) return true;
			}
		}
		return false;
	}
	
	private boolean isExist(QuestionResult result,String[] selectedItemIds){
		if(result.getObjectiveValue() != null){
			for(String id : selectedItemIds){
				if(result.getObjectiveValue().equals(id)) return true;
			}
		}
		return false;
	}
	
	
	
	
	@Transactional(readOnly=true)
	public QuestionResult findResult(User user, CheckupInstance instance,
			Question question,String questionItemId,String itemGroup) {
		return dao.findResult(user, instance, question, questionItemId,itemGroup);
	}
	

	@Transactional(readOnly=false)
	@Override
	public List<QuestionResult> findResult(User user, CheckupInstance instance,QuestionGroup group,
			int depth) {
		return dao.findResult(user,instance, group, depth);
	}

	@Transactional(readOnly=false)
	@Override
	public QuestionGroup findLastQuestionGroup(CheckupInstance instance) {
		Question lastQuestion = masterDao.loadQuestion(instance.getLastQuestionId());
		
		return lastQuestion.getParentGroup();
	}
	
	private void updateTotalCountAndProgress(CheckupInstance instance,boolean skipNutrition){
		int totalCount =  resultUpdateService.countTotalQuestion(instance.getMaster(), instance.getOwner(),skipNutrition, instance.isSkipStress(),instance);
		
		instance.setSkipNutrition(skipNutrition);
		
		instance.setTotalQuestionCount(totalCount);

		totalCount = instance.getTotalQuestionCount();	
		
		double currentCount = instance.getResultCount();
		if(currentCount > totalCount){
			currentCount = totalCount;
			instance.setResultCount((int)currentCount);
		}
		double t = currentCount/totalCount;
		int progress = (int)(t * 100);
		
		instance.setProgress(progress);
		
		//진도율이 100%가 되면 완료처리한다.
		if(progress == 100 && !instance.equals(InstanceStatus.COMPLETED)){
			instance.setStatus(InstanceStatus.FIRST_COMPLETED);
		}else if(progress != 0){
			instance.setStatus(InstanceStatus.IN_PROGRESS);
		}
		
		dao.saveInstance(instance);
		
	}
	
	
	
	
	@Transactional
	@Override
	public void updateAgree(AgreeType type, CheckupInstance instance) {
		switch(type){
		case FIRST_AGREE:
			instance.setAgreeFlag(true);
			break;
		case FIRST_DISAGREE:
			instance.setAgreeFlag(false);
			break;
		case SECOND_AGREE:
			instance.setAgreeFlag2(true);
			break;
		case SECOND_DISAGREE:
			instance.setAgreeFlag2(false);
			break;
		}
		
		if(instance.getAgreeFlag() != null && instance.getAgreeFlag2() != null){
			instance.setStatus(InstanceStatus.FIRST_COMPLETED);
			instance.setProgress(100);
		}
		
		dao.saveInstance(instance);
		
	}

	/**
	 * 영양설문을 진행하는지여부를 체크하고 전체 진도율등을 재계산한다.
	 */
	@Transactional
	@Override
	public void updateNutritionFlow(CheckupInstance instance,
			boolean skipNutrition) {
		instance.setSkipNutrition(skipNutrition);
		instance.setNutritionFlagUpdated(true);
		updateTotalCountAndProgress(instance,skipNutrition);
		
	}

	/**
	 * toggleFlag에 따라서 해당 QuestionItem의 상위 Question에 존재하는 모든 값들을 초기화처리한다.
	 * 영양문진의 checkbox에서만 사용함.
	 */
	@Transactional
	@Override
	public void doSubmitAndClear(String questionItemId, User user,
			boolean toggleFlag,boolean useNoAnswerCode) throws Exception {
		
		QuestionItem item = masterDao.loadQuestionItem(questionItemId);
		Question question = item.getParentQuestion();
		CheckupInstance instance = dao.findInstance(user, question.getParentGroup().getMaster(),InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
		if(instance != null){
			synchronized(instance.getId()){
				if(toggleFlag){
					//없음이 Toggle on이 되면 기존에 설정된 다른 값들을 다 clear시켜야함.
					String[] selectedItemId = {item.getId()};
					clearResult(user,instance,question,useNoAnswerCode,selectedItemId);
					
					updateItemResult(user,instance,question,item,true);
//					for(QuestionItem subItem : question.getChildItems()){
//						if(subItem.equals(item)) continue;
//						if(subItem.isActive() == false) continue;
//						updateItemResult(user,instance,question,subItem,false);
//					}
				
				}else{
					updateItemResult(user,instance,question,item,false);
				}
				
				OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, question.getParentGroup());
				if(progress != null){
					//해당 질문그룹의 마지막 수정날짜를 업데이트한다.
					progress.setLastUpdateDate(new Date());
					dao.saveOcsResult(progress);
				}
			}
		}
	}
	
	/**
	 * 영양문진에서만 사용되는 형태로 item은 checkbox유형이다.
	 * 없음을 처리하기 위해서 만듬.
	 * @param user
	 * @param instance
	 * @param question
	 * @param item
	 * @param active
	 */
	private void updateItemResult(User user,CheckupInstance instance,Question question,QuestionItem item,boolean active){
		boolean isCreate = false;
		QuestionResult result = findResult(user, instance, question,item.getId(),item.getItemGroupStr());
		if(result == null){
			result = new QuestionResult();
			result.setOwner(user);
			result.setQuestion(question);
			result.setInstacne(instance);
			
			isCreate = true;
		}
		
		result.setType(item.getType());
		result.setObjectiveValue(item.getId());
		result.setActive(active);
		result.setOcsAskCode(item.getOcsAskCode());
		if(active)
			result.setOcsValue(item.getOcsAnswer());
		else
			result.setOcsValue(item.getOcsNoAnswerCode());
		
		if(isCreate){
			dao.createResult(result);
			
			if(question.isRequired())
				resultUpdateService.updateProgress(result,true);
			else if(question.getDepth() == 1){
				resultUpdateService.updateLastQuestion(result);
			}
		}else{
			
			dao.saveResult(result);
		}
		
		//OCS WebDB는 sync함수에서 한번에 업데이트처리한다.
		//ocsInterface.updateOcsWebDb(user,instance,result);
	}

	@Transactional(readOnly=true)
	@Override
	public CheckupInstance loadInstance(String instanceId) {
		return dao.loadInstance(instanceId);
	}

	/**
	 * 간호사 화면에서 데이타를 수정하는 부분임.
	 * 기존 update코드를 호출한후에
	 * 실시간으로 OCS DB에 update를 진행함. --> OCS DB업데이트는 실제로는 Sync함수에서 처리함 
	 */
	@Transactional
	@Override
	public void doSubmitFromNurse(QuestionSubmitModel submitModel, User user)
			throws Exception {
		
		doSubmit(submitModel,user);
		
	}
	

	/**
	 * 간호사가 체크한 항목을 업데이트한다.
	 */
	@Transactional
	@Override
	public void updateNurseCheck(String instanceId, String questionId,
			String embededItemId) throws Exception {
		Question question = masterDao.loadQuestion(questionId);
		CheckupInstance instance = dao.loadInstance(instanceId);
		
		//동일한 사용자의 하나의 instance에서 하나만 업데이트 가능하다.
		synchronized(instance){
			boolean isCreate = false;
			NurseCheckResult result = dao.findNurseResult(question, instance, instance.getOwner());
			if(result == null){
				result = new NurseCheckResult();
				result.setActive(true);
				result.setQuestion(question);
				result.setInstance(instance);
				result.setPatient(instance.getOwner());
				result.setType(question.getNurseQuestionType());
				result.setOcsAskCode(question.getNurseOcsAskCode());
				isCreate = true;
			}
			if(question.getNurseQuestionType().equals(QuestionType.CHECK)){
				if(result.isChecked()){
					result.setChecked(false);
					result.setOcsAskCode(question.getNurseOcsAskCode());
					result.setOcsValue(question.getNurseOcsNoAnswerCode());
				}else{
					result.setChecked(true);
					result.setOcsAskCode(question.getNurseOcsAskCode());
					result.setOcsValue(question.getNurseOcsAnswer());
				}
				
			}else{
				
				for(QuestionEmbeddedItem item : question.getChildNurseItems()){
					if(item.getKey().equals(embededItemId)){
						result.setObjectiveValue(embededItemId);
						result.setOcsAskCode(item.getOcsAskCode());
						result.setOcsValue(item.getOcsAnswer());
					}
				}
			}
			
			if(isCreate){
				dao.createNurseResult(result);
			}else{
				dao.saveNurseResult(result);
			}
			
			//OCS WebDB에 업데이트하고 그것을 Realtime으로 OCS DB에 업데이트한다.
			//NurseCheck부분의 update는 여기서 직접 진행한다.
			ocsInterface.updateOcsWebDb(instance.getOwner(),instance,result);
			
			OCSWebResultProgress progress = dao.findWebResultProgress(instance, instance.getOwner(), question.getParentGroup());
			if(progress != null){
				//해당 질문그룹의 마지막 수정날짜를 업데이트한다.
				synchronized (progress) {
					progress.setLastUpdateDate(new Date());
					dao.saveOcsResult(progress);
				}
			}
		}
	}

	/**
	 * 해당 사용자의 칼로리를 계산한다.
	 */
	@Transactional
	@Override
	public long calculateCalory(CheckupInstance instance, User user)
			throws Exception {
		List<EmbeddedPatientNo> patientNos = user.getPatientNos();
		String patientNo = null;
		
		for(EmbeddedPatientNo no : patientNos){
			if(no.isActive()){
				patientNo = no.getPatientNo();
				break;
			}
		}
		
		if(patientNo == null){
			throw new Exception("수진번호가 없습니다.");
		}
		
		Date acptDate = instance.getAcptDate();
		if(acptDate == null)
			acptDate = instance.getReserveDate();	//acptDate는 문진시점에는 존재하지 않는
		
		/*
		 * 1. su1fcwtt 테이블의 1일 섭취 무게분량 데이타를 삭제 
		 */
		dao.initSu1fcwtt(patientNo, acptDate);
		/*
		 * 2. su1fsrst 테이블의 수진자별 섭취 영양소 삭제
		 */
		dao.initSu1fsrst(patientNo, acptDate);
		
		
		/*
		 * 3. su1fssmt 테이블의 수진자별 일일 섭취량 삭제 
		 */
		//dao.initSu1fssmt(patientNo, acptDate);
		
		/*
		 * 4. su1fskmt 테이블의 수진자별 식품군별 집게 삭제.
		 */
		//dao.initSu1fskmt(patientNo, acptDate);
		
		/**
		 * 칼로리 계산 프로시저 순서대로 호출.
		 */
		dao.executeProcedure(patientNo, acptDate);
	
		
		return dao.getCalory(patientNo, acptDate);
	}

	/**
	 * yyyy-mm-dd형태의 예약일을 기준으로 그날자로 생성되어있는 예약데이타가 있는지 찾아온다.
	 */
	@Override
	public CheckupInstance findInstanceByReserveDateStr(User user,
			String reserveDateStr) {
		return dao.findInstanceByReserveDateStr(user, reserveDateStr);
	}

	/**
	 * 해당 사용자가 현재 Complete되지 않은 문진중 마지막 문진을 얻어온다. - 기본가정은 동시에 한명은 하나의 Active한 문진 Instance를 가질수 있다.
	 */
	@Override
	public CheckupInstance findActiveInstance(User user) {
		List<CheckupMaster> masters = masterDao.searchMaster(CheckupMasterStatus.ACTIVE);
		if(masters.size() > 0){
			return dao.findInstance(user, masters.get(0), InstanceStatus.READY,InstanceStatus.IN_PROGRESS,InstanceStatus.FIRST_COMPLETED);
		}
		
		return null;
	}

	/**
	 * surempt테이블에 acptDate가 오늘날짜인 예약내역을 찾아서 instance에 acptDate에 update처리한다.
	 */
	@Transactional
	@Override
	public void syncAcptDate(CheckupInstance instance) {
		String patno = userService.findPatientNo(instance.getOwner());
		LinkReserveData reserve = userDao.findReserveData(patno, new Date(),false);
		if(reserve != null){
			instance.setAcptDate(reserve.getAcptDate());
			dao.saveInstance(instance);
		}
	}

	@Override
	public List<NurseCheckResult> findNurseResult(CheckupInstance instance,
			QuestionGroup group, int depth) {
		return dao.findNurseResult(instance, group, depth);
	}

	@Override
	public CheckupInstance findInstance(User user, InstanceStatus... states) {
		CheckupMaster master = null;
		List<CheckupMaster> masters = masterDao.searchMaster(CheckupMasterStatus.ACTIVE);
		if(masters.size() > 0){
			master = masters.get(0);
		}
		if(master == null){
			return null;
		}
		
		return dao.findInstance(user, master, states);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ProgressEntity> getProgress(User user) {
		List<ProgressEntity> progressList = new ArrayList<ProgressEntity>();
		
		CheckupInstance instance = findProgressInstance(user);
		CheckupMaster master = null;
		Question lastQuestion = null;
		if(instance == null){
			List<CheckupMaster> masters = masterDao.searchMaster(CheckupMasterStatus.ACTIVE);
			if(masters.size() > 0){
				master = masters.get(0);
			}
			if(master == null)
				return progressList;
		}else{
			master = instance.getMaster();
			lastQuestion = masterDao.loadQuestion(instance.getLastQuestionId());
		}
		
		List<OCSWebResultProgress> resultProgress = dao.findWebResultProgress(instance);
		Map<QuestionGroup,OCSWebResultProgress> resultMap = new HashMap<QuestionGroup,OCSWebResultProgress>();
		for(OCSWebResultProgress p : resultProgress){
			if(p.getLastUpdateDate() != null){
				resultMap.put(p.getQuestionGroup(), p);
			}
		}
		
		
		List<Category> categories = masterDao.findCategory(master);
		for(Category category : categories){
			ProgressEntity pCategory = new ProgressEntity();
			pCategory.setId(category.getId());
			pCategory.setTitle(category.getTitle());
			pCategory.setType(category.getType().name());
			
			if(category.getType().equals(CategoryType.HEALTH_AGE)){
				if(!instance.isSamsungEmployee()){
					continue;
				}
			}
			
			if(category.getType().equals(CategoryType.SLEEP)){
				if(!instance.isNeedSleepTest()){
					continue;
				}
			}
			
			if(category.getType().equals(CategoryType.STRESS)){
				if(!instance.isNeedStressTest()){
					continue;
				}
			}
			
			if(category.getType().equals(CategoryType.NUTRITION)){
				if(instance.isSkipNutrition()){
					continue;
				}
			}
			
			//boolean alreadyProcessed = false;
			
//			if(instance != null){
//				int currentSortOrder = category.getSortOrder();
//				int lastSortOrder = lastQuestion.getParentGroup().getCategory().getSortOrder();
//				
//				if(currentSortOrder <= lastSortOrder){
//					pCategory.setProcessed(true);
//					alreadyProcessed = true;
//				}else{
//					pCategory.setProcessed(false);
//				}
//			}else{
//				pCategory.setProcessed(false);
//			}
			makeChildGroups(pCategory,category,resultMap,instance,user);
			progressList.add(pCategory);
		}
		
		return progressList;
	}
	
	private void makeChildGroups(ProgressEntity progress,Category category,Map<QuestionGroup,OCSWebResultProgress> resultMap,CheckupInstance instance,User currentUser){
		List<ProgressEntity> childGroups = new ArrayList<ProgressEntity>();
		
		//int lastSortOrder = lastQuestion.getParentGroup().getSortOrder();
		
		if(instance == null){
			List<QuestionGroup> groups = masterService.findQuestionGroup(category, currentUser.getGender(),false,false);
			for(QuestionGroup group : groups){
				ProgressEntity entity = new ProgressEntity();
				entity.setId(group.getId());
				entity.setTitle(group.getTitle());
				entity.setProcessed(false);
				childGroups.add(entity);
			}
		}else{
			List<QuestionGroup> groups = masterService.findQuestionGroup(category, currentUser.getGender(),instance.isSkipNutrition(),instance.isSkipStress());
			for(QuestionGroup group : groups){
				ProgressEntity entity = new ProgressEntity();
				entity.setId(group.getId());
				entity.setTitle(group.getTitle());
				
				if(resultMap.containsKey(group)){
					entity.setProcessed(true);
				}else{
					entity.setProcessed(false);
				}
				
				childGroups.add(entity);
			}
		}
		progress.setQuestionGroups(childGroups);
	}

	@Override
	public List<CheckupInstance> findHistory(User user) {
		
		return dao.findHistory(user, true);
	}

	@Override
	public List<CheckupInstance> findInstance(Date acptDate, String patno,
			String resno, String name, int start, int limit) {
		List<String> patnos = new ArrayList<String>();
		
		if(patno != null){
			patnos.add(patno);
		}else if(resno != null){
			User user = userService.findByPlainLoginId(resno);
			if(user != null){
				patnos.add(userService.findPatientNo(user));
			}
			
		}else if(name != null){
			List<User> users = userDao.findByName(name);
			for(User user : users){
				patnos.add(userService.findPatientNo(user));
			}
		}
		
		return dao.findInstance(acptDate, patnos,start,limit);
	}

	/**
	 * 해당 instance의 패키지가 변경되었으니 새로운 패키지로 업데이트하고 내부 진도율및 ocs진도율을 재 계산한다.
	 */
	@Transactional
	@Override
	public void modifyPackage(CheckupInstance instance) {
		instance.setSamsungEmployee(instance.getOwner().isSamsungEmployee());
		instance.setNeedSleepTest(instance.getOwner().isNeedSleepTest());
		instance.setNeedStressTest(instance.getOwner().isNeedStressTest());
		
		long oldProgress = instance.getProgress();
		InstanceStatus oldStatus = instance.getStatus();
		updateTotalCountAndProgress(instance, instance.isSkipNutrition());
		
		int ocsProgress = 0;
		
		if(!oldStatus.equals(instance.getStatus())){
			//상태가 변경되었을때에만 업데이트한다.
			if(instance.getStatus().equals(InstanceStatus.FIRST_COMPLETED) || instance.getStatus().equals(InstanceStatus.COMPLETED)){
				//현재 상태가 완료이면.
				ocsProgress = 0;
			}else if(instance.getStatus().equals(InstanceStatus.IN_PROGRESS)){
				//진행중이면 무조건 영양상태로 만든다.
				ocsProgress = 4;
			}
			
			resultUpdateService.updateOcsWebstat(instance, ocsProgress);
		}
		
	}

	@Transactional(readOnly=true)
	@Override
	public ParentProtection loadOrCreateProtection(CheckupInstance instance,String id) {
		ParentProtection protection = null;
		protection = findProtection(instance);
		if(protection == null && id == null){
			protection = new ParentProtection();
			protection.setInstance(instance);
			protection.setOwner(instance.getOwner());
			
		}else if(protection == null){
			protection = dao.loadProtection(id);
		}
		
		return protection;
	}

	@Transactional
	@Override
	public void saveProtection(ParentProtection protection) {
		if(protection.getId() == null){
			dao.createProtection(protection);
		}else{
			dao.saveProtection(protection);
		}
		
		ocsInterface.syncParentProtection(protection);
	}

	@Override
	public ParentProtection findProtection(CheckupInstance instance) {
		
		return dao.findProtection(instance);
	}

	@Override
	public List<ZipCode> searchZipCode(String dongName) {
		return dao.searchZipCode(dongName);
	}

	@Override
	public ResultRequest findResultRequest(CheckupInstance instance) {
		
		return dao.findResultRequest(instance);
	}

	@Override
	public ResultRequest loadOrCreateRequest(CheckupInstance instance, String id) {
		ResultRequest request = null;
		request = findResultRequest(instance);
		if(request == null && id == null){
			request = new ResultRequest();
			request.setInstance(instance);
			request.setOwner(instance.getOwner());
			//request.setCompanyName(userDao.getCompanyName(userService.findPatientNo(instance.getOwner()),instance.getReserveDate()));
			
		}else if(request == null){
			request = dao.loadResultResult(id);
		}
		
		return request;
	}

	@Transactional
	@Override
	public void saveResultRequest(ResultRequest request) {
		if(request.getId() == null){
			dao.createResultRequest(request);
		}else{
			dao.saveResultRequest(request);
		}
		
		ocsInterface.syncResultRequest(request);
		
	}

	@Transactional
	@Override
	public void initHistoryCode(User owner) {
		dao.initHistoryCode(owner);
	}

	@Override
	public void initOcsHistoryCode(CheckupInstance instance) {
		ocsInterface.initOcsHistoryCode(instance);
	}

	@Override
	public List<CheckupInstance> syncAndSearchInstance(String startDate,
			String endDate) {
		List<CheckupInstance> results = dao.findInstance(startDate, endDate);
		List<CheckupInstance> instances = new ArrayList<CheckupInstance>();
		
		for(CheckupInstance result : results){
			User user = result.getOwner();
			String patno = userService.findPatientNo(user);
			if(result.getAcptDate() == null){
				//1-1번 
				LinkReserveData reserveData = userDao.findReserveData(patno,new Date(),false);
				if(reserveData == null){
					//1.2번 오늘날짜에서 가장 가까운 예약일 예약정보를 얻는다.
					reserveData = userDao.findReserveData(patno, null, true);
				}
				
				if(reserveData != null){
					result.setAcptDate(reserveData.getAcptDate());
					result.setReserveDate(reserveData.getHopeDate());
					dao.saveInstance(result);
					
					user.setLastReserveDate(result.getReserveDate());
					user.setLastReserveNo(result.getReserveNo());
					userDao.updateUser(user);
				}
				instances.add(result);
			}else{
				instances.add(result);
			}
		}
		
		return instances;
	}
	
	
	
}
