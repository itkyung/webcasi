package com.kbsmc.webcasi.identity.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * OCS의 환자테이블에 대한 임시 테이블.
 * DB Link로 연결되기 전에 임시로 이곳에서 데이타를 가져와서 로그인 처리함.
 * --> 이 테이블 말고 예약정보 테이블에서 데이타를 가져온다.
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name=LinkPatient.TABLE_NAME)
public class LinkPatient {
	public static final String TABLE_NAME = "TMP_PATIENT";
	
	@Id
	@Column(name="partno")
	private String patno;	//환자번호
	
	@Column(name="partname")
	private String patname;
	
	@Column(name="resno1")
	private String resno1;	//주민번호 앞자리 
	
	@Column(name="resno2")
	private String resno2;	//주민번호뒷자리
	
	@Column(name="birthdate")
	private Date birthDate;
	
	@Column(name="sex")
	private String sex;
	
	
	public String getPatno() {
		return patno;
	}

	public void setPatno(String patno) {
		this.patno = patno;
	}

	
	public String getPatname() {
		return patname;
	}

	public void setPatname(String patname) {
		this.patname = patname;
	}

	public String getResno1() {
		return resno1;
	}

	public void setResno1(String resno1) {
		this.resno1 = resno1;
	}

	public String getResno2() {
		return resno2;
	}

	public void setResno2(String resno2) {
		this.resno2 = resno2;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	
	
}
