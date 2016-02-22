package com.kbsmc.webcasi.checkup.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceDAO;
import com.kbsmc.webcasi.checkup.ICheckupMasterDAO;
import com.kbsmc.webcasi.checkup.IOCSInterfaceService;
import com.kbsmc.webcasi.checkup.IResultUpdateService;
import com.kbsmc.webcasi.checkup.Su2Qustt;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.OCSHistoryCode;
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
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.identity.impl.UserDAO;
import com.kbsmc.webcasi.ui.QuestionSubmitModel;

@Service
public class OCSInterfaceService implements IOCSInterfaceService{
	private Log log = LogFactory.getLog(OCSInterfaceService.class);
	
	@Autowired private ICheckupInstanceDAO dao;
	@Autowired private ICheckupMasterDAO masterDao;
	@Autowired private IResultUpdateService resultUpdateService;
	@Autowired private IUserService userService;
	@Autowired private IUserDAO userDao;
	
	@Transactional
	@Override
	public void updateOcsRow(User user,String patientNo, String version, String askCode,
			String answer, boolean active, Date hopeDate,CheckupInstance instance){
		updateOcsRow(user,patientNo,version,askCode,answer,active,hopeDate,null,false,instance);
	}
	
	private void updateOcsRowByNative(User user,String patientNo, String version, String askCode,
			String answer, boolean active, Date hopeDate,Date acptDate,boolean realtimeUpdate,CheckupInstance instance) {
	
		if(askCode == null || "".equals(askCode)) return;
		
		boolean isCreate = false;
		//String id = dao.getIdOcsResult(patientNo, version, askCode, realtimeUpdate, acptDate);
		String id = dao.getIdOcsResult(instance,askCode);
		
		if(id == null){
			OCSWebResult row = new OCSWebResult();
			String resno = user.getResno();
			if(resno != null && resno.length() == 13){
				row.setResno("a" + resno.substring(0,5));	
				row.setResno2("b" + resno.substring(5,10));
				row.setResno3("c" + resno.substring(10));
			}
			
			row.setPatientNo(patientNo);
			row.setPageCd(version);
			row.setAskCode(askCode);
			row.setAskSeq(1);
			row.setQstNo("K");
			row.setEditId("web");
			row.setEntId("web");
			row.setInstance(instance);
			
			row.setEntDate(new Date());
			if(realtimeUpdate)	// 당일날 접수와 동시에 작성하는 경우
				row.setItfYn("Y");
			else
				row.setItfYn("N");
			row.setInkbn(user.getInkbn());
			if(realtimeUpdate)
				row.setAcptDate(acptDate);	
			else
				row.setAcptDate(hopeDate); //여기서 hopeDate로 넣는 이유는 칼로리계산때문이다. realtime update가 아닐때에는 acptDate가 null이기 때문이다.
			
			row.setAnswer(answer);
			row.setEditDate(new Date());
			
			isCreate = true;
			dao.createOcsResult(row);
		}else{
			//업데이트한다
			OCSWebResult result = dao.loadOcsResult(id);
			result.setAnswer(answer);
			result.setEditDate(new Date());
			
			dao.saveOcsResult(result);
			
		}
		
		if(realtimeUpdate){
			updateRemoteOcsRow(patientNo,version,askCode,answer,acptDate);
		}
		
		
	}
	
	
	private void updateOcsRow(User user,String patientNo, String version, String askCode,
		String answer, boolean active, Date hopeDate,Date acptDate,boolean realtimeUpdate,CheckupInstance instance) {
		
		//askCode가 없으면 그냥 아무것도 하지 않는다.
		if(askCode == null || "".equals(askCode)) return;
		try{
		
			boolean isCreate = false;
			//OCSWebResult row = dao.findOcsResult(patientNo, version, askCode, realtimeUpdate,acptDate);
			OCSWebResult row = dao.findOcsResult(instance,askCode);
			if(!active){
				if(row != null){
					//Active가 false이면 삭제한다. active여부와는 관계없이 처리한다. 
	//				dao.removeOcsResult(row);
	//				if(realtimeUpdate){
	//					removeRemoteOcsRow(patientNo,version,askCode,answer,acptDate);
	//				}
				}
	//			return;
			}
			
			if(row == null){
				row = new OCSWebResult();
				String resno = user.getResno();
				if(resno != null && resno.length() == 13){
					row.setResno("a" + resno.substring(0,5));	
					row.setResno2("b" + resno.substring(5,10));
					row.setResno3("c" + resno.substring(10));
				}
				
				row.setPatientNo(patientNo);
				row.setPageCd(version);
				row.setAskCode(askCode);
				row.setAskSeq(1);
				row.setQstNo("K");
				row.setEditId("web");
				row.setEntId("web");
				row.setInstance(instance);
				row.setEntDate(new Date());
				if(realtimeUpdate)	// 당일날 접수와 동시에 작성하는 경우
					row.setItfYn("Y");
				else
					row.setItfYn("N");
				row.setInkbn(user.getInkbn());
				if(realtimeUpdate)
					row.setAcptDate(acptDate);	
				else
					row.setAcptDate(hopeDate); //여기서 hopeDate로 넣는 이유는 칼로리계산때문이다. realtime update가 아닐때에는 acptDate가 null이기 때문이다.
				
				isCreate = true;
			}
			
			row.setAnswer(answer);
			row.setEditDate(new Date());
			
			if(isCreate){
				dao.createOcsResult(row);
			}else{
				dao.saveOcsResult(row);
			}
			
			if(realtimeUpdate){
				updateRemoteOcsRow(patientNo,version,askCode,answer,acptDate);
			}
		}catch(Exception e){
			//중복에러가 나도 로그만 남기고 무시함.
			log.error("Checkup_su2qustt Insert error : ",e);
		}
	}
	
	/**
	 * DB Link로 연결되어있는 su2qustt에 직접 업데이트한다.
	 * @param patientNo
	 * @param version
	 * @param askCode
	 * @param answer
	 * @param acptDate
	 */
	private void updateRemoteOcsRow(String patientNo, String version, String askCode,
			String answer, Date acptDate){
		dao.updateRemoteSu2qustt(patientNo, acptDate, askCode, answer, version);
	}
	
	private void removeRemoteOcsRow(String patientNo, String version, String askCode,
			String answer, Date acptDate){
		dao.removeRemoteSu2qustt(patientNo, acptDate, askCode, answer, version);
	}
	
