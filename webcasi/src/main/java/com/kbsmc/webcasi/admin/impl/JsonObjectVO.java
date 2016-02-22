package com.kbsmc.webcasi.admin.impl;

import java.util.List;

import com.google.gson.annotations.Expose;

public class JsonObjectVO {
	@Expose
	private int page ;     // 현재 페이지
	@Expose
	private int records;   // 전체 레코드
	@Expose
	private int total;     // 전체 페이지
	@Expose
	private Object[] rows;
	
	public JsonObjectVO(int page, int records, int total, Object[] rows) {
		this.page = page;
		this.records = records;
		this.total = total;
		this.rows = rows;
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public Object[] getRows() {
		return rows;
	}
	public void setRows(Object[] rows) {
		this.rows = rows;
	}
	
	
}
