package com.kbsmc.webcasi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.kbsmc.webcasi.identity.entity.User;

/**
 * OCS WebDB에 사용자가 각 그룹별로 진행을 했는지 여부 저장.
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="INSTANCE")
@Entity
@Table(name=OCSWebResultProgress.TABLE_NAME)
public class OCSWebResultProgress {
	public static final String TABLE_NAME = "CHECKUP_OCS_PROGRESS";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	@ManyToOne
	private User owner;
	
	@ManyToOne
	private CheckupInstance instance;
	
	@ManyToOne
	private QuestionGroup questionGroup;
	
	private Date createDate;
	
	private Date lastUpdateDate;	//해당 사용자가 이 질문그룹의 값을 마지막으로 수정한날짜.

	private Date lastSyncDate;	//마지막으로 ocs와 동기화된 날짜.
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public CheckupInstance getInstance() {
		return instance;
	}

	public void setInstance(CheckupInstance instance) {
		this.instance = instance;
	}

	public QuestionGroup getQuestionGroup() {
		return questionGroup;
	}

	public void setQuestionGroup(QuestionGroup questionGroup) {
		this.questionGroup = questionGroup;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
	
	
}
