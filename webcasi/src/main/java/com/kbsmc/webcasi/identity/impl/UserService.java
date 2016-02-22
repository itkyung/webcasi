package com.kbsmc.webcasi.identity.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.identity.ILogin;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.Role;
import com.kbsmc.webcasi.identity.UserOptions;
import com.kbsmc.webcasi.identity.entity.EmbeddedPatientNo;
import com.kbsmc.webcasi.identity.entity.LinkReserveData;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.identity.entity.UserRoles;

@Service("userService")
public class UserService implements IUserService,ILogin {
	@Autowired private IUserDAO dao;
	
	private Logger log = Logger.getLogger(UserService.class);  
	
	public void init(){
		//initAdmin();
	}
	

	@Transactional
	public void initAdmin() {
		try{
			User adminUser = dao.findByPlainLoginId(IUserService.ADMIN_ID);
			if(adminUser == null){
				adminUser = new User();
				adminUser.setLoginId(CommonUtils.encryptTripleDes(IUserService.ADMIN_ID));	//아이디는 TripleDes로 암호화를 한다. 
				adminUser.setPassword(new String(CommonUtils.md5(IUserService.ADMIN_PASSWORD)));
				adminUser.setActive(true);
				adminUser.setName("Admin");
				adminUser.setCreated(new Date());
				adminUser.setUpdated(new Date());
				
				dao.addRoleToUser(adminUser,Role.ADMIN_ROLE);
				dao.createUser(adminUser);
			}
		}catch(Exception e){
			log.error("Init admin error : " + e);
		}
	}
	
	@Transactional
	public void initNurse() {
		try{
			User nurseUser = dao.findByPlainLoginId(IUserService.DEFAULT_NURSE_ID);
			if(nurseUser == null){
				nurseUser = new User();
				nurseUser.setLoginId(CommonUtils.encryptTripleDes(IUserService.DEFAULT_NURSE_ID));	//아이디는 TripleDes로 암호화를 한다. 
				nurseUser.setPassword(new String(CommonUtils.md5(IUserService.DEFAULT_NURSE_PASSWORD)));
				nurseUser.setActive(true);
				nurseUser.setName("기본 간호사");
				nurseUser.setCreated(new Date());
				nurseUser.setUpdated(new Date());
				
				dao.addRoleToUser(nurseUser,Role.NURSE_ROLE);
				dao.createUser(nurseUser);
			}
		}catch(Exception e){
			log.error("Init nurse error : " + e);
		}
	}




	/**
	 * 현재 로그인한 사용자를 리턴한다. 
	 */
	@Transactional(readOnly=true)
	public User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal != null){
			org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User)principal;
			User user = dao.findByLoginId(springUser.getUsername());
			//User user = findPatient(springUser.getUsername());
			return user;
		}
		return null;
	}

