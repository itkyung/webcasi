package com.kbsmc.webcasi.identity.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.QuestionEmbeddedItem;

/**
 * User를 표현하는 Entity
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="IDENTITY")
@Entity
@Table(name=User.TABLE_NAME)
public class User {
	public static final String TABLE_NAME = "IDEN_USER";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	/**
	 * 사용자 이름.
	 */
	private String name;
	
	/**
	 * 사용자 아이디.
	 * 환자의 경우에는 암호화된 주민번호로 그 이외의 사용자의 경우에는 loginId로 이용한다.
	 * 이 번호가 Unique해야한다.
	 * 기본적으로 SHA1으로 암호화가 되어있다.
	 */
	@Column(unique=true,nullable=false)
	private String loginId;
	
	/**
	 * 사용자 비밀번호. 
	 */
	private String password;

	private boolean active;
	
	private Date created;
	
	private Date updated;
	
	private Date lastLoginDate;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private String lastReserveNo;	//마지막에 업데이트된 예약번호. 예약번호는 단지 예약날짜를 스트링으로 변환한값임.
	
	private Date lastReserveDate;	//마지막 업데이트된 예약날짜.
	
	@OneToMany(mappedBy="user",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	private Set<UserRoles> roles;

	
	@ElementCollection
	@CollectionTable(name="USER_PATIENT_NO", joinColumns= @JoinColumn(name="USER_ID"))
	private List<EmbeddedPatientNo> patientNos;	//환자번호들.
	
	
	private boolean samsungEmployee;	//삼성 임직원 인지 여부.
	
	private boolean needSleepTest;	//수면정밀문진을 진행할 사람인지 여부.
	
	private boolean needStressTest; //스트레스 및 피로를 진행할 사람인지 여부.
	
	private Boolean needAgree;	//코호트 동의서를 확인받아야하는지 여부.
	
	private String inkbn;	//본원수진자인지, 수원쪽수진자인지 구분하는 필드 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}


	public Set<UserRoles> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}


	public String getLastReserveNo() {
		return lastReserveNo;
	}

	public void setLastReserveNo(String lastReserveNo) {
		this.lastReserveNo = lastReserveNo;
	}

	public List<EmbeddedPatientNo> getPatientNos() {
		return patientNos;
	}

	public void setPatientNos(List<EmbeddedPatientNo> patientNos) {
		this.patientNos = patientNos;
	}

	public Date getLastReserveDate() {
		return lastReserveDate;
	}

	public void setLastReserveDate(Date lastReserveDate) {
		this.lastReserveDate = lastReserveDate;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public boolean isSamsungEmployee() {
		return samsungEmployee;
	}

	public void setSamsungEmployee(boolean samsungEmployee) {
		this.samsungEmployee = samsungEmployee;
	}

	public boolean isNeedSleepTest() {
		return needSleepTest;
	}

	public void setNeedSleepTest(boolean needSleepTest) {
		this.needSleepTest = needSleepTest;
	}

	public boolean isNeedStressTest() {
		return needStressTest;
	}

	public void setNeedStressTest(boolean needStressTest) {
		this.needStressTest = needStressTest;
	}
	
	
	
	public Boolean getNeedAgree() {
		return needAgree;
	}

	public void setNeedAgree(Boolean needAgree) {
		this.needAgree = needAgree;
	}

	
	
	public String getInkbn() {
		return inkbn;
	}

	public void setInkbn(String inkbn) {
		this.inkbn = inkbn;
	}

	/**
	 * 복호화된 주민번호를 리턴한다.
	 * @return
	 */
	public String getResno(){
		if(loginId != null){
			try{
				return CommonUtils.descriptTribleDes(loginId);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
}