	/*
	 * OCS DB로 부터 과거 문진데이타를 가져온다.
	 * 과거 데이타가 없으면 미결코드로 초기화한다.
	 */
	@Transactional
	@Override
	public void initFromOCSHistory(User user, CheckupInstance instance,
		Question question, QuestionItem item, String ocsAskCode,
		String noAnswerCode, String patientNo,OCSHistoryCode su2qustt) throws Exception {
	
	
		QuestionSubmitModel submitModel = new QuestionSubmitModel();
		
		QuestionType type = null;
		if(item != null){
			type = item.getType();
		}else{
			type = question.getType();
		}
		submitModel.setType(type.name());
		boolean needUpdate = false;
		
		switch(type){
		case CHECK:
		case CHECK_VER:
		case CHECK_SUBJ:
		case CHECK_SUBJ_SUBJ:
		case CHECK_SUBJ_1:
		case CHECK_SUBJ_RADIO_SUBJ:
			if(item != null){
				needUpdate = makeCheckModel(type,submitModel,su2qustt,question,item);
			}
			break;
		case RADIO:
		case RADIO_HOR:
		case RADIO_IMAGE:
		case RADIO_RADIO:
		case RADIO_SUBJ_1:
		case RADIO_SUBJ_HOUR_MINUTE:
			if(item != null){
				needUpdate = makeRadioModel(type,submitModel,su2qustt,question,item);
			}
			break;
		case OBJ_RADIO:
		case OBJ_RADIO_SUBJ:
			if(item != null){
				needUpdate = makeObjModel(type,submitModel,su2qustt,question,item);
			}
			break;
		case SUBJECTIVE:
		case SUBJECTIVE_YEAR:
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_MONTH_DATE_RANGE:
//			case SUBJECTIVE_YEAR_MONTH:	//Date형의 경우에는 과거데이타를 가져오지 않는다. 복잡함..
//			case SUBJECTIVE_YEAR_MONTH_DAY:
		case SUBJECTIVE_YEAR_MONTH_RANGE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:
		case TEXT_AREA:
			needUpdate = makeSubjectiveModel(type,submitModel,su2qustt,question);
			break;
		default :
			return;
		}
		submitModel.setQuestionId(question.getId());
		
		if(needUpdate)
			resultUpdateService.updateResult(user, instance, submitModel);
	
		
	}
		
	private boolean makeSubjectiveModel(QuestionType type,QuestionSubmitModel model,OCSHistoryCode su2qustt,Question question){
		boolean needUpdate = false;
		
		switch(type){
		case SUBJECTIVE:
		case TEXT_AREA:
		case SUBJECTIVE_YEAR:
			model.setStrValue(su2qustt.getOcsAnswer());
			needUpdate = true;
			break;
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:	
		case SUBJECTIVE_MONTH_DATE_RANGE:
		case SUBJECTIVE_YEAR_MONTH_RANGE:
			if(su2qustt.getOcsAskCode().equals(question.getOcsAskCode())){
				model.setStrValue(su2qustt.getOcsAskCode());
			}else{
				model.setStrValue2(su2qustt.getOcsAskCode());
			}
			needUpdate = true;
			break;
		}
		return needUpdate;
	}
	
	private boolean makeCheckModel(QuestionType type,QuestionSubmitModel model,OCSHistoryCode su2qustt,Question question,QuestionItem item){
		boolean needUpdate = false;
		
		switch(type){
		case CHECK:
		case CHECK_VER:
			if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
				model.setQuestionItemId(item.getId());
				needUpdate = true;
			}
			break;
		case CHECK_SUBJ:
		case CHECK_SUBJ_1:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode2())){
				model.setStrValue(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			break;
		case CHECK_SUBJ_SUBJ:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode2())){
				model.setStrValue(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode3())){
				model.setStrValue2(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			break;
		case CHECK_SUBJ_RADIO_SUBJ:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode2())){
				model.setStrValue(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode3())){
				model.setStrValue2(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			for(QuestionEmbeddedItem eItem : item.getChildItems()){
				if(su2qustt.getOcsAskCode().equals(eItem.getOcsAskCode()) && 
						su2qustt.getOcsAnswer().equals(eItem.getOcsAnswer())){
					model.setEmbededItemId(eItem.getKey());
					needUpdate = true;
				}
			}
			break;
		
		}
		return needUpdate;
	}
	
	
	private boolean makeRadioModel(QuestionType type,QuestionSubmitModel model,OCSHistoryCode su2qustt,Question question,QuestionItem item){
		boolean needUpdate = false;
		
		switch(type){
		case RADIO:
		case RADIO_HOR:
		case RADIO_IMAGE:
			if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
				model.setQuestionItemId(item.getId());
				needUpdate = true;
			}
			break;
		case RADIO_RADIO:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			for(QuestionEmbeddedItem eItem : item.getChildItems()){
				if(su2qustt.getOcsAskCode().equals(eItem.getOcsAskCode()) && 
						su2qustt.getOcsAnswer().equals(eItem.getOcsAnswer())){
					model.setEmbededItemId(eItem.getKey());
					needUpdate = true;
				}
			}
			
			break;
		case RADIO_SUBJ_1:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode2())){
				model.setStrValue(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			break;
		case RADIO_SUBJ_HOUR_MINUTE:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode2())){
				model.setStrValue(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode3())){
				model.setStrValue2(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			break;
		}
		
		
		return needUpdate;
	}
	
	private boolean makeObjModel(QuestionType type,QuestionSubmitModel model,OCSHistoryCode su2qustt,Question question,QuestionItem item){
		boolean needUpdate = false;
		
		switch(type){
		case OBJ_RADIO:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			for(QuestionEmbeddedItem eItem : item.getChildItems()){
				if(su2qustt.getOcsAskCode().equals(eItem.getOcsAskCode()) && 
						su2qustt.getOcsAnswer().equals(eItem.getOcsAnswer())){
					model.setEmbededItemId(eItem.getKey());
					needUpdate = true;
				}
			}
			
			
			break;
		case OBJ_RADIO_SUBJ:
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode())){
				if(su2qustt.getOcsAnswer().equals(item.getOcsAnswer())){
					model.setQuestionItemId(item.getId());
					needUpdate = true;
				}
			}
			if(su2qustt.getOcsAskCode().equals(item.getOcsAskCode2())){
				model.setStrValue(su2qustt.getOcsAnswer());
				needUpdate = true;
			}
			
			for(QuestionEmbeddedItem eItem : item.getChildItems()){
				if(su2qustt.getOcsAskCode().equals(eItem.getOcsAskCode()) && 
						su2qustt.getOcsAnswer().equals(eItem.getOcsAnswer())){
					model.setEmbededItemId(eItem.getKey());
					needUpdate = true;
				}
			}
			break;
		}
		
		return needUpdate;
	}
	
	@Transactional
	@Override
	public void initOcsResultSubRowFromHistory(Question question, String patientNo,
			String version, Date acptDate, User user, CheckupInstance instance,boolean useNoAnswerCode)
			throws Exception {
		OCSHistoryCode su2qustt = null;
		OCSHistoryCode su2qustt2 = null;
		OCSHistoryCode su2qustt3 = null;
		
		for(QuestionItem item : question.getChildItems()){
			if(item.isActive() == false) continue;
			switch(item.getType()){
			case RADIO:
			case RADIO_HOR:
			case RADIO_IMAGE:
			case CHECK:
			case CHECK_VER:
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					//과거 데이타를 가져와야한다.
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(), useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",true,acptDate);
//				}
				break;
			case RADIO_SUBJ_1:
			case CHECK_SUBJ:
			case CHECK_SUBJ_1:
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(), useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",true,acptDate);
//				}
				
				su2qustt2 = dao.findOcsHistory(user, item.getOcsAskCode2());
				if(su2qustt2 != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode2(),useNoAnswerCode ? item.getOcsNoAnswerCode2() : "1",patientNo,su2qustt2);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode2(), useNoAnswerCode ? item.getOcsNoAnswerCode2() : "1",true,acptDate);
//				}
				break;
			case CHECK_SUBJ_SUBJ:
			case RADIO_SUBJ_HOUR_MINUTE:
				
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(), useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",true,acptDate);
//				}
				
				su2qustt2 = dao.findOcsHistory(user, item.getOcsAskCode2());
				if(su2qustt2 != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode2(),item.getOcsNoAnswerCode2(),patientNo,su2qustt2);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode2(), item.getOcsNoAnswerCode2(),true,acptDate);
//				}
				
				su2qustt3 = dao.findOcsHistory(user, item.getOcsAskCode3());
				if(su2qustt3 != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode3(),item.getOcsNoAnswerCode3(),patientNo,su2qustt3);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode3(), item.getOcsNoAnswerCode3(),true,acptDate);
