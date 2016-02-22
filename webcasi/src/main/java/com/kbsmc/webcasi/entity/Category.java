package com.kbsmc.webcasi.entity;

import java.io.Serializable;
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

import com.kbsmc.webcasi.CategoryType;

/**
 * 질문그룹의 카테고리
 * 검진준비 체크리스트.
 * 건강문진표.
 * 정신건강
 * 영양설문..등...
 * @author bizwave
 *
 */

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=Category.TABLE_NAME)
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "CHECKUP_CATEGORY";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")		
	private String id;
	
	private String title;
	
	@Enumerated(EnumType.STRING)
	private CategoryType type;	//카테고리의 타입.  생활습관,정신건강등...
	
	private int sortOrder;
	
	private Date createDate;
	
	private Date updateDate;
	
	private boolean active;

	@ManyToOne
	private CheckupMaster master;	//문진 마스터 정보.
	
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

	public CategoryType getType() {
		return type;
	}

	public void setType(CategoryType type) {
		this.type = type;
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

	public CheckupMaster getMaster() {
		return master;
	}

	public void setMaster(CheckupMaster master) {
		this.master = master;
	}
	
	
	
}