/**
 * 현재 로그인한 사용자가 해당 Role을 가지고 있는지 확인.
 */
	@Transactional(readOnly=true)
	public boolean isInRole(User user,String roleName) {

		for(UserRoles ur : user.getRoles()){
			if(ur.getRoleName().equals(roleName))
				return true;
		}
		return false;
	}

	@Transactional(readOnly=true)
	public User findByPlainLoginId(String plainLoginId) {
		return dao.findByPlainLoginId(plainLoginId);
	}

	/** OCS User테이블에서 사용자 정보를 찾아서 DB에 insert한다.
	 *  그리고 예약번호 정보를 얻어와서 user db에 업데이트한다.
	 */
	@Transactional
	public boolean initUserFromOCS(String patno,String patName) {
//		if(socialId.length() != 13){
//			return false;
//		}
		try{
			//String resno1 = socialId.substring(0, 6);
			//String resno2 = socialId.substring(6);
			
			LinkReserveData reserveData = dao.findUserFromOCS(patno,true,patName);
			if(reserveData == null){
				return false;
			}
			
			dao.insertUserUsePatient(reserveData);
			
		}catch(Exception e){
			log.error("Insert User error : ",e);
			return false;
		}
		return true;
	}

	@Transactional
	public void updateLastLoginDate(User user) {
		user.setLastLoginDate(new Date());
		if(isInRole(user, Role.USER_ROLE)){
			//OCS에서 건강나이등의 정보를 얻어온다.
			String resno = user.getResno();
			String patno = findPatientNo(user);
			
			// DB링크에서 데이타를 가져오는 부분이라서 주석처리함.  
//			UserOptions options = dao.getUserOptions(resno, patno);
//			user.setSamsungEmployee(options.isSamsungEmployee());
//			if(options.isNeedSleep() || options.isNeedStress()){
//				//둘중에 하나만 true여도 둘다 문진을 진행한다고 생각한다.
//				user.setNeedSleepTest(true);
//				user.setNeedStressTest(true);
//			}else{
//				user.setNeedSleepTest(false);
//				user.setNeedStressTest(false);
//			}
//			user.setNeedAgree(options.isNeedAgree());
			
			user.setSamsungEmployee(true);
			user.setNeedSleepTest(true);
			user.setNeedStressTest(true);
			user.setNeedAgree(true);
			
			
			
		}
		dao.updateUser(user);
	}

	@Transactional
	public void insertPassword(String patno,String password) throws Exception{
		User user = findPatient(patno);
		//User user = dao.findByPlainLoginId(loginId);

		user.setPassword(CommonUtils.md5(password));
		dao.updateUser(user);
	}
	
	@Transactional
	public void insertPasswordById(String id,String password) throws Exception{
		User user = dao.load(id);
		//User user = dao.findByPlainLoginId(loginId);
		if(CommonUtils.isEmpty(password)) {
			user.setPassword("");
		} else {
			user.setPassword(CommonUtils.md5(password));
		}
		
		dao.updateUser(user);
	}	

	/**
	 * 예약 테이블에서 해당 사용자의 최신 예약정보를 update한다.
	 * 사용안함.
	 */
	@Deprecated
	@Transactional
	public void syncRecentReserveInfo(User user) throws Exception{
		String patno = findPatientNo(user);
		LinkReserveData reserveData = dao.findUserFromOCS(patno,true,user.getName()); // CommonUtils.descriptTribleDes(user.getLoginId()));
		if(reserveData != null && reserveData.getReserveNo() != null &&
				!reserveData.getReserveNo().equals(user.getLastReserveNo())
				){
			//최신정보가 존재하면 업데이트한다.
			user.setLastReserveDate(reserveData.getHopeDate());
			user.setLastReserveNo(reserveData.getReserveNo());
			dao.updateUser(user);
		}
		
	}


	@Override
	public List<User> getUsers(String role) {
		return dao.getUsers(role);
	}


	@Override
	public List<User> getNurses() {
		return this.getUsers(Role.NURSE_ROLE);
	}
	
	@Override
	public List<User> getPatients(String loginId, String name, String patientNo) {
		if(loginId!= null && loginId.length() > 0) {
			User user = dao.findByPlainLoginId(loginId);
			List users = new ArrayList<User>();
			users.add(user);
			return users;
		} else if(name != null && name.length() > 0) {
			return dao.findByName(name);
		} else if(patientNo != null && patientNo.length() > 0) {
			User user = dao.findPatient(patientNo);
			List users = new ArrayList<User>();
			users.add(user);
			return users;
		} else {
			return this.getUsers(Role.USER_ROLE);
		}
	}	

	@Transactional(readOnly=true)
	@Override
	public User findPatient(String patientNo) {
		return dao.findPatient(patientNo);
	}


	@Override
	public String findPatientNo(User user) {
		for(EmbeddedPatientNo pno : user.getPatientNos()){
			if(pno.isActive()) return pno.getPatientNo();
		}
		return null;
	}

/**
 * 입력한 주민번호로 우선 surempt에서 최신 예약정보를 얻어온다.
 * 
 */
	@Transactional
	@Override
	public String findAndSyncPatno(String resno) throws Exception {
		
		LinkReserveData reserve = dao.findUserFromOCS(resno, false,null);
		if(reserve != null){
			User user = dao.findByPlainLoginId(resno);
			if(user != null){
				//user의 patno를 업데이트할 필요가 있는지 판단해서 업데이트한다. 
				User fUser = dao.findPatient(reserve.getPatno());
				if(fUser == null){
					dao.addPatientNo(user, reserve.getPatno());
					dao.updateUser(user);
				}
			}
			return reserve.getPatno();
		}
		
		return null;
	}

	
	
}
