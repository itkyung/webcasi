package com.kbsmc.webcasi.identity;

import java.util.Date;
import java.util.List;

import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.entity.LinkReserveData;
import com.kbsmc.webcasi.identity.entity.User;

public interface IUserDAO {
	User load(String id);
	User findByPlainLoginId(String plainLoginId);		//암호화가 되기이전의 로그인 아이디로 찾는다. 
	User findByLoginId(String encLoginId);	//암호화된 아이디로 찾는다.
	List<User> findByName(String name);
	void createUser(User user);
	LinkReserveData findUserFromOCS(String key,boolean usePatno,String patName);
	void insertUserUsePatient(LinkReserveData reserveData) throws Exception;
	LinkReserveData findReserveData(String partno,Date acptDate,boolean hopeDateCheck);
	
	void addRoleToUser(User user, String roleName);
	void updateUser(User user);
	User loadUser(String userId);
	
	List<User> getUsers(String role);
	List<User> getNurses();
	User findPatient(String patientNo);
	void updateOcsProgress(String patno,Date reserveDate,int progress);
	void addPatientNo(User user,String patientNo);
	UserOptions getUserOptions(String resno,String patno);
	String getCompanyName(String patientNo,Date hopeDate);
	void updateOcsUser(String patientNo,Date reserveDate,ParentProtection protection);
	void updateOcsUser(String patientNo,Date reserveDate,ResultRequest request);
}
