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

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="INSTANCE")
@Entity
@Table(name=InstanceProgress.TABLE_NAME)
public class InstanceProgress {
	public static final String TABLE_NAME= "CHECKUP_INSTANCE_PROG_RESULT";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	@ManyToOne
	private CheckupInstance instance;
	
	private String questionId;
	
	private Date createDate;

	
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

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}
