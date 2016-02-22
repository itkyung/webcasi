package com.kbsmc.webcasi.identity.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.identity.entity.UserRoles;

public class UserDetailsServiceImpl implements UserDetailsService,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8139958564799612623L;
	
	@Autowired private IUserDAO dao;
	@Autowired private IUserService userService;
	
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String loginId)
			throws UsernameNotFoundException {
		//여기서 loginId는 실제로는 patno를 의미한다.
		//어드민의 경우에는 patno가 없기때문에 loginId로 검색한다.
		User user = userService.findPatient(loginId);
		if(user == null){
			user = dao.findByPlainLoginId(loginId);
		}
		
		if(user == null){
			throw new UsernameNotFoundException("User[" + loginId + "] does not exist");
		}
		
		return buildSpringUser(user);
	}

	public org.springframework.security.core.userdetails.User buildSpringUser(User user){
		
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for(final UserRoles role : user.getRoles()){
			authorities.add(new GrantedAuthority() {
				private static final long serialVersionUID = 1751733950989952303L;

				public String getAuthority() {
					return role.getRoleName();
				}
			});
		}
		
	//	String patno = userService.findPatientNo(user);
		//spring user를 만들때에 userName은 그대로 loginId를 이용한다.
		org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(user.getLoginId(),
				user.getPassword(),user.isActive(),user.isActive(),user.isActive(),user.isActive(),authorities);
		return springUser;
	}
	
}
