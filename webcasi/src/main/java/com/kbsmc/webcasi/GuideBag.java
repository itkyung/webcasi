package com.kbsmc.webcasi;

public class GuideBag {
	private String title;
	private String url;
	private int path;	//첫번째값.
	private int step;	//두번째값.
	private int totalStepCount;	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPath() {
		return path;
	}
	public void setPath(int path) {
		this.path = path;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public int getTotalStepCount() {
		return totalStepCount;
	}
	public void setTotalStepCount(int totalStepCount) {
		this.totalStepCount = totalStepCount;
	}
	
	
	
}
