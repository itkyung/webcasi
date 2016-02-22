package com.kbsmc.webcasi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.kbsmc.webcasi.identity.entity.User;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=OCSHistoryCode.TABLE_NAME)
public class OCSHistoryCode {
	public static final String TABLE_NAME = "CHECKUP_OCS_HISTORY";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@ManyToOne
	private User owner;
	
	private String ocsAskCode;

	private String ocsAnswer;
	
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOcsAskCode() {
		return ocsAskCode;
	}

	public void setOcsAskCode(String ocsAskCode) {
		this.ocsAskCode = ocsAskCode;
	}

	

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getOcsAnswer() {
		return ocsAnswer;
	}

	public void setOcsAnswer(String ocsAnswer) {
		this.ocsAnswer = ocsAnswer;
	}
	
	
}
