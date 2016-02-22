package com.kbsmc.webcasi.checkup;

import com.kbsmc.webcasi.entity.CheckupInstance;

public class InstanceWrapper {
	private CheckupInstance instance;
	private boolean needHistoryInit;
	public CheckupInstance getInstance() {
		return instance;
	}
	public void setInstance(CheckupInstance instance) {
		this.instance = instance;
	}
	public boolean isNeedHistoryInit() {
		return needHistoryInit;
	}
	public void setNeedHistoryInit(boolean needHistoryInit) {
		this.needHistoryInit = needHistoryInit;
	}
	
	
}
