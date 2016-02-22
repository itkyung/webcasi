package com.kbsmc.webcasi.ui;

import com.google.gson.annotations.Expose;

public class ProtectionModel {
	@Expose
	private String id;
	
	private String instanceId;
	
	private String cellPhone;
	
	private String companyPhone;
	
	private String zipCode;
	
	private String zpAddress;
	
	private String address;
	
	private String askType;
	
	private String prefix;
	
	private String postfix;
	
	private String emailPostfixInput;
	
	private Boolean emailAgree;
	
	private Boolean smsAgree;
	
	@Expose
	private String parentPhone;
	
	@Expose
	private boolean confirmFlag;	//하단에 확인버튼을 누른것인지 여부.
	
	@Expose
	private boolean success;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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

	public String getAskType() {
		return askType;
	}

	public void setAskType(String askType) {
		this.askType = askType;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public String getEmailPostfixInput() {
		return emailPostfixInput;
	}

	public void setEmailPostfixInput(String emailPostfixInput) {
		this.emailPostfixInput = emailPostfixInput;
	}

	public Boolean getEmailAgree() {
		return emailAgree;
	}

	public void setEmailAgree(Boolean emailAgree) {
		this.emailAgree = emailAgree;
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

	public boolean isConfirmFlag() {
		return confirmFlag;
	}

	public void setConfirmFlag(boolean confirmFlag) {
		this.confirmFlag = confirmFlag;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
