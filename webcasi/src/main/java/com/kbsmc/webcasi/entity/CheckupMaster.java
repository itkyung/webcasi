package com.kbsmc.webcasi.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.kbsmc.webcasi.CheckupMasterStatus;


/**
 * 문진 마스터 Entity 
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=CheckupMaster.TABLE_NAME)
public class CheckupMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "CHECKUP_MASTER";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@Column(unique=true,nullable=true)
	private String version;	//문진 마스터의 버전정보 
	
	private String title;	//문진 마스터의 제목 
	
	@OneToMany(mappedBy="master")
	@OrderBy("sortOrder")
	private List<Category> categorys;
	
	private Date createDate;
	
	private Date updateDate;

	private boolean active;
	
	@Enumerated(EnumType.STRING)
	private CheckupMasterStatus status;
	
	private int maleQuestionCount;	//남성용 설문의 1Depth 질문갯수
	
	private int femaleQuestionCount; //여성용 설문의 1Depth 질문갯수

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Category> getCategorys() {
		return categorys;
	}
	
	public void setCategorys(List<Category> categorys) {
		this.categorys = categorys;
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

	public CheckupMasterStatus getStatus() {
		return status;
	}

	public void setStatus(CheckupMasterStatus status) {
		this.status = status;
	}

	public int getMaleQuestionCount() {
		return maleQuestionCount;
	}

	public void setMaleQuestionCount(int maleQuestionCount) {
		this.maleQuestionCount = maleQuestionCount;
	}

	public int getFemaleQuestionCount() {
		return femaleQuestionCount;
	}

	public void setFemaleQuestionCount(int femaleQuestionCount) {
		this.femaleQuestionCount = femaleQuestionCount;
	}
}
