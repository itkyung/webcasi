package com.kbsmc.webcasi.identity.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * DBLink를 통해서 View로 만들어지는 예약정보 테이블. (실제로 OCS상에 존재함.)
 * @author bizwave
 *
 */
@Entity
@Table(name=LinkReserveData.TABLE_NAME)
public class LinkReserveData {
	public static final String TABLE_NAME = "TMP_RESERVE";
	
	@Id
	@Column(name="resno")
	private String resno;
	
	@Column(name="patno")
	private String patno;
	
	@Column(name="patname")
	private String patName;
	
	@Column(name="sex")
	private String sex;
	
	@Column(name="birthdate")
	private Date birthDate;
	
	//예약날짜.
	@Column(name="hopedate")
	private Date hopeDate;
	
	//접수날짜.
	@Column(name="acptdate")
	private Date acptDate;
	
	@Column(name="webstat")
	private String webstat;

	
	private String inkbn;
//	private Boolean samsungFlag;
//	
//	private Boolean stressFlag;
//	
//	private Boolean sleepFlag;
	
	
	public String getResno() {
		return resno;
	}

	public void setResno(String resno) {
		this.resno = resno;
	}

	public String getPatno() {
		return patno;
	}

	public void setPatno(String patno) {
		this.patno = patno;
	}

	

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getHopeDate() {
		return hopeDate;
	}

	public void setHopeDate(Date hopeDate) {
		this.hopeDate = hopeDate;
	}

	public Date getAcptDate() {
		return acptDate;
	}

	public void setAcptDate(Date acptDate) {
		this.acptDate = acptDate;
	}
	
	@Transient
	public String getReserveNo(){
		return hopeDate != null ? hopeDate.getTime() + patno : null;
	}

	public String getWebstat() {
		return webstat;
	}

	public void setWebstat(String webstat) {
		this.webstat = webstat;
	}

	public String getInkbn() {
		return inkbn;
	}

	public void setInkbn(String inkbn) {
		this.inkbn = inkbn;
	}
	
	
}
