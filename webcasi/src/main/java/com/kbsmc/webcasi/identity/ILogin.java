package com.kbsmc.webcasi.identity;

import com.kbsmc.webcasi.identity.entity.User;

public interface ILogin {
	User getCurrentUser();
	boolean isInRole(User user,String roleName);
	void updateLastLoginDate(User user);
	void syncRecentReserveInfo(User user)  throws Exception;
	String findPatientNo(User user);
}
