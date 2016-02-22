package com.kbsmc.webcasi.identity;

import java.util.List;

import com.kbsmc.webcasi.identity.entity.User;

public interface IUserService {
	final static String ADMIN_ID = "admin";
	final static String ADMIN_PASSWORD = "admin1234";
	
	final static String DEFAULT_NURSE_ID = "nurse1";
	final static String DEFAULT_NURSE_PASSWORD = "nurse1234";
	
	//final static String OCS_USER_TABLE = "surtempt";	//DB Link를 통한 View로 생성된 OCS의 예약테이블이름.
	final static String OCS_USER_TABLE = "TMP_RESERVE";
	
	
	void init();
	void initAdmin();
	void initNurse();
	
	User findByPlainLoginId(String plainLoginId);	
	boolean initUserFromOCS(String patno,String name);
	void insertPassword(String patno,String password) throws Exception;
	void insertPasswordById(String id, String password) throws Exception;
	
	List<User> getUsers(String role);
	List<User> getNurses();
	List<User> getPatients(String loginId, String name, String patientNo);
	User findPatient(String patientNo);
	String findPatientNo(User user);
	
	String findAndSyncPatno(String resno) throws Exception;
}