//				}
				break;
			case RADIO_RADIO:
			case OBJ_RADIO:
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),item.getOcsNoAnswerCode(),patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(), item.getOcsNoAnswerCode(),true,acptDate);
//				}
//				for(QuestionEmbeddedItem eItem : item.getChildItems()){
//					updateOcsRow(user,patientNo, version, eItem.getOcsAskCode(), eItem.getOcsNoAnswerCode(),true,acptDate);
//				}
				break;
			case OBJ_RADIO_SUBJ:
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),item.getOcsNoAnswerCode(),patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(), item.getOcsNoAnswerCode(),true,acptDate);
//				}
				
				su2qustt2 = dao.findOcsHistory(user, item.getOcsAskCode2());
				if(su2qustt2 != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode2(),item.getOcsNoAnswerCode2(),patientNo,su2qustt2);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode2(), item.getOcsNoAnswerCode2(),true,acptDate);
//				}
				
//				for(QuestionEmbeddedItem eItem : item.getChildItems()){
//					updateOcsRow(user,patientNo, version, eItem.getOcsAskCode(), eItem.getOcsNoAnswerCode(),true,acptDate);
//				}
				break;
			case CHECK_SUBJ_RADIO_SUBJ:
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),item.getOcsNoAnswerCode(),patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(), item.getOcsNoAnswerCode(),true,acptDate);
//				}
				
				su2qustt2 = dao.findOcsHistory(user, item.getOcsAskCode2());
				if(su2qustt2 != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode2(),item.getOcsNoAnswerCode2(),patientNo,su2qustt2);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode2(), item.getOcsNoAnswerCode2(),true,acptDate);
//				}
				
				su2qustt3 = dao.findOcsHistory(user, item.getOcsAskCode3());
				if(su2qustt3 != null){
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode3(),item.getOcsNoAnswerCode3(),patientNo,su2qustt3);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode3(), item.getOcsNoAnswerCode3(),true,acptDate);
//				}
				
