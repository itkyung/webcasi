package com.kbsmc.webcasi.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.identity.entity.User;

//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="INSTANCE")
/**
 * 결과테이블은 캐쉬에서 제거함.
 * @author bizwave
 *
 */
@Entity
@Table(name=QuestionResult.TABLE_NAME)
public class QuestionResult {
	public static final String TABLE_NAME = "CHECKUP_QUESTION_RESULT";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	@ManyToOne
	private CheckupInstance instacne;
	
	@ManyToOne
	private Question question;
	
	@Enumerated(EnumType.STRING)
	private QuestionType type;
	
	private String ocsAskCode;	//OCS AskCode
	
	private String ocsAskCode2;	//OCS AskCode2
	
	private String ocsAskCode3;	//OCS AskCode3
	
	private String ocsAskCode4;
	
	private String ocsNoAnswerCode;	//strValue가 null일때를 위한 noAnswerCode
	
	private String ocsNoAnswerCode2;	//strValu2가 null일때를 위한 noAnswerCode;
	
	private String ocsNoAnswerCode3;	//strValu3가 null일때를 위한 noAnswerCode;
	
	private String ocsObjectiveNoAnswerCode;	//실제 item항목(radio,check)를 위한 noAnswerCode;
	
	private String ocsObjectiveNoAnswerCode2;	//embedded item항목(radio,check)를 위한 noAnswerCode;
	
	private String ocsValue;	//OCS에 IF해 주어야할 결과값.항목코드값.
	
	private String ocsValue2;	//OCS에 IF해 주어야할 결과값.항목코드값. CHECK_SUBJ_RADIO_SUBJ같은 경우에 두번째 Radio의 선택값.
	
	
	private String strValue;	//Subjective등의 주관식 입력값.
	
	private Date dateValue;	//날짜 정보에 대한 입력값.
	
	private String strValue2;	//두번째 주관식에 대한 입력값.
	
	private String objectiveValue;	//객관식의 경우에 선택한 questionItemId 값.
	
	private String objectiveValue2;	//객관식 항목아래에 embedded로 들어있는 아이템의 key값.
	
	@ManyToOne
	private User owner;
	
	private Date createDate;
	
	private Date updateDate;
	
	private boolean active;	
	
	@Enumerated(EnumType.STRING)
	private NutritionItemType itemGroup;	//영양문진용.
	
	@Transient
	private DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	@Transient
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Transient
	private boolean ignoreUpdateObjectiveValue = false;	//OcsWebDb에 업데이트할때에 objecitiveValue를 update를 무시할지에 대한 부분. 라디오값의 선택변경 및 취소때문에 필요함.
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public String getOcsValue() {
		return ocsValue;
	}

	public void setOcsValue(String ocsValue) {
		this.ocsValue = ocsValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public String getStrValue2() {
		return strValue2;
	}

	public void setStrValue2(String strValue2) {
		this.strValue2 = strValue2;
	}

	public String getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(String objectiveValue) {
		this.objectiveValue = objectiveValue;
	}

	public CheckupInstance getInstacne() {
		return instacne;
	}

	public void setInstacne(CheckupInstance instacne) {
		this.instacne = instacne;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getObjectiveValue2() {
		return objectiveValue2;
	}

	public void setObjectiveValue2(String objectiveValue2) {
		this.objectiveValue2 = objectiveValue2;
	}
	
	public String getMonthValue(){
		if(dateValue != null){
			return monthFormat.format(dateValue);
		}
		return null;
	}
	
	public String getDayValue(){
		if(dateValue != null){
			return dateFormat.format(dateValue);
		}
		return null;		
	}

	public NutritionItemType getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(NutritionItemType itemGroup) {
		this.itemGroup = itemGroup;
	}

	public String getOcsAskCode() {
		return ocsAskCode;
	}

	public void setOcsAskCode(String ocsAskCode) {
		this.ocsAskCode = ocsAskCode;
	}

	public String getOcsAskCode2() {
		return ocsAskCode2;
	}

	public void setOcsAskCode2(String ocsAskCode2) {
		this.ocsAskCode2 = ocsAskCode2;
	}

	public String getOcsAskCode3() {
		return ocsAskCode3;
	}

	public void setOcsAskCode3(String ocsAskCode3) {
		this.ocsAskCode3 = ocsAskCode3;
	}

	public String getOcsAskCode4() {
		return ocsAskCode4;
	}

	public void setOcsAskCode4(String ocsAskCode4) {
		this.ocsAskCode4 = ocsAskCode4;
	}

	public String getOcsValue2() {
		return ocsValue2;
	}

	public void setOcsValue2(String ocsValue2) {
		this.ocsValue2 = ocsValue2;
	}

	public String getOcsNoAnswerCode() {
		return ocsNoAnswerCode;
	}

	public void setOcsNoAnswerCode(String ocsNoAnswerCode) {
		this.ocsNoAnswerCode = ocsNoAnswerCode;
	}

	public String getOcsNoAnswerCode2() {
		return ocsNoAnswerCode2;
	}

	public void setOcsNoAnswerCode2(String ocsNoAnswerCode2) {
		this.ocsNoAnswerCode2 = ocsNoAnswerCode2;
	}

	public String getOcsNoAnswerCode3() {
		return ocsNoAnswerCode3;
	}

	public void setOcsNoAnswerCode3(String ocsNoAnswerCode3) {
		this.ocsNoAnswerCode3 = ocsNoAnswerCode3;
	}

	public String getOcsObjectiveNoAnswerCode() {
		return ocsObjectiveNoAnswerCode;
	}

	public void setOcsObjectiveNoAnswerCode(String ocsObjectiveNoAnswerCode) {
		this.ocsObjectiveNoAnswerCode = ocsObjectiveNoAnswerCode;
	}

	public String getOcsObjectiveNoAnswerCode2() {
		return ocsObjectiveNoAnswerCode2;
	}

	public void setOcsObjectiveNoAnswerCode2(String ocsObjectiveNoAnswerCode2) {
		this.ocsObjectiveNoAnswerCode2 = ocsObjectiveNoAnswerCode2;
	}

	public boolean isIgnoreUpdateObjectiveValue() {
		return ignoreUpdateObjectiveValue;
	}

	public void setIgnoreUpdateObjectiveValue(boolean ignoreUpdateObjectiveValue) {
		this.ignoreUpdateObjectiveValue = ignoreUpdateObjectiveValue;
	}
	
	
	
}
