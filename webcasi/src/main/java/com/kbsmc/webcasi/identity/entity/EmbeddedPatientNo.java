package com.kbsmc.webcasi.identity.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddedPatientNo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5203311273130253378L;
	
	private String patientNo;
	
	private boolean active;

	public String getPatientNo() {
		return patientNo;
	}

	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
