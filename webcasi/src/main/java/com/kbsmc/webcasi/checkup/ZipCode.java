package com.kbsmc.webcasi.checkup;

import com.google.gson.annotations.Expose;

public class ZipCode {
	@Expose
	private String zipCode;
	@Expose
	private String address;
	@Expose
	private String bunzi;
	
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBunzi() {
		return bunzi == null || bunzi.equals("") ? "-" : bunzi;
	}
	public void setBunzi(String bunzi) {
		this.bunzi = bunzi;
	}
	
	
	
}
