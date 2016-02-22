package com.kbsmc.webcasi.checkup;

import java.util.Date;

/**
 * OCS의 과거 문진이력 데이타를 담는 클래스 
 * @author bizwave
 *
 */
public class Su2Qustt {
	private String patno;
	private Date acptDate;
	private String askCode;
	private String answer;
	private String setPageCd;
	
	
	public String getPatno() {
		return patno;
	}
	public void setPatno(String patno) {
		this.patno = patno;
	}
	public String getAskCode() {
		return askCode;
	}
	public void setAskCode(String askCode) {
		this.askCode = askCode;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
}
