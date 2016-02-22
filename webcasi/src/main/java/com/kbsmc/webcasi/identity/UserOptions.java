package com.kbsmc.webcasi.identity;

public class UserOptions {
	private boolean samsungEmployee=false;	//true이면 건강나이 대상이다.
	private boolean needStress=false;	//스트래스를 진행하는 경우
	private boolean needSleep=false;	//수면을 진행하는 경우
	private boolean needAgree=true;	//코호트 동의서를 진행해야하는 경우
	
	public boolean isSamsungEmployee() {
		return samsungEmployee;
	}
	public void setSamsungEmployee(boolean samsungEmployee) {
		this.samsungEmployee = samsungEmployee;
	}
	public boolean isNeedStress() {
		return needStress;
	}
	public void setNeedStress(boolean needStress) {
		this.needStress = needStress;
	}
	public boolean isNeedSleep() {
		return needSleep;
	}
	public void setNeedSleep(boolean needSleep) {
		this.needSleep = needSleep;
	}
	public boolean isNeedAgree() {
		return needAgree;
	}
	public void setNeedAgree(boolean needAgree) {
		this.needAgree = needAgree;
	}
	 
	
	
}
