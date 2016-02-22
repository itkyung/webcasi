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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.QuestionGroupType;

/**
 * 문진 마스터의 질문 그룹 정보 
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=QuestionGroup.TABLE_NAME)
public class QuestionGroup {
	public static final String TABLE_NAME = "CHECKUP_QUES_GRP";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	@Expose
	private String id;
	@Expose
	private String title;
	@Expose
	private String description;	//질문 그룹에 대한 부가 설명 - 이것은 말풍선으로 표현된다. 
	@Expose
	private String groupNo;	//질문 그룹 항목 번호 예)1-1.
	@Expose
	private int sortOrder;	//항목의 순번.
	@Expose
	private Date createDate;
	@Expose
	private Date updateDate;
	@Expose
	private boolean active;
	
	@Expose
	@ManyToOne
	private Category category;	//해당 질문이 속한 카테고리.
	@Expose
	@Enumerated(EnumType.STRING)
	private Gender gender;	//성별정보. ALL이면 모두, 그 이외에는 해당 성별만 의미있음.
	@Expose
	@Enumerated(EnumType.STRING)
	private QuestionGroupType groupType;	//질문그룹 유형의 종류. Normal또는 Custom유형들..
	
	@Expose
	@ManyToOne
	private CheckupMaster master;	//문진 마스터 정보.

	@Expose
	private String thumbnailImage;	//썸네일 이미지 이름.(업로드는 아니고 서버에 존재하는 이미지의 상대경로를 입력한다.)
	
	@Expose
	@ManyToOne
	private HelpContents help;	//Help Contents정보.
	
	
	private boolean stressTestGroup = false;	//직무 스트레스 측정도구 페이지인지 여부.
	
	@Expose
	private Boolean nurseEditable;	//간호사가 별도로 체크할수 있는 질문그룹인지 여부.
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
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

	public QuestionGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(QuestionGroupType groupType) {
		this.groupType = groupType;
	}

	public CheckupMaster getMaster() {
		return master;
	}

	public void setMaster(CheckupMaster master) {
		this.master = master;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}


	public String getThumbnailImage() {
		return thumbnailImage;
	}


	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}


	public HelpContents getHelp() {
		return help;
	}


	public void setHelp(HelpContents help) {
		this.help = help;
	}


	public boolean isStressTestGroup() {
		return stressTestGroup;
	}


	public void setStressTestGroup(boolean stressTestGroup) {
		this.stressTestGroup = stressTestGroup;
	}


	public Boolean getNurseEditable() {
		return nurseEditable;
	}


	public void setNurseEditable(Boolean nurseEditable) {
		this.nurseEditable = nurseEditable;
	}
	
	
	
}
