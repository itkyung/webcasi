package com.kbsmc.webcasi.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.Expose;

public class QuestionSubmitModel {
	private String instanceId;
	
	@Expose
	private String questionId;
	@Expose
	private String questionItemId;
	
	private String embededItemId;
	private String onOffFlag;	//체크박스 같은 경우에 현재 값이 on인지 off인지 여부.
	
	@Expose
	private String type;
	
	private String strValue;
	private String strValue2;
	private Date dateValue;
	
	@Expose
	private boolean success;
	@Expose
	private boolean allCompleted;
	
	@Expose
	private String msg;
	@Expose
	private long progressRate;
	
	@Expose
	private String nextUrl;
	
	@Expose
	private String itemGroup;
	
	private DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Expose
	private long calory;
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestionItemId() {
		return questionItemId;
	}
	public void setQuestionItemId(String questionItemId) {
		this.questionItemId = questionItemId;
	}
	public String getOnOffFlag() {
		return onOffFlag;
	}
	public void setOnOffFlag(String onOffFlag) {
		this.onOffFlag = onOffFlag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public String getStrValue2() {
		return strValue2;
	}
	public void setStrValue2(String strValue2) {
		this.strValue2 = strValue2;
	}
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public long getProgressRate() {
		return progressRate;
	}
	public void setProgressRate(long progressRate) {
		this.progressRate = progressRate;
	}

	
	public String getEmbededItemId() {
		return embededItemId;
	}
	public void setEmbededItemId(String embededItemId) {
		this.embededItemId = embededItemId;
	}
	public String getMonthValue(){
		if(dateValue != null){
			return monthFormat.format(dateValue);
		}
		return null;
	}
	
	public String getDayValue(){
		if(dateValue != null){
			return dateFormat.format(dateValue);
		}
		return null;		
	}
	public boolean isAllCompleted() {
		return allCompleted;
	}
	public void setAllCompleted(boolean allCompleted) {
		this.allCompleted = allCompleted;
	}
	public String getItemGroup() {
		return itemGroup;
	}
	public void setItemGroup(String itemGroup) {
		this.itemGroup = itemGroup;
	}
	public String getNextUrl() {
		return nextUrl;
	}
	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public long getCalory() {
		return calory;
	}
	public void setCalory(long calory) {
		this.calory = calory;
	}
	
}
