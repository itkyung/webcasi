package com.kbsmc.webcasi.identity;

import com.google.gson.annotations.Expose;

public class UserCheckObj {
	@Expose
	private boolean needPwInsert = false;	//비번설정 필요여부.
	@Expose
	private boolean errorOccur = false;	//에러 발생.
	@Expose
	private boolean success = false;	//아무런 문제가 없어서 로그인 가능한지 여부.
	@Expose
	private String msg;
	@Expose
	private String patno;
	
	public boolean isNeedPwInsert() {
		return needPwInsert;
	}
	public void setNeedPwInsert(boolean needPwInsert) {
		this.needPwInsert = needPwInsert;
	}
	public boolean isErrorOccur() {
		return errorOccur;
	}
	public void setErrorOccur(boolean errorOccur) {
		this.errorOccur = errorOccur;
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
	public String getPatno() {
		return patno;
	}
	public void setPatno(String patno) {
		this.patno = patno;
	}
	
	
	
}
