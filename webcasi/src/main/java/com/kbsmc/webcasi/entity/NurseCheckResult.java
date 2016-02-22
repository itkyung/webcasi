package com.kbsmc.webcasi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.identity.entity.User;

/**
 * 간호사가 체크한것에 대한 결과.
 * @author bizwave
 *
 */
@Entity
@Table(name=NurseCheckResult.TABLE_NAME)
public class NurseCheckResult {
	public static final String TABLE_NAME = "CHECKUP_NURSE_RESULT";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	@ManyToOne
	private CheckupInstance instance;
	
	@ManyToOne
	private Question question;
	
	@ManyToOne
	private User patient;	//환자.
	
	@Enumerated(EnumType.STRING)
	private QuestionType type;
	
	private boolean checked;	//체크박스일 경우에 체크여부.
	
	private String objectiveValue;	//객관식일 경우에 key값.
	
	private String ocsAskCode;	//OCS AskCode
	
	private String ocsValue;
	
	private Date createDate;
	
	private Date updateDate;

	private boolean active;	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CheckupInstance getInstance() {
		return instance;
	}

	public void setInstance(CheckupInstance instance) {
		this.instance = instance;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public User getPatient() {
		return patient;
	}

	public void setPatient(User patient) {
		this.patient = patient;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(String objectiveValue) {
		this.objectiveValue = objectiveValue;
	}

	public String getOcsAskCode() {
		return ocsAskCode;
	}

	public void setOcsAskCode(String ocsAskCode) {
		this.ocsAskCode = ocsAskCode;
	}

	public String getOcsValue() {
		return ocsValue;
	}

	public void setOcsValue(String ocsValue) {
		this.ocsValue = ocsValue;
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
	
	
}
