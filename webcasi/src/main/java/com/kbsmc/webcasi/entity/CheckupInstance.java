package com.kbsmc.webcasi.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.identity.entity.User;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="INSTANCE")
@Entity
@Table(name=CheckupInstance.TABLE_NAME)
public class CheckupInstance {
	public static final String TABLE_NAME = "CHECKUP_INSTANCE";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	private String reserveNo;	//예약번호
	
	private Date reserveDate;	//예약날짜.
	
	private Date acptDate;	//접수일자.
	
	@ManyToOne
	private User owner;
	
	private Date createDate;
	
	private Date updatedDate;
	
	@ManyToOne
	private CheckupMaster master;	//문진 마스터 정보.
	
	private long progress;	//진행률
	
	private int resultCount;	//1Depth질문에 대한 답변갯수.
	
	private String lastQuestionId;	//마지막순서로 업데이트된 질문.
	
	private int totalQuestionCount;	//1Depth질문의 갯수. 선택한 질문에 따라서 변한다.
	
	@Enumerated(EnumType.STRING)
	private InstanceStatus status;	
	
	
//	@ElementCollection
//	@CollectionTable(name="CHECKUP_INSTANCE_RESULT")
//	private List<String> userResults;
//	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy="instance",cascade=CascadeType.ALL)
	@OrderBy("createDate")
	private List<InstanceProgress> userResultProgress;
	
	private boolean skipNutrition = false;
	
	private boolean skipStress = false;	//스트레스 측정도구 관련...
	
	private Boolean agreeFlag;	//동의서 첫번째 동의햇는지 여부.
	
	private Boolean agreeFlag2;	//동의서 두번째 항목 
	
	private Boolean nutritionFlagUpdated;	//영양설문 응답여부를 체크햇는지 여부.
	
	private String patno;	//현재 진행하는 수진자의 수진번호.
	
	
	private boolean samsungEmployee;	//건강나이 진행여부 
	
	private boolean needSleepTest; //수면정밀 카테고리 진행여부 
	
	private boolean needStressTest; //스트레스 카테고리 진행여부.
	
	private Boolean protectionFlag;	//보호자 안심서비스 신청여부.
	
	
	@Transient
	private static final DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReserveNo() {
		return reserveNo;
	}

	public void setReserveNo(String reserveNo) {
		this.reserveNo = reserveNo;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public CheckupMaster getMaster() {
		return master;
	}

	public void setMaster(CheckupMaster master) {
		this.master = master;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}



	public InstanceStatus getStatus() {
		return status;
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
	}

	public Date getReserveDate() {
		return reserveDate;
	}

	public void setReserveDate(Date reserveDate) {
		this.reserveDate = reserveDate;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public String getLastQuestionId() {
		return lastQuestionId;
	}

	public void setLastQuestionId(String lastQuestionId) {
		this.lastQuestionId = lastQuestionId;
	}

	
	public List<InstanceProgress> getUserResultProgress() {
		return userResultProgress;
	}

	public void setUserResultProgress(List<InstanceProgress> userResultProgress) {
		this.userResultProgress = userResultProgress;
	}

	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}

	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
	}

	public boolean isSkipNutrition() {
		return skipNutrition;
	}

	public void setSkipNutrition(boolean skipNutrition) {
		this.skipNutrition = skipNutrition;
	}

	public boolean isSkipStress() {
		return skipStress;
	}

	public void setSkipStress(boolean skipStress) {
		this.skipStress = skipStress;
	}

	public Boolean getAgreeFlag() {
		return agreeFlag;
	}

	public void setAgreeFlag(Boolean agreeFlag) {
		this.agreeFlag = agreeFlag;
	}

	public Boolean getAgreeFlag2() {
		return agreeFlag2;
	}

	public void setAgreeFlag2(Boolean agreeFlag2) {
		this.agreeFlag2 = agreeFlag2;
	}

	public Boolean getNutritionFlagUpdated() {
		return nutritionFlagUpdated;
	}

	public void setNutritionFlagUpdated(Boolean nutritionFlagUpdated) {
		this.nutritionFlagUpdated = nutritionFlagUpdated;
	}

	public Date getAcptDate() {
		return acptDate;
	}

	public void setAcptDate(Date acptDate) {
		this.acptDate = acptDate;
	}

	public String getPatno() {
		return patno;
	}

	public void setPatno(String patno) {
		this.patno = patno;
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

	public String getAcptDateStr(){
		if(acptDate != null){
			return fm.format(acptDate);
		}else{
			//접수일자가 없으면 예약일을 리턴함.
			return fm.format(reserveDate);
		}
	}

	public Boolean getProtectionFlag() {
		return protectionFlag;
	}

	public void setProtectionFlag(Boolean protectionFlag) {
		this.protectionFlag = protectionFlag;
	}
	
	
}