//				for(QuestionEmbeddedItem eItem : item.getChildItems()){
//					updateOcsRow(user,patientNo, version, eItem.getOcsAskCode(), eItem.getOcsNoAnswerCode(),true,acptDate);
//				}
					
				break;
			case SUBJECTIVE:
			case TEXT_AREA:
			case SUBJECTIVE_YEAR:
				su2qustt = dao.findOcsHistory(user, item.getOcsAskCode());
				if(su2qustt != null){
					//과거 데이타를 가져와야한다.
					initFromOCSHistory(user, instance, question, item,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
				}
//				else{
//					updateOcsRow(user,patientNo, version, item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1",true,acptDate);
//				}
				break;
			
			}
		}
		
	}
	
	/**
	 * 과거 문진이력으로부터 데이타를 가져온다.
	 * @param question
	 * @param patientNo
	 * @param version
	 * @param acptDate
	 * @param user
	 * @param instance
	 * @param useNoAnswerCode
	 * @throws Exception
	 */
	@Transactional
	@Override
	public void initOcsWebResultFromHistory(Question question, String patientNo,
			String version, Date acptDate, User user, CheckupInstance instance,boolean useNoAnswerCode)
			throws Exception {
		OCSHistoryCode su2qustt = null;
		OCSHistoryCode su2qustt2 = null;
		OCSHistoryCode su2qustt3 = null;
		
		switch(question.getType()){
		case SUBJECTIVE:
		case TEXT_AREA:
		case SUBJECTIVE_YEAR:
			su2qustt = dao.findOcsHistory(user, question.getOcsAskCode());
			if(su2qustt != null){
				initFromOCSHistory(user, instance, question, null,question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
			}
//			else{
//				updateOcsRow(user,patientNo, version, question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1",true,acptDate);
//			}
			break;
		case SUBJECTIVE_YEAR_MONTH_RANGE:
		case SUBJECTIVE_YEAR_MONTH:
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:
		case SUBJECTIVE_MONTH_DATE_RANGE:
			su2qustt = dao.findOcsHistory(user, question.getOcsAskCode());
			if(su2qustt != null){
				initFromOCSHistory(user, instance, question, null,question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
			}
//			else{
//				updateOcsRow(user,patientNo, version, question.getOcsAskCode(), useNoAnswerCode ? question.getOcsNoAnswerCode() : "1",true,acptDate);
//			}
			
			su2qustt2 = dao.findOcsHistory(user, question.getOcsAskCode2());
			if(su2qustt2 != null){
				initFromOCSHistory(user, instance, question, null,question.getOcsAskCode2(),useNoAnswerCode ? question.getOcsNoAnswerCode2() : "1",patientNo,su2qustt2);
			}
//			else{
//				updateOcsRow(user,patientNo, version, question.getOcsAskCode2(), useNoAnswerCode ? question.getOcsNoAnswerCode2() : "1",true,acptDate);
//			}
			
			break;
		case SUBJECTIVE_YEAR_MONTH_DAY:
			su2qustt = dao.findOcsHistory(user, question.getOcsAskCode());
			if(su2qustt != null){
				initFromOCSHistory(user, instance, question, null,question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1",patientNo,su2qustt);
			}
//			else{
//				updateOcsRow(user,patientNo, version, question.getOcsAskCode(), useNoAnswerCode ? question.getOcsNoAnswerCode() : "1",true,acptDate);
//			}
			
			su2qustt2 = dao.findOcsHistory(user, question.getOcsAskCode2());
			if(su2qustt2 != null){
				initFromOCSHistory(user, instance, question, null,question.getOcsAskCode2(),question.getOcsNoAnswerCode2(),patientNo,su2qustt2);
			}
//			else{
//				updateOcsRow(user,patientNo, version, question.getOcsAskCode2(), question.getOcsNoAnswerCode2(),true,acptDate);
//			}
			
			su2qustt3 = dao.findOcsHistory(user, question.getOcsAskCode3());
			if(su2qustt3 != null){
				initFromOCSHistory(user, instance, question, null,question.getOcsAskCode3(),question.getOcsNoAnswerCode3(),patientNo,su2qustt3);
			}
//			else{
//				updateOcsRow(user,patientNo, version, question.getOcsAskCode3(), question.getOcsNoAnswerCode3(),true,acptDate);
//			}
			
			break;
		default:	
			initOcsResultSubRowFromHistory(question,patientNo,version,acptDate,user,instance,useNoAnswerCode);
			break;
		}
		
	}
	
	/**
	 * 
	 * 여기서 실제로는 해당페이지 질문에 대해서 과거 문진이력데이타를 동기화한다.
	 * 미결코드 초기화는 syncOcsWeb으로 통합한다. --> 비동기에서 동기화로 바꿈.
	 */
	
//	@Async
	@Transactional
	@Override
	public void initOcsWebResultFromHistory(CheckupInstance instance, User user,QuestionGroup group) throws Exception {

		OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, group);
		if(progress != null)
			return;
		
		boolean useNoAnswerCode = true;
		QuestionGroupType type = group.getGroupType();
		switch(type){
		
		case NUTRITION_2:
			//일반 음식유형인 경우
			useNoAnswerCode = false;
			break;
		}
		
		initOcsWebResultGroupFromHistory(instance,user,group,useNoAnswerCode);
		
		progress = new OCSWebResultProgress();
		progress.setInstance(instance);
		progress.setOwner(user);
		progress.setQuestionGroup(group);
		//이때에는 lastUdateDate와 syncDate를 다 null로 둔다.
		dao.createOcsResult(progress);
	}

	
	
	/**
	 * Result와 OcsWebDB를 sync처리한다.
	 * 모든질문들에 대해서 미결코드를 얻고 질문에 응답한것은 정상적인값으로 그외에는 미결코드로 넣는다.
	 */

	@Transactional(timeout=-1)
	@Override
	public void syncAllOcsWebDb(CheckupInstance instance) throws Exception{
		User user = instance.getOwner();
		
		List<Category> categories = masterDao.findCategory(instance.getMaster());
		
		for(Category category : categories){
			if(category.getType().equals(CategoryType.NUTRITION)){
				if(instance.isSkipNutrition()) continue;
				//영양을 스킵하는 경우는 영양문진은 넘어간다.
			}
			if(category.getType().equals(CategoryType.SLEEP)){
				if(!instance.isNeedSleepTest()) continue;
			}
			if(category.getType().equals(CategoryType.STRESS)){
				if(!instance.isNeedStressTest()) continue;
			}
			if(category.getType().equals(CategoryType.HEALTH_AGE)){
				if(!instance.isSamsungEmployee()) continue;
			}
			
		
			
			
			Map<String,String> ocsAnswerMap = makeOcsNoAnswerMap(category,false,user.getGender());
			
			List<QuestionResult> results = dao.findResult(user, instance, category);
			//일단 result를 가져온 이후에 process의 날짜를 바꾼다.
			
			
			String version = instance.getMaster().getVersion();
			List<EmbeddedPatientNo> nos = user.getPatientNos();
			EmbeddedPatientNo no = nos.get(nos.size()-1);
			
			for(QuestionResult result : results){
				if(result.getOcsAskCode() == null && result.getOcsAskCode2() == null) continue;
				putOcsValue(ocsAnswerMap,result);
			}	
			
			List<NurseCheckResult> nurseResults = dao.findNurseResult(instance, category);
			for(NurseCheckResult result : nurseResults){
				if(result.getOcsAskCode() == null) continue;
				putOcsValue(ocsAnswerMap, result);
			}
			
			changeNutritionException(ocsAnswerMap);
			
			for(String askCode : ocsAnswerMap.keySet()){
				// 로그
				log.warn(askCode + " : " + ocsAnswerMap.get(askCode));
				updateOcsRowByNative(user,no.getPatientNo(),version,askCode,ocsAnswerMap.get(askCode),true,instance.getReserveDate(),instance.getAcptDate(),instance.getAcptDate()==null?false:true,instance);
			}
			
			List<QuestionGroup> groups = masterDao.findQuestionGroup(category, user.getGender(), instance.isSkipStress());
			
			for(QuestionGroup group : groups){
				
				OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, group);
				if(progress == null){
					progress = new OCSWebResultProgress();
					progress.setInstance(instance);
					progress.setOwner(user);
					progress.setQuestionGroup(group);
					
					dao.createOcsResult(progress);
				}
				//해당 질문그룹의 데이타 동기화는 동시에 하나만 진행한다.
			//	synchronized (progress) {
					try{
//						if(progress.getLastSyncDate() != null && progress.getLastUpdateDate() != null && 
//								(progress.getLastSyncDate().after(progress.getLastUpdateDate()) || progress.getLastSyncDate().equals(progress.getLastUpdateDate()))){
//							//마지막 동기화 날짜가 마지막 수정날짜보다 이후이이거나 같으면 다시 동기화를 하지 않는다.
//							return;
//						}
						
					
						
						progress.setLastUpdateDate(new Date());
						progress.setLastSyncDate(new Date());
						dao.saveOcsResult(progress);
						
						
						
						
					}catch(Exception e){
						log.error("Exception :", e);
						throw e;
					}
				//}
				
			}
			
		}
		
		
	}

	/**
	 * Result와 OcsWebDB를 sync처리한다.
	 * 우선 해당페이지의 모든 질문들에 대해서 미결코드를 얻고 질문에 응답한것은 정상적인값으로 그외에는 미결코드로 넣는다.
	 */
	@Async
	@Transactional
	public void syncOcsWebDb(CheckupInstance instance, User user,QuestionGroup group) throws Exception{
		
		OCSWebResultProgress progress = dao.findWebResultProgress(instance, user, group);
		if(progress == null){
			progress = new OCSWebResultProgress();
			progress.setInstance(instance);
			progress.setOwner(user);
			progress.setQuestionGroup(group);
			
			dao.createOcsResult(progress);
		}
		//해당 질문그룹의 데이타 동기화는 동시에 하나만 진행한다.
		synchronized (progress) {
			try{
				if(progress.getLastSyncDate() != null && progress.getLastUpdateDate() != null && 
						(progress.getLastSyncDate().after(progress.getLastUpdateDate()) || progress.getLastSyncDate().equals(progress.getLastUpdateDate()))){
					//마지막 동기화 날짜가 마지막 수정날짜보다 이후이이거나 같으면 다시 동기화를 하지 않는다.
					return;
				}
				
				boolean useNoAnswerCode = true;
				QuestionGroupType type = group.getGroupType();
				switch(type){
				
				case NUTRITION_2:
					//일반 음식유형인 경우
					useNoAnswerCode = false;
					break;
				}
				
				Map<String,String> ocsAnswerMap = makeOcsNoAnswerMap(group, useNoAnswerCode,false);
				
				List<QuestionResult> results = dao.findResult(user, instance, group, 0);
				//일단 result를 가져온 이후에 process의 날짜를 바꾼다.
				
				progress.setLastUpdateDate(new Date());
				progress.setLastSyncDate(new Date());
				dao.saveOcsResult(progress);
				
				
				String version = instance.getMaster().getVersion();
				List<EmbeddedPatientNo> nos = user.getPatientNos();
				EmbeddedPatientNo no = nos.get(nos.size()-1);
				
				for(QuestionResult result : results){
					if(result.getOcsAskCode() == null && result.getOcsAskCode2() == null) continue;
					putOcsValue(ocsAnswerMap,result);
				}	
				
				List<NurseCheckResult> nurseResults = dao.findNurseResult(instance, group, -1);
				for(NurseCheckResult result : nurseResults){
					if(result.getOcsAskCode() == null) continue;
					putOcsValue(ocsAnswerMap, result);
				}
				
				//여기에서 영양의 예외로직을 적용해서 map값을 바꾼다.
				//changeNutritionException(ocsAnswerMap);
				
				for(String askCode : ocsAnswerMap.keySet()){
					// 로그
					log.warn(askCode + " : " + ocsAnswerMap.get(askCode));
					updateOcsRow(user,no.getPatientNo(),version,askCode,ocsAnswerMap.get(askCode),true,instance.getReserveDate(),instance.getAcptDate(),instance.getAcptDate()==null?false:true,instance);
				}
				
			}catch(Exception e){
				log.error("Exception :", e);
				throw e;
			}
		}
	}
	
	private void changeNutritionException(Map<String,String> ocsAnswerMap){
		
		for(String key : ocsAnswerMap.keySet()){
			if(key.endsWith("_AM")){
				//_AM으로 끝나면 섭취량이다.
				String value = ocsAnswerMap.get(key);
				if(value != null && Integer.parseInt(value) == 0){
					//섭취량이 0일때.
					ocsAnswerMap.put(key, ""+1);
					String pairKey = findPairKey(key);
					changePairValue(ocsAnswerMap,pairKey,false);
				}else if(value != null && Integer.parseInt(value) == 4){
					//섭취량이 4일때.
					ocsAnswerMap.put(key, ""+3);
					String pairKey = findPairKey(key);
					changePairValue(ocsAnswerMap,pairKey,true);
				}
			}
		}
	}
	
	private void changePairValue(Map<String,String> ocsAnswerMap,String pairKey,boolean isUp){
		String pairValue = ocsAnswerMap.get(pairKey);
		if(pairValue == null || "".equals(pairValue)) return;
		int iPairValue = Integer.parseInt(pairValue);
		
		if(isUp){
			if(iPairValue < 9){
				ocsAnswerMap.put(pairKey, (iPairValue+1) + "");
			}
		}else{
			if(iPairValue > 2){
				ocsAnswerMap.put(pairKey, (iPairValue-1) + "");
			}
		}
	}
	
	/**
	 * KF001_AM이면 KF001_FQ를 찾고  KF0100_AM이면 KF100_FQ를 찾는다. (100이상이면 앞에 0이 없다.)
	 * @param key
	 * @return
	 */
	private String findPairKey(String key){
		String prefix = key.substring(0, key.indexOf("_AM"));
		String alphaPrefix = prefix.substring(0,2);
		String numPrefix = prefix.substring(2);
		
		if(Integer.parseInt(numPrefix) > 100){
			return alphaPrefix + Integer.parseInt(numPrefix) + "_FQ";
		}else{
			return prefix + "_FQ";
		}
		
	}
	
	private void putOcsValue(Map<String,String> ocsAnswerMap,NurseCheckResult result){
		ocsAnswerMap.put(result.getOcsAskCode(), result.getOcsValue());
	}
	
	/**
	 * OCSAnswerMap에 result의 값들을 타입별로 넣는다.
	 * @param ocsAnswerMap
	 * @param result
	 */
	private void putOcsValue(Map<String,String> ocsAnswerMap,QuestionResult result){
		DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
		
		switch(result.getType()){
		case RADIO :
		case RADIO_HOR:
		case RADIO_IMAGE:
		case CHECK:
		case CHECK_VER:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(), result.getOcsValue());
			break;
		case CHECK_SUBJ:
		case CHECK_SUBJ_1:
		case RADIO_SUBJ_1:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(), result.getOcsValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(), result.getStrValue());
			break;
		case CHECK_SUBJ_SUBJ:
		case RADIO_SUBJ_HOUR_MINUTE:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(), result.getOcsValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(), result.getStrValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode3(), result.getStrValue2());
			break;
		case CHECK_SUBJ_RADIO_SUBJ:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(), result.getOcsValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(), result.getStrValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode3(), result.getStrValue2());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode4(), result.getOcsValue2());
			break;
		case RADIO_RADIO:
		case OBJ_RADIO:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(), result.getOcsValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(), result.getOcsValue2());
			break;
		case OBJ_RADIO_SUBJ:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(), result.getOcsValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(), result.getStrValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode3(), result.getOcsValue2());
			break;
		case SUBJECTIVE:
		case TEXT_AREA:
		case SUBJECTIVE_YEAR:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(),result.getStrValue());
			break;
		case SUBJECTIVE_YEAR_MONTH_RANGE:
		case SUBJECTIVE_MONTH_DATE_RANGE:
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(),result.getStrValue());
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(),result.getStrValue2());
			break;
		case SUBJECTIVE_YEAR_MONTH:
			String dateValue = fm.format(result.getDateValue());
			String[] dates = dateValue.split("-"); 
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(),dates[0]);
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(),dates[1]);
			
			break;
		case SUBJECTIVE_YEAR_MONTH_DAY:
			String dateValue2 = fm.format(result.getDateValue());
			String[] dates2 = dateValue2.split("-"); 
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode(),dates2[0]);
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode2(),dates2[1]);
			putAnswerMap(ocsAnswerMap,result.getOcsAskCode3(),dates2[2]);
			break;
		}
		
	}
	
	private void putAnswerMap(Map<String,String> ocsAnswerMap,String key,String value){
		if(key == null || value == null || "".equals(value)) return;
		ocsAnswerMap.put(key,value);
	}
	
	
	private Map<String,String> makeOcsNoAnswerMap(Category category,boolean nutritionNoAnswer,Gender gender){
		Map<String,String> noAnswerMap = new HashMap<String,String>();
		
		List<Question> questions = masterDao.findQuestions(category,gender);
		boolean useNoAnswerCode = true;
		
		for(Question question : questions){
			
			QuestionGroupType type = question.getParentGroup().getGroupType();
			switch(type){
			
			case NUTRITION_2:
				//일반 음식유형인 경우
				useNoAnswerCode = false;
				break;
			default : 
				useNoAnswerCode = true;
				break;
			}
			makeOcsNoAnswerMap(noAnswerMap,question,useNoAnswerCode,nutritionNoAnswer);
			makeNurseOcsNoAnswerMap(noAnswerMap,question);
		}
		
		return noAnswerMap;
	}
	
	/**
	 * 해당 질문그룹에 속하는 모든 질문들의 미결코드맵을 만든다.
	 * @param group
	 * @param nutritionNoAnswer 가 true이면 영양에서 응답하지 않겠습니다인 경우임.
	 * @return
	 */
	private Map<String,String> makeOcsNoAnswerMap(QuestionGroup group,boolean useNoAnswerCode,boolean nutritionNoAnswer){
		Map<String,String> noAnswerMap = new HashMap<String,String>();
		
		List<Question> questions = masterDao.findQuestions(group);
		
		for(Question question : questions){
			makeOcsNoAnswerMap(noAnswerMap,question,useNoAnswerCode,nutritionNoAnswer);
			makeNurseOcsNoAnswerMap(noAnswerMap,question);
		}
		
		return noAnswerMap;
	}
	
	private void makeNurseOcsNoAnswerMap(Map<String,String> noAnswerMap,Question question){
		if(question.getNurseQuestionType() == null) return;
		if( question.getNurseQuestionType().equals(QuestionType.CHECK)){
			putAnswerMap(noAnswerMap,question.getNurseOcsAskCode(),question.getNurseOcsNoAnswerCode());
		}else{
			for(QuestionEmbeddedItem item : question.getChildNurseItems()){
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),item.getOcsNoAnswerCode());
			}
		}
	}
	
	private void makeOcsNoAnswerMap(Map<String,String> noAnswerMap,Question question,boolean useNoAnswerCode,boolean nutritionNoAnswer){
		switch(question.getType()){
		case SUBJECTIVE:
		case TEXT_AREA:
		case SUBJECTIVE_YEAR:
			putAnswerMap(noAnswerMap,question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1");
			break;
		case SUBJECTIVE_YEAR_MONTH_RANGE:
		case SUBJECTIVE_YEAR_MONTH:
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:
		case SUBJECTIVE_MONTH_DATE_RANGE:
			putAnswerMap(noAnswerMap,question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1");
			putAnswerMap(noAnswerMap,question.getOcsAskCode2(),useNoAnswerCode ? question.getOcsNoAnswerCode2() : "1");
			break;
			
		case SUBJECTIVE_YEAR_MONTH_DAY:
			putAnswerMap(noAnswerMap,question.getOcsAskCode(),useNoAnswerCode ? question.getOcsNoAnswerCode() : "1");
			putAnswerMap(noAnswerMap,question.getOcsAskCode2(),useNoAnswerCode ? question.getOcsNoAnswerCode2() : "1");
			putAnswerMap(noAnswerMap,question.getOcsAskCode3(),useNoAnswerCode ? question.getOcsNoAnswerCode3() : "1");
			break;
			
		default:	
			makeOcsNoAnswerSubMap(noAnswerMap,question,useNoAnswerCode,nutritionNoAnswer);
			break;
		}
		
		
	}
	
	/**
	 * 해당 질문의 하위 item과 하위 질문에 대해서 미결코드 Map을 만든다.
	 * @param noAnswerMap
	 * @param question
	 * @param useNoAnswerCode
	 */
	private void makeOcsNoAnswerSubMap(Map<String,String> noAnswerMap,Question question,boolean useNoAnswerCode,boolean nutritionNoAnswer){
		for(QuestionItem item : question.getChildItems()){
			if(item.isActive() == false) continue;
			
			switch(item.getType()){
			case RADIO:
			case RADIO_HOR:
			case RADIO_IMAGE:
			case CHECK:
			case CHECK_VER:
				if(!useNoAnswerCode){
					if(item.getItemGroup() != null && item.getItemGroup().equals(NutritionItemType.FREQUENCY)){
						//영양의 섭취빈도는 1로.
						putAnswerMap(noAnswerMap,item.getOcsAskCode(),"1");
					}else{
						//그 이외에는 기본 미결코드로 입력한다.
						putAnswerMap(noAnswerMap,item.getOcsAskCode(),item.getOcsNoAnswerCode());
					}
				}else{
					//음주의 경우에는 useNoAnswerCode는 false인데 타입입 frequency인 경우에는 0으로 미결코드를 넣는다.
					//여기로 올때에 음주이외에도 있다. 즉 응답하지않겠습니다의 경우가 있기때문에 확인이 필요하다.
					if(nutritionNoAnswer){
						putAnswerMap(noAnswerMap,item.getOcsAskCode(),item.getOcsNoAnswerCode());
					}else{
						if(item.getItemGroup() != null && item.getItemGroup().equals(NutritionItemType.FREQUENCY)){
							putAnswerMap(noAnswerMap,item.getOcsAskCode(),"0");
						}else{
							putAnswerMap(noAnswerMap,item.getOcsAskCode(),item.getOcsNoAnswerCode());
						}
					}
				}
				break;
			case RADIO_SUBJ_1:
			case CHECK_SUBJ:
			case CHECK_SUBJ_1:
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1");
				putAnswerMap(noAnswerMap,item.getOcsAskCode2(),useNoAnswerCode ? item.getOcsNoAnswerCode2() : "1");
				break;
			case CHECK_SUBJ_SUBJ:
			case RADIO_SUBJ_HOUR_MINUTE:
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1");
				putAnswerMap(noAnswerMap,item.getOcsAskCode2(),useNoAnswerCode ? item.getOcsNoAnswerCode2() : "1");
				putAnswerMap(noAnswerMap,item.getOcsAskCode3(),useNoAnswerCode ? item.getOcsNoAnswerCode3() : "1");
				
				break;
			case RADIO_RADIO:
			case OBJ_RADIO:
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1");
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					putAnswerMap(noAnswerMap,eItem.getOcsAskCode(),useNoAnswerCode ? eItem.getOcsNoAnswerCode() : "1");
				}
				
				break;
			case OBJ_RADIO_SUBJ:
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1");
				putAnswerMap(noAnswerMap,item.getOcsAskCode2(),useNoAnswerCode ? item.getOcsNoAnswerCode2() : "1");
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					putAnswerMap(noAnswerMap,eItem.getOcsAskCode(),useNoAnswerCode ? eItem.getOcsNoAnswerCode() : "1");
				}

				break;
			case CHECK_SUBJ_RADIO_SUBJ:
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1");
				putAnswerMap(noAnswerMap,item.getOcsAskCode2(),useNoAnswerCode ? item.getOcsNoAnswerCode2() : "1");
				putAnswerMap(noAnswerMap,item.getOcsAskCode3(),useNoAnswerCode ? item.getOcsNoAnswerCode3() : "1");
				for(QuestionEmbeddedItem eItem : item.getChildItems()){
					putAnswerMap(noAnswerMap,eItem.getOcsAskCode(),useNoAnswerCode ? eItem.getOcsNoAnswerCode() : "1");
				}
					
				break;
			case SUBJECTIVE:
			case TEXT_AREA:
			case SUBJECTIVE_YEAR:
				putAnswerMap(noAnswerMap,item.getOcsAskCode(),useNoAnswerCode ? item.getOcsNoAnswerCode() : "1");
				break;
			}
			
		}
		
		for(Question child : question.getChildQuestions()){
			if(child.isActive() == false) continue;
			makeOcsNoAnswerMap(noAnswerMap,child,useNoAnswerCode,nutritionNoAnswer);
		}
	}
	
	
	
	/**
	 * 과거 문진이력으로부터 데이타를 가져온다.
	 * @param instance
	 * @param user
	 * @param group
	 * @param useNoAnswerCode - 정의되어있는 미결코드를 이용할지 여부. 영양을 위해서 필요하다.
	 * @throws Exception
	 */
	private void initOcsWebResultGroupFromHistory(CheckupInstance instance, User user,QuestionGroup group,boolean useNoAnswerCode) throws Exception {
		List<Question> questions = masterDao.findQuestions(group, user.getGender());
		
		String version = instance.getMaster().getVersion();
		List<EmbeddedPatientNo> nos = user.getPatientNos();
		EmbeddedPatientNo no = nos.get(nos.size()-1);
		
		for(Question question : questions){
			initOcsWebResultFromHistory(question,no.getPatientNo(),version,instance.getReserveDate(),user,instance,useNoAnswerCode);
		}	
	}
	
	/**
	 * 해당 카테고리안에 존재하는 모든 질문의 미결코드를 업데이트한다.
	 * 영양문진처럼 한번에 모든곳의 미결코드를 업데이트할 필요가 있다.
	 * 이때에는 미결코드를 그대로 DB 의 것을 이용한다. 예외없음.
	 */
	@Async
	@Transactional
	@Override
	public void initOcsWebResult(CheckupInstance instance, User user,
			Category category) throws Exception {
		List<QuestionGroup> groups = masterDao.findQuestionGroup(category, user.getGender());
		try{
			for(QuestionGroup group : groups){
				//새롭게 만들어지는 함수를 이용한다.
				boolean useNoAnswerCode = true;
				QuestionGroupType type = group.getGroupType();
				//응답않겠습니다를 통해서 올 경우에는 무조건 미결코드를 그대로 이용한다.)
				
	//			switch(type){
	//			
	//			case NUTRITION_2:
	//				//일반 음식유형인 경우도 (응답않겠습니다를 통해서 올경우에는 무조건 true이다)
	//				useNoAnswerCode = false;
	//				break;
	//			}
				
				Map<String,String> ocsAnswerMap = makeOcsNoAnswerMap(group, useNoAnswerCode,true);
				String version = instance.getMaster().getVersion();
				List<EmbeddedPatientNo> nos = user.getPatientNos();
				EmbeddedPatientNo no = nos.get(nos.size()-1);
				
				for(String askCode : ocsAnswerMap.keySet()){
					updateOcsRow(user,no.getPatientNo(),version,askCode,ocsAnswerMap.get(askCode),true,instance.getReserveDate(),instance.getAcptDate(),instance.getAcptDate()==null?false:true,instance);
				}
				
			}
		}catch(Exception e){
			log.error("Error :",e);
		}
	}

	
	/**
	 * WebDB안에 존재하는 OCS 관련된 테이블에 데이타를 update한다.
	 * @param user
	 * @param instance
	 * @param result
	 */
	@Transactional
	@Override
	public void updateOcsWebDb(User user, CheckupInstance instance,
			QuestionResult result) {
		DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
		String answer = null;
		
		QuestionType type = result.getType();
		String version = instance.getMaster().getVersion();
		List<EmbeddedPatientNo> nos = user.getPatientNos();
		EmbeddedPatientNo no = nos.get(nos.size()-1);
		
		boolean realtimeUpdate = false;
		if(instance.getAcptDate() != null){
			//instance의 acptDate가 null이 아니면 realtime으로 remote OCS DB에 업데이트해야한다.
			realtimeUpdate = true;
		}
		
		switch(type){
		case SUBJECTIVE:
		case TEXT_AREA:
		case SUBJECTIVE_YEAR:
			
			if( result.isActive() && result.getStrValue() != null && !"".equals(result.getStrValue())){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			
			break;
		
		case CHECK:
		case CHECK_VER:
		case RADIO:
		case RADIO_HOR:
		case RADIO_IMAGE:
			//active가 false이면 value를 미결코드로 바꾸어서 넘긴다.
			if(result.isActive()){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				if(result.isIgnoreUpdateObjectiveValue())
					break;
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsObjectiveNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			break;
		
		case CHECK_SUBJ:
		case CHECK_SUBJ_1:
		case RADIO_SUBJ_1:
			//active가 false이면 value를 미결코드로 바꾸어서 넘긴다.
			if(result.isActive()){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				if(result.getStrValue() != null && !"".equals(result.getStrValue())){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}else{
					//strValue가 없으면 미결코드로 다시 바꾼다.
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
			}else{
				if(!result.isIgnoreUpdateObjectiveValue()){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsObjectiveNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			break;
		
		case SUBJECTIVE_YEAR_MONTH_RANGE:
		case SUBJECTIVE_MONTH_DATE_RANGE:
		case SUBJECTIVE_HOUR_MINUTE:
		case SUBJECTIVE_HOUR_MINUTE_RANGE:
			if(result.isActive() && result.getStrValue() != null && !"".equals(result.getStrValue())){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			if(result.isActive() && result.getStrValue2() != null && !"".equals(result.getStrValue2())){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			break;
		case SUBJECTIVE_YEAR_MONTH:
			if(result.isActive() && result.getDateValue() != null){
				String dateValue = fm.format(result.getDateValue());
				String[] dates = dateValue.split("-");
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),dates[0],result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),dates[1],result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			break;
		case SUBJECTIVE_YEAR_MONTH_DAY:
			if(result.isActive() && result.getDateValue() != null){
				String dateValue = fm.format(result.getDateValue());
				String[] dates = dateValue.split("-");
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),dates[0],result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),dates[1],result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),dates[2],result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsNoAnswerCode3(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			
			break;
		
		case CHECK_SUBJ_SUBJ:
		case RADIO_SUBJ_HOUR_MINUTE:
			//active가 false이면 value를 미결코드로 바꾸어서 넘긴다.
			if(result.isActive()){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				if(result.getStrValue() != null && !"".equals(result.getStrValue())){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}else{
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				if(result.getStrValue2() != null && !"".equals(result.getStrValue2())){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getStrValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}else{
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}	
			}else{
				if(!result.isIgnoreUpdateObjectiveValue()){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsObjectiveNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			break;
		
		case RADIO_RADIO:
		case OBJ_RADIO:
			//active가 false이면 value를 미결코드로 바꾸어서 넘긴다.
			if(result.isActive()){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				if(result.getOcsValue2() != null)
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				if(!result.isIgnoreUpdateObjectiveValue()){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsObjectiveNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}
			break;
		
		case OBJ_RADIO_SUBJ:
			//active가 false이면 value를 미결코드로 바꾸어서 넘긴다.
			if(result.isActive()){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				if(result.getStrValue() != null && !"".equals(result.getStrValue())){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}else{
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				if(result.getOcsValue2() != null)
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				if(!result.isIgnoreUpdateObjectiveValue()){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsObjectiveNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsObjectiveNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				
			}
			break;
		
		case CHECK_SUBJ_RADIO_SUBJ:
			//active가 false이면 value를 미결코드로 바꾸어서 넘긴다.
			if(result.isActive()){
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				if(result.getStrValue() != null && !"".equals(result.getStrValue())){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}else{
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				if(result.getStrValue2() != null && !"".equals(result.getStrValue2())){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getStrValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}else{
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
				
				if(result.getOcsValue2() != null)
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode4(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			}else{
				
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsObjectiveNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsNoAnswerCode(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				if(!result.isIgnoreUpdateObjectiveValue()){
					updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode4(),result.getOcsObjectiveNoAnswerCode2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
				}
			}
			break;
		
		}
		
	}

	
	@Transactional
	@Override
	public void updateOcsWebDb(User user, CheckupInstance instance,
			NurseCheckResult result) {
		String version = instance.getMaster().getVersion();
		List<EmbeddedPatientNo> nos = user.getPatientNos();
		EmbeddedPatientNo no = nos.get(nos.size()-1);
		
		boolean realtimeUpdate = false;
		if(instance.getAcptDate() != null){
			//instance의 acptDate가 null이 아니면 realtime으로 remote OCS DB에 업데이트해야한다.
			realtimeUpdate = true;
		}
		updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
	}

	
	
	/**
	 * Background job으로 구동된다.
	 */
	@Async
	@Transactional
	@Override
	public void initOcsHistoryCode(CheckupInstance instance) {
		User user = instance.getOwner();
		String patientNo = userService.findPatientNo(user);
		
		dao.initOcsHistoryCode(user, patientNo,instance.getReserveDate());
		
		List<Category> categories = masterDao.findCategory(instance.getMaster());
		for(Category category : categories){
			List<QuestionGroup> groups = masterDao.findQuestionGroup(category, user.getGender());
			for(QuestionGroup group : groups){
				try{
					initOcsWebResultFromHistory(instance,user,group);
				}catch(Exception e){
					log.error(e);
				}
			}
		}
	}

	
	@Deprecated
	@Transactional
	@Override
	public void deleteOcsWebDb(User user, CheckupInstance instance,
			QuestionResult result) {
		
		String version = instance.getMaster().getVersion();
		List<EmbeddedPatientNo> nos = user.getPatientNos();
		EmbeddedPatientNo no = nos.get(nos.size()-1);
		
		DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
		QuestionType type = result.getType();
		
		boolean realtimeUpdate = false;
		if(instance.getAcptDate() != null){
			//instance의 acptDate가 null이 아니면 realtime으로 remote OCS DB에 업데이트해야한다.
			realtimeUpdate = true;
		}
		
		/**
		 * delete가 호출되면 result의 active가 false이기 때문에 updateOcsRow안에서 삭제된다.
		 */
		switch(type){
		case CHECK:
		case CHECK_VER:
		case RADIO:
		case RADIO_HOR:
		case RADIO_IMAGE:
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			break;
		
		case CHECK_SUBJ:
		case CHECK_SUBJ_1:
		case RADIO_SUBJ_1:
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			break;
		
		
		case CHECK_SUBJ_SUBJ:
		case RADIO_SUBJ_HOUR_MINUTE:
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getStrValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			break;
		
		case RADIO_RADIO:
		case OBJ_RADIO:
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			break;
		
		case OBJ_RADIO_SUBJ:
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			break;
		
		case CHECK_SUBJ_RADIO_SUBJ:
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode(),result.getOcsValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode2(),result.getStrValue(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode3(),result.getStrValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			updateOcsRow(user,no.getPatientNo(),version,result.getOcsAskCode4(),result.getOcsValue2(),result.isActive(),instance.getReserveDate(),instance.getAcptDate(),realtimeUpdate,instance);
			
			break;
		
		}
		
	}

	@Transactional
	@Override
	public void syncParentProtection(ParentProtection protection) {
		CheckupInstance instance = protection.getInstance();
		userDao.updateOcsUser(userService.findPatientNo(instance.getOwner()), instance.getReserveDate(), protection);
		
	}
	
	@Transactional
	@Override
	public void syncResultRequest(ResultRequest request) {
		CheckupInstance instance = request.getInstance();
		userDao.updateOcsUser(userService.findPatientNo(instance.getOwner()), instance.getReserveDate(),request);
	}
	
	
	
}
