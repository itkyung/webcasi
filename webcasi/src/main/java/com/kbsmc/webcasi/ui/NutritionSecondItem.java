package com.kbsmc.webcasi.ui;

import java.util.ArrayList;
import java.util.List;

import com.kbsmc.webcasi.entity.QuestionItem;

public class NutritionSecondItem {
	private String itemId;
	private String itemTitle;
	private String questionId;
	private String questionDesc;
	
	private List<QuestionItem> frequencyItems = new ArrayList<QuestionItem>();	//섭취빈도 아이템들.
	private List<QuestionItem> quantityItems = new ArrayList<QuestionItem>();	//섭취분량 아이템들.
	private List<QuestionItem> monthItems = new ArrayList<QuestionItem>();	//섭취개월 아이템들.
	private List<QuestionItem> whetherItems = new ArrayList<QuestionItem>();	//섭취여부 아이템들.
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemTitle() {
		return itemTitle;
	}
	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}
	public List<QuestionItem> getFrequencyItems() {
		return frequencyItems;
	}
	public void setFrequencyItems(List<QuestionItem> frequencyItems) {
		this.frequencyItems = frequencyItems;
	}
	public List<QuestionItem> getQuantityItems() {
		return quantityItems;
	}
	public void setQuantityItems(List<QuestionItem> quantityItems) {
		this.quantityItems = quantityItems;
	}
	public List<QuestionItem> getMonthItems() {
		return monthItems;
	}
	public void setMonthItems(List<QuestionItem> monthItems) {
		this.monthItems = monthItems;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	
	public int getFrequencyItemCount(){
		return frequencyItems == null ? 0 : frequencyItems.size();
	}
	
	public int getQuantityItemCount(){
		return quantityItems == null ? 0 : quantityItems.size();
	}
	public List<QuestionItem> getWhetherItems() {
		return whetherItems;
	}
	public void setWhetherItems(List<QuestionItem> whetherItems) {
		this.whetherItems = whetherItems;
	}
	
	
	public int getWhetherItemCount(){
		return whetherItems == null ? 0 : whetherItems.size();
	}
	public String getQuestionDesc() {
		return questionDesc;
	}
	public void setQuestionDesc(String questionDesc) {
		this.questionDesc = questionDesc;
	}
	
	
}
