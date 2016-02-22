package com.kbsmc.webcasi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="MASTER")
@Entity
@Table(name=HelpContents.TABLE_NAME)
public class HelpContents {
	public static final String TABLE_NAME = "CHECKUP_HELP";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Expose
	private String id;
	@Expose
	private String contents;	//4000자 이하짜리 도움말. (html?)
	@Expose
	private String attachFilePath;	//첨부되는 도움말 파일의 경로.
	@Expose
	private String fileMimeType;
	
	private Date createDate;
	
	private Date updatedDate;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getAttachFilePath() {
		return attachFilePath;
	}

	public void setAttachFilePath(String attachFilePath) {
		this.attachFilePath = attachFilePath;
	}

	public String getFileMimeType() {
		return fileMimeType;
	}

	public void setFileMimeType(String fileMimeType) {
		this.fileMimeType = fileMimeType;
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
	
	
	
}
