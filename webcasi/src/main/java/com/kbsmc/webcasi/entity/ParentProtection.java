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

import com.kbsmc.webcasi.checkup.AskType;
import com.kbsmc.webcasi.identity.entity.User;
/**
 * 가족사랑 문자 서비스 
 * @author bizwave
 *
 */
@Entity
@Table(name=ParentProtection.TABLE_NAME)
public class ParentProtection {
	public static final String TABLE_NAME = "CHECKUP_PROTECTION";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@ManyToOne
	private CheckupInstance instance;
	
	@ManyToOne
	private User owner;
	
	
	private Boolean smsAgree;
	
	private String parentPhone;

	private Date createDate;
	
	private Date updateDate;
	
	
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Boolean getSmsAgree() {
		return smsAgree;
	}

	public void setSmsAgree(Boolean smsAgree) {
		this.smsAgree = smsAgree;
	}

	public String getParentPhone() {
		return parentPhone;
	}

	public void setParentPhone(String parentPhone) {
		this.parentPhone = parentPhone;
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
	
	
	
}
