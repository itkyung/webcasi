package com.kbsmc.webcasi.checkup;

import java.util.List;

public class ProgressEntity {
	private String id;
	private String type;	//Category타입정보.
	private String title;
	private List<ProgressEntity> questionGroups;
	private boolean processed;
	
	
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
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<ProgressEntity> getQuestionGroups() {
		return questionGroups;
	}
	public void setQuestionGroups(List<ProgressEntity> questionGroups) {
		this.questionGroups = questionGroups;
	}
	
	
}
