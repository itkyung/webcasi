package com.kbsmc.webcasi.identity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * 사용자의 Role을 표현하는 entity 
 * @author bizwave
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,region="IDENTITY")
@Entity
@Table(name=UserRoles.TABLE_NAME)
public class UserRoles {
	public static final String TABLE_NAME="IDEN_USER_ROLES";
	
	@Id @Column(length=36) @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	
	private String id;
	
	@ManyToOne
	@JoinColumn(name="user_fk")
	private User user;
	
	private String roleName;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
	
}
