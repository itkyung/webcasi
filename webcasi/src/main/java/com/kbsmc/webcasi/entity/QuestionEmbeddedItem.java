package com.kbsmc.webcasi.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.google.gson.annotations.Expose;

/**
 * 질문 항목에 추가로 붙는 Child항목들.
 * 예) 복통) 없음, 가벼움, 중간, 심함.에서 복통이라는 질문 항목의 Sub로 붙는 항목들을 가진다.
 * Embbedded Collection으로 존재함.
 * @author bizwave
 *
 */

@Embeddable
public class QuestionEmbeddedItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 32002937119674112L;
	@Expose
	private String key;
	@Expose
	private String value;
	@Expose
	private String ocsAskCode;
	@Expose
	private String ocsAnswer;
	@Expose
	private String ocsNoAnswerCode;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOcsAskCode() {
		return ocsAskCode;
	}

	public void setOcsAskCode(String ocsAskCode) {
		this.ocsAskCode = ocsAskCode;
	}

	public String getOcsAnswer() {
		return ocsAnswer;
	}

	public void setOcsAnswer(String ocsAnswer) {
		this.ocsAnswer = ocsAnswer;
	}

	public String getOcsNoAnswerCode() {
		return ocsNoAnswerCode;
	}

	public void setOcsNoAnswerCode(String ocsNoAnswerCode) {
		this.ocsNoAnswerCode = ocsNoAnswerCode;
	}
	
	
	
}
