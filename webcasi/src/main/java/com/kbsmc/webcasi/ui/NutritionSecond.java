package com.kbsmc.webcasi.ui;

import java.util.List;
import java.util.Map;

public class NutritionSecond {
	private String topQuestionTitle;	//대표질문 타이틀

	
	private List<String> itemIds;
	
	private Map<String,NutritionSecondItem> itemInfos;	//itemId를 key로 가지고 그 itemId에 해당하는 하위 질문과 Item정보를 담고 있다.

	public String getTopQuestionTitle() {
		return topQuestionTitle;
	}

	public void setTopQuestionTitle(String topQuestionTitle) {
		this.topQuestionTitle = topQuestionTitle;
	}

	
	

	public List<String> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<String> itemIds) {
		this.itemIds = itemIds;
	}

	public Map<String, NutritionSecondItem> getItemInfos() {
		return itemInfos;
	}

	public void setItemInfos(Map<String, NutritionSecondItem> itemInfos) {
		this.itemInfos = itemInfos;
	}
	
	
}
