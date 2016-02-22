package com.kbsmc.webcasi.ui;


import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.ValidatorType;
import com.kbsmc.webcasi.entity.Question;

import com.kbsmc.webcasi.entity.QuestionItem;

public class FamilySubItem {
	private boolean checked;
	private String strValue;
	private QuestionItem item;
	private boolean active;

	public boolean isChecked() {
		return checked;
	}


	public void setChecked(boolean checked) {
		this.checked = checked;
	}


	public QuestionItem getItem() {
		return item;
	}


	public void setItem(QuestionItem item) {
		this.item = item;
	}


	public String getStrValue() {
		return strValue;
	}


	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
	public String getId() {
		return item.getId();
	}

	public QuestionType getType() {
		return item.getType();
	}

	
	public String getTitle() {
		return item.getTitle();
	}


	public String getPreText() {
		return item.getPreText();
	}

	public String getPostText() {
		return item.getPostText();
	}

	
	public void setActive(boolean active) {
		this.active = active;
	}


	public boolean isActive() {
		return active;
	}
	
	public Question getParentQuestion() {
		return item.getParentQuestion();
	}

	public ValidatorType getValidator() {
		return item.getValidator();
	}
	
}
