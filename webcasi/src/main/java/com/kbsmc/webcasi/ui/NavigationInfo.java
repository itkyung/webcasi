package com.kbsmc.webcasi.ui;

public class NavigationInfo {
	private String preUrl;
	private String nextUrl;
	
	private boolean lastQuestionGroupInCategory=false;	//현재 페이지가 해당 카테고리의 마지막 QuestionGroup인지 여부.
	private boolean lastQuestionGroup=false;	//모든 문진의 마지막 질문그룹인지 여부.
	public String getPreUrl() {
		return preUrl;
	}
	public void setPreUrl(String preUrl) {
		this.preUrl = preUrl;
	}
	public String getNextUrl() {
		return nextUrl;
	}
	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}
	public boolean isLastQuestionGroupInCategory() {
		return lastQuestionGroupInCategory;
	}
	public void setLastQuestionGroupInCategory(boolean lastQuestionGroupInCategory) {
		this.lastQuestionGroupInCategory = lastQuestionGroupInCategory;
	}
	public boolean isLastQuestionGroup() {
		return lastQuestionGroup;
	}
	public void setLastQuestionGroup(boolean lastQuestionGroup) {
		this.lastQuestionGroup = lastQuestionGroup;
	}
	
	
	
}
