package com.kbsmc.webcasi.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.kbsmc.webcasi.identity.entity.User;

/**
 *  환자 정보 클래스
 * @author bizwave
 *
 */
public class PatientInfo {
	private User patient;
	private int age;
	private String socialNumber;
	private Date reserveDate;
	private Date acptDate;
	private String patNo;
	
	private DateFormat fm = new SimpleDateFormat("yyyy/MM/dd");
	
	public User getPatient() {
		return patient;
	}
	public void setPatient(User patient) {
		this.patient = patient;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSocialNumber() {
		return socialNumber;
	}
	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}
	public String getReserveDate() {
		return fm.format(reserveDate);
	}
	public void setReserveDate(Date reserveDate) {
		this.reserveDate = reserveDate;
	}
	public String getAcptDate() {
		return acptDate == null ? "접수안함" : fm.format(acptDate);
	}
	public void setAcptDate(Date acptDate) {
		this.acptDate = acptDate;
	}
	public String getPatNo() {
		return patNo;
	}
	public void setPatNo(String patNo) {
		this.patNo = patNo;
	}
	
	public String getName(){
		return patient.getName();
	}
	
	public String getGender(){
		return patient.getGender().name();
	}
	
}
