package com.kbsmc.webcasi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Web DB에 존재하는 OCS Result테이블.
 * @author bizwave
 *
 */
@Entity
@Table(name=OCSWebResult.TABLE_NAME)
public class OCSWebResult {
	public static final String TABLE_NAME = "CHECKUP_SU2QUSTT";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")		
	private String id;
	
	@Column(name="patno")
	private String patientNo;	//수진번호.
	
	@Column(name="acptdate",columnDefinition="date")
	private Date acptDate;	//접수일자
	
	@Column(name="qstno")
	private String qstNo;	//문진구분  K로 입력 
	
	@Column(name="askcode")
	private String askCode;	//문진코드 
	
	@Column(name="askseq",columnDefinition="int default 1")
	private int askSeq;	//문진순번 
	
	@Column(name="resvtype")
	private String resvType;	//예약유형
	
	@Column(name="answer")
	private String answer;	//결과
	
	@Column(name="common1")
	private String common1;	//주석1
	
	@Column(name="common2")
	private String common2;	//주석2
	
	@Column(name="common3")
	private String common3;	
	
	@Column(name="common4")
	private String common4;	
	
	@Column(name="common5")
	private String common5;	
	
	@Column(name="inkbn")
	private String inkbn;	//본지사구분
	
	@Column(name="entdate")
	private Date entDate;	//입력일자
	
	@Column(name="entid")
	private String entId;	//입력자 ID
	
	@Column(name="editdate")
	private Date editDate;	//수정일자
	
	@Column(name="editid")
	private String editId;	//수정자
	
	@Column(name="editip")
	private String editIp;	//수정자 IP
	
	@Column(name="pagecd")
	private String pageCd;	//문진버전
	
	@Column(name="modifyyn")
	private String modifyYn;	//수정여부

	@Column(name="itfdt")
	private Date itfDt;	//인터페이스 시간. WEB DB -> OCS DB
	
	@Column(name="itfyn",columnDefinition="varchar(1) default 'N'")
	private String itfYn;	//인터페이스 여부.
	
	@Column(name="useriden1")
	private String resno;	//주민번호 5자리.
	
	@Column(name="useriden2")
	private String resno2;	//주민번호 5자리.
	
	@Column(name="useriden3")
	private String resno3;  //주민번호 3자리.
	
	@ManyToOne
	private CheckupInstance instance;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientNo() {
		return patientNo;
	}

	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
	}

	public Date getAcptDate() {
		return acptDate;
	}

	public void setAcptDate(Date acptDate) {
		this.acptDate = acptDate;
	}

	public String getQstNo() {
		return qstNo;
	}

	public void setQstNo(String qstNo) {
		this.qstNo = qstNo;
	}

	public String getAskCode() {
		return askCode;
	}

	public void setAskCode(String askCode) {
		this.askCode = askCode;
	}

	public int getAskSeq() {
		return askSeq;
	}

	public void setAskSeq(int askSeq) {
		this.askSeq = askSeq;
	}

	public String getResvType() {
		return resvType;
	}

	public void setResvType(String resvType) {
		this.resvType = resvType;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCommon1() {
		return common1;
	}

	public void setCommon1(String common1) {
		this.common1 = common1;
	}

	public String getCommon2() {
		return common2;
	}

	public void setCommon2(String common2) {
		this.common2 = common2;
	}

	public String getCommon3() {
		return common3;
	}

	public void setCommon3(String common3) {
		this.common3 = common3;
	}

	public String getCommon4() {
		return common4;
	}

	public void setCommon4(String common4) {
		this.common4 = common4;
	}

	public String getCommon5() {
		return common5;
	}

	public void setCommon5(String common5) {
		this.common5 = common5;
	}

	public String getInkbn() {
		return inkbn;
	}

	public void setInkbn(String inkbn) {
		this.inkbn = inkbn;
	}

	public Date getEntDate() {
		return entDate;
	}

	public void setEntDate(Date entDate) {
		this.entDate = entDate;
	}

	public String getEntId() {
		return entId;
	}

	public void setEntId(String entId) {
		this.entId = entId;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getEditId() {
		return editId;
	}

	public void setEditId(String editId) {
		this.editId = editId;
	}

	public String getEditIp() {
		return editIp;
	}

	public void setEditIp(String editIp) {
		this.editIp = editIp;
	}

	public String getPageCd() {
		return pageCd;
	}

	public void setPageCd(String pageCd) {
		this.pageCd = pageCd;
	}

	public String getModifyYn() {
		return modifyYn;
	}

	public void setModifyYn(String modifyYn) {
		this.modifyYn = modifyYn;
	}

	public Date getItfDt() {
		return itfDt;
	}

	public void setItfDt(Date itfDt) {
		this.itfDt = itfDt;
	}

	public String getItfYn() {
		return itfYn;
	}

	public void setItfYn(String itfYn) {
		this.itfYn = itfYn;
	}

	public String getResno() {
		return resno;
	}

	public void setResno(String resno) {
		this.resno = resno;
	}

	public String getResno2() {
		return resno2;
	}

	public void setResno2(String resno2) {
		this.resno2 = resno2;
	}

	public String getResno3() {
		return resno3;
	}

	public void setResno3(String resno3) {
		this.resno3 = resno3;
	}

	public CheckupInstance getInstance() {
		return instance;
	}

	public void setInstance(CheckupInstance instance) {
		this.instance = instance;
	}
	
	

	
	
}
