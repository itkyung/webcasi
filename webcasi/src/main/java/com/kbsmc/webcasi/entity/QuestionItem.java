package com.kbsmc.webcasi.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.ValidatorType;


@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=QuestionItem.TABLE_NAME)
public class QuestionItem {
	public static final String TABLE_NAME = "CHECKUP_QUES_ITEM";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Expose
	private String id;
	
	@Enumerated(EnumType.STRING)
	@Expose
	private QuestionType type;
	
	@Expose
	private String title;
	@Expose
	private String preText;	//주관식의 경우에 입력필드 앞에 나올 추가글 
	@Expose
	private String postText; //주관식의 경우에 입력필드 뒤에 나올 추가글
	@Expose
	private String preText2;	//주관식 입력필드가 두개가 있을 경우에 앞에 나올 추가글 
	@Expose
	private String postText2; //주관식 입력필드가 두개가 있을 경우에 뒤에 나올 추가글 
	@Expose
	private int sortOrder;	//질문의 순번.
	@Expose
	private Date createDate;
	@Expose
	private Date updateDate;
	@Expose
	private boolean active;
	
	@Expose
	@ManyToOne
	private Question parentQuestion;
	
	@Expose
	private boolean existChildQuestion;	//하위에 추가질문이 존재하는지 여부.

	@Expose
	@ElementCollection
	@CollectionTable(name="CHECKUP_ITEM_SUB", joinColumns= @JoinColumn(name="ITEM_ID"))
	private List<QuestionEmbeddedItem> childItems;	//OBJ_RADIO처럼 객관식 항목에 추가로 붙는 객관식 항목을 저장하는 childItems
	
	@Expose
	@Enumerated(EnumType.STRING)
	private ValidatorType validator;	//Validator유형
	
	@Expose
	private Integer minRange;	//숫자인 경우 최소값 
	
	@Expose
	private Integer maxRange;	//숫자인 경우 최대값 
	
	@Expose
	private String ocsAskCode;	//AskCode값이 null이면 OCS에 I/F가 안되는 항목으로 처리함.
	
	@Expose
	private String ocsAnswer;	//OCS에 넘길 값. 필수입력.항목코드값임.
	
	@Expose
	private String ocsAskCode2;	//XXX_SUBJ_SUBJ처럼 주관식입력값이 두개인경우에는 각각마다 askcode가 존재해야한다.
	
	@Expose
	private String ocsAskCode3;	//주관식 입력값이 두개이면서 체크박스인경우에는 Askcode가 3개가 존재한다.
	
	@Expose
	private String ocsNoAnswerCode;	//OCS 미결코드. 아무것도 입력안했을때 넘길 코드값.

	@Expose
	private String ocsNoAnswerCode2;	//OCS 미결코드2. 아무것도 입력안했을때 넘길 코드값.
	
	@Expose
	private String ocsNoAnswerCode3;	//OCS 미결코드3. 아무것도 입력안했을때 넘길 코드값.
	
	@Expose
	private String thumnailImage;	//아이템에 이미지가 잇는 경우 이미지이름.소스상 resource에 이미 올라가있는 이미지의 이름./images/thumbnail/아래에 경로...
	
	@Expose
	@Enumerated(EnumType.STRING)
	private NutritionItemType itemGroup;	//영양설문에서만 이용하는 아이템의 그룹. 평균섭취빈도,평균섭취분량,섭취개월. 
	
	@Expose
	private Boolean noneFlag;	//없음이라는 항목 플래그. 예) 없음을 체크하면 다른 항목들은 다 선택불가가 되는것 구현이 이용함.
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPreText() {
		return preText;
	}

	public void setPreText(String preText) {
		this.preText = preText;
	}

	public String getPostText() {
		return postText;
	}

	public void setPostText(String postText) {
		this.postText = postText;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
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

	public Question getParentQuestion() {
		return parentQuestion;
	}

	public void setParentQuestion(Question parentQuestion) {
		this.parentQuestion = parentQuestion;
	}

	public boolean isExistChildQuestion() {
		return existChildQuestion;
	}

	public void setExistChildQuestion(boolean existChildQuestion) {
		this.existChildQuestion = existChildQuestion;
	}

	public List<QuestionEmbeddedItem> getChildItems() {
		return childItems;
	}

	public void setChildItems(List<QuestionEmbeddedItem> childItems) {
		this.childItems = childItems;
	}

	public String getPreText2() {
		return preText2;
	}

	public void setPreText2(String preText2) {
		this.preText2 = preText2;
	}

	public String getPostText2() {
		return postText2;
	}

	public void setPostText2(String postText2) {
		this.postText2 = postText2;
	}

	public ValidatorType getValidator() {
		return validator;
	}

	public void setValidator(ValidatorType validator) {
		this.validator = validator;
	}

	public String getOcsAskCode() {
		return ocsAskCode;
	}

	public void setOcsAskCode(String ocsAskCode) {
		this.ocsAskCode = ocsAskCode;
	}

	public String getOcsAnswer() {
		return ocsAnswer;
	}

	public void setOcsAnswer(String ocsAnswer) {
		this.ocsAnswer = ocsAnswer;
	}

	public String getOcsAskCode2() {
		return ocsAskCode2;
	}

	public void setOcsAskCode2(String ocsAskCode2) {
		this.ocsAskCode2 = ocsAskCode2;
	}

	public String getThumnailImage() {
		return thumnailImage;
	}

	public void setThumnailImage(String thumnailImage) {
		this.thumnailImage = thumnailImage;
	}

	public NutritionItemType getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(NutritionItemType itemGroup) {
		this.itemGroup = itemGroup;
	}

	public String getItemGroupStr(){
		return itemGroup == null ? null : itemGroup.name();
	}

	public String getOcsAskCode3() {
		return ocsAskCode3;
	}

	public void setOcsAskCode3(String ocsAskCode3) {
		this.ocsAskCode3 = ocsAskCode3;
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

	public Boolean getNoneFlag() {
		return noneFlag;
	}

	public void setNoneFlag(Boolean noneFlag) {
		this.noneFlag = noneFlag;
	}

	public Integer getMinRange() {
		return minRange;
	}

	public void setMinRange(Integer minRange) {
		this.minRange = minRange;
	}

	public Integer getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Integer maxRange) {
		this.maxRange = maxRange;
	}

	
}
