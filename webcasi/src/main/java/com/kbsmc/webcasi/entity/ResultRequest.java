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
 * 건진 결과지 신청
 * @author bizwave
 *
 */
@Entity
@Table(name=ResultRequest.TABLE_NAME)
public class ResultRequest {
	public static final String TABLE_NAME = "CHECKUP_RESULT_REQUEST";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@ManyToOne
	private CheckupInstance instance;
	
	@ManyToOne
	private User owner;
	
	private String companyName;
	
	private String cellPhone;
	
	private String companyPhone;
	
	private String zipCode;
	
	private String zpAddress;
	
	private String address;
	
	@Enumerated(EnumType.STRING)
	private AskType askType;
	
	private String email;
	
	private Boolean emailAgree;
	
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZpAddress() {
		return zpAddress;
	}

	public void setZpAddress(String zpAddress) {
		this.zpAddress = zpAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public AskType getAskType() {
		return askType;
	}

	public void setAskType(AskType askType) {
		this.askType = askType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEmailAgree() {
		return emailAgree;
	}

	public void setEmailAgree(Boolean emailAgree) {
		this.emailAgree = emailAgree;
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
