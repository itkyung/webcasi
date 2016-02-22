package com.kbsmc.webcasi.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.ValidatorType;

/**
 * 문진의 질문 마스터
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=Question.TABLE_NAME)
public class Question {
	public static final String TABLE_NAME = "CHECKUP_QUESTION";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")		
	@Expose
	private String id;
	@Expose
	private String title;
	@Expose
	private String description;	//질문에 대한 부가 설명
	@Expose
	private String questionNo;	//질문 항목 번호 예)1-1.
	@Expose
	private int sortOrder;	//질문의 순번.
	@Expose
	private Date createDate;
	@Expose
	private Date updateDate;
	@Expose
	private boolean active;
	@Expose
	private String preText;	//주관식의 경우에 입력필드 앞에 나올 추가글 
	@Expose
	private String postText; //주관식의 경우에 입력필드 뒤에 나올 추가글
	
	@Expose
	@Enumerated(EnumType.STRING)
	private QuestionType type;	//질문의 유형
	@Expose
	private int depth;	//질문의 Depth
	@Expose
	@Enumerated(EnumType.STRING)
	private Gender gender;	//성별정보. ALL이면 모두, 그 이외에는 해당 성별만 의미있음.
	@Expose
	@ManyToOne
	private QuestionGroup parentGroup;	//속하는 질문그룹 
	
	/**
	 * 특정 질문이 2Depth이하일때에는 parentQuestion또는 parentItem중에 한가지는 꼭 존재해야한다.
	 */
	@ManyToOne
	@Expose
	private Question parentQuestion;	//2depth이하 질문일 경우 부모 질문이 존재하면 부모질문값.
	
	//@ManyToOne
	//private QuestionItem parentItem;	//2Depth이하 질문의 경우 부모 질문항목 값. 
	
	@Expose
	@ElementCollection
	@CollectionTable(name="CHECKUP_Q_ITEM_LINK", joinColumns= @JoinColumn(name="QUESTION_ID"))
	private List<String> parentItems;	//부모 질문항목값 id 리스트.  (quetionItemId를 리스트로넣는다.)
	
	
	@OneToMany(mappedBy="parentQuestion")
	@OrderBy("sortOrder")
	private List<Question> childQuestions;	//질문에 직접적으로 붙어있는 하위 질문리스트.
	
	
	@OneToMany(mappedBy="parentQuestion",fetch=FetchType.EAGER)
	@OrderBy("sortOrder")
	private List<QuestionItem> childItems;	//질문에 붙어있는 질문항목 리스트. 


	private boolean modified;	//기존버전에서 copy된 질문일 경우에 해당 질문이 그 이후에 수정되었는지에 대한 여부.
	@Expose
	@Enumerated(EnumType.STRING)
	private ValidatorType validator;	//Validator유형
	
	@Expose
	private Integer minRange;	//숫자인 경우 최소값 
	
	@Expose
	private Integer maxRange;	//숫자인 경우 최대값 
	
	@Expose
	private String ocsAskCode;	//OCS에 넘길 질문의 코드값.(필수 입력값)
	
	@Expose
	private String ocsAskCode2;	//OCS에 넘길 질문코드값2. 주관식이나 객관식중에서 값을 두개로 나누어서 입력받는 유형의 경우에 각 항목마다 OCS AskCode가 다르게 있다.
	
	@Expose
	private String ocsAskCode3;
	
	@Expose
	private String ocsNoAnswerCode;	//OCS 미결코드. 아무것도 입력안했을때 넘길 코드값.

	@Expose
	private String ocsNoAnswerCode2;	//OCS 미결코드2. 아무것도 입력안했을때 넘길 코드값.
	
	@Expose
	private String ocsNoAnswerCode3;
	
	@Expose
	@ManyToOne
	private HelpContents help;	//Help Contents정보.
	@Expose
	private boolean required;	//필수로 답을 해야하는 질문 여부.
	@Expose
	private boolean navigationStressFlag = false;	//스트레스 측정도구를 스킵할지 판단하는 질문인지여부. 
	@Expose
	private boolean navigationNutritionFlag = false;	//영양설문을 진행할지 여부를 판단하는 질문여부.
	
	@Expose
	private Boolean checkListRequired;	//체크리스트 카테고리 질문일경우에 검진당일에만 보여야할 질문여부, true면 검진당일에만 보이는 질문임.
	
	@Expose
	private QuestionType nurseQuestionType;	//체크리스트에서 간호사가 체크하는 항목 유형.
	
	@Expose
	@ElementCollection
	@CollectionTable(name="CHECKUP_QUES_NURSE_SUB", joinColumns= @JoinColumn(name="QUESTION_ID"))
	private List<QuestionEmbeddedItem> childNurseItems;	// 간호사 체크항목중에서 하위로 존재하는게 필요한 항목 아이템.
	
	@Expose
	private String nurseOcsAskCode;	//간호사 체크리스트용 OCS Code;
	
	@Expose
	private String nurseOcsAnswer;	//간호사 체크리스트용 OCS Answer;
	
	@Expose
	private String nurseOcsNoAnswerCode;	//간호사 체크리스트용 미결코드
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getQuestionNo() {
		return questionNo;
	}


	public void setQuestionNo(String questionNo) {
		this.questionNo = questionNo;
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


	public QuestionType getType() {
		return type;
	}


	public void setType(QuestionType type) {
		this.type = type;
	}


	public int getDepth() {
		return depth;
	}


	public void setDepth(int depth) {
		this.depth = depth;
	}


	public Gender getGender() {
		return gender;
	}


	public void setGender(Gender gender) {
		this.gender = gender;
	}


	public QuestionGroup getParentGroup() {
		return parentGroup;
	}


	public void setParentGroup(QuestionGroup parentGroup) {
		this.parentGroup = parentGroup;
	}


	public Question getParentQuestion() {
		return parentQuestion;
	}


	public void setParentQuestion(Question parentQuestion) {
		this.parentQuestion = parentQuestion;
	}


	
	public List<String> getParentItems() {
		return parentItems;
	}


	public void setParentItems(List<String> parentItems) {
		this.parentItems = parentItems;
	}


	public List<Question> getChildQuestions() {
		return childQuestions;
	}


	public void setChildQuestions(List<Question> childQuestions) {
		this.childQuestions = childQuestions;
	}


	
	public List<QuestionItem> getChildItems() {
		return childItems;
	}


	public void setChildItems(List<QuestionItem> childItems) {
		this.childItems = childItems;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public boolean isModified() {
		return modified;
	}


	public void setModified(boolean modified) {
		this.modified = modified;
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


	public HelpContents getHelp() {
		return help;
	}


	public void setHelp(HelpContents help) {
		this.help = help;
	}


	public boolean isRequired() {
		return required;
	}


	public void setRequired(boolean required) {
		this.required = required;
	}


	public boolean isNavigationStressFlag() {
		return navigationStressFlag;
	}


	public void setNavigationStressFlag(boolean navigationStressFlag) {
		this.navigationStressFlag = navigationStressFlag;
	}


	public boolean isNavigationNutritionFlag() {
		return navigationNutritionFlag;
	}


	public void setNavigationNutritionFlag(boolean navigationNutritionFlag) {
		this.navigationNutritionFlag = navigationNutritionFlag;
	}


	public String getOcsAskCode2() {
		return ocsAskCode2;
	}


	public void setOcsAskCode2(String ocsAskCode2) {
		this.ocsAskCode2 = ocsAskCode2;
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


	public String getOcsAskCode3() {
		return ocsAskCode3;
	}


	public void setOcsAskCode3(String ocsAskCode3) {
		this.ocsAskCode3 = ocsAskCode3;
	}


	public String getOcsNoAnswerCode3() {
		return ocsNoAnswerCode3;
	}


	public void setOcsNoAnswerCode3(String ocsNoAnswerCode3) {
		this.ocsNoAnswerCode3 = ocsNoAnswerCode3;
	}

	public int getChildItemCount(){
		return this.childItems == null ? 0 : this.childItems.size();
	}


	public Boolean getCheckListRequired() {
		return checkListRequired;
	}


	public void setCheckListRequired(Boolean checkListRequired) {
		this.checkListRequired = checkListRequired;
	}


	public QuestionType getNurseQuestionType() {
		return nurseQuestionType;
	}


	public void setNurseQuestionType(QuestionType nurseQuestionType) {
		this.nurseQuestionType = nurseQuestionType;
	}


	public List<QuestionEmbeddedItem> getChildNurseItems() {
		return childNurseItems;
	}


	public void setChildNurseItems(List<QuestionEmbeddedItem> childNurseItems) {
		this.childNurseItems = childNurseItems;
	}


	public String getNurseOcsAskCode() {
		return nurseOcsAskCode;
	}


	public void setNurseOcsAskCode(String nurseOcsAskCode) {
		this.nurseOcsAskCode = nurseOcsAskCode;
	}


	public String getNurseOcsAnswer() {
		return nurseOcsAnswer;
	}


	public void setNurseOcsAnswer(String nurseOcsAnswer) {
		this.nurseOcsAnswer = nurseOcsAnswer;
	}


	public String getNurseOcsNoAnswerCode() {
		return nurseOcsNoAnswerCode;
	}


	public void setNurseOcsNoAnswerCode(String nurseOcsNoAnswerCode) {
		this.nurseOcsNoAnswerCode = nurseOcsNoAnswerCode;
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
