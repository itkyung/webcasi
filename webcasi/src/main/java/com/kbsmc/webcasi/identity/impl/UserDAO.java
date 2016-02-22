package com.kbsmc.webcasi.identity.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.OCSWebResult;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.Role;
import com.kbsmc.webcasi.identity.UserOptions;
import com.kbsmc.webcasi.identity.entity.EmbeddedPatientNo;
import com.kbsmc.webcasi.identity.entity.LinkPatient;
import com.kbsmc.webcasi.identity.entity.LinkReserveData;
import com.kbsmc.webcasi.identity.entity.User;
import com.kbsmc.webcasi.identity.entity.UserRoles;

@Repository("userDAO")
public class UserDAO implements IUserDAO {
	private Log log = LogFactory.getLog(UserDAO.class);
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	private DateFormat fm = new SimpleDateFormat("yyyy/MM/dd");
	
	public User load(String id) {
		try{
			Query query = em.createQuery("SELECT a from User a WHERE a.id = :id");
			query.setParameter("id", id);
			return (User)query.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
	public User findByPlainLoginId(String plainLoginId) {
		try{
			String encLoginId = CommonUtils.encryptTripleDes(plainLoginId);
			return findByLoginId(encLoginId);
		}catch(Exception e){
			return null;
		}
	}
	
	public User findByLoginId(String encLoginId) {
		try{
			Query query = em.createQuery("SELECT a from User a WHERE a.loginId = :loginId");
			query.setParameter("loginId", encLoginId);
			query.setHint("org.hibernate.cacheable", true);
			return (User)query.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
	public List<User> findByName(String name) {
		try{
			Query query = em.createQuery("SELECT a from User a WHERE a.name = :name");
			query.setParameter("name", name);
			query.setHint("org.hibernate.cacheable", true);
			return query.getResultList();
		}catch(Exception e){
			return null;
		}
	}



	public void createUser(User user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());
		em.persist(user);
	}

	
	
	public void updateUser(User user) {
		user.setUpdated(new Date());
		em.merge(user);
	}

	/**
	 * acptdate is null이라는 조건을 뺀다. 즉 유저가 처음 로그인할때에는 오늘을 포함해서 이후로 예약내역이 존재하면 로그인은 가능하다.
	 * patno는 수진자 번호이다.
	 */
	public LinkReserveData findUserFromOCS(String key,boolean usePatno,String patName) {
		String name = null;
		if(patName != null) 
			name = patName.trim();
		
		try{
			String sql = "SELECT a.patno,a.patname,a.sex,min(a.hopedate),a.resno,a.inkbn FROM " + IUserService.OCS_USER_TABLE + 
					" a WHERE a.hopedate is not null AND TO_CHAR(a.hopedate,'YYYY/MM/DD') >= :today ";
			if(usePatno){
				sql += "AND a.patno = :patno ";
			}else{
				sql += "AND a.resno = :resno ";
			}
			if(name != null){
				sql += "AND a.patname = :patname ";
			}
			
			sql += "GROUP BY a.patno,a.patname,a.sex,a.hopedate,a.resno,a.inkbn";
		
			Query query = em.createNativeQuery(sql);
			
			if(usePatno){
				query.setParameter("patno", key);
			}else{
				query.setParameter("resno", key);
			}
			if(name != null){
				query.setParameter("patname", patName);
			}
			
			query.setParameter("today", fm.format(new Date()));
			
			List<Object> results = query.getResultList();
			if(results.size() == 0){
				return null;
			}
			LinkReserveData reserve = new LinkReserveData();
			for(Object row : results){
				Object[] cols = (Object[]) row;
				reserve.setPatno((String)cols[0]);
				reserve.setPatName((String)cols[1]);
				reserve.setSex((String)cols[2]);
				
				reserve.setHopeDate((Date)cols[3]);
				//reserve.setAcptDate((Date)cols[5]);
				reserve.setResno((String)cols[4]);
				reserve.setInkbn((String)cols[5]);
			}
			return reserve;
		}catch(Exception e){
			log.error("Fetch ocs user error ",e);
			return null;
		}
		
	}

/**
 * hopeDateCheck가 true이면 오늘또는 오늘 이후에 hopteDate인것만 가져온다.
 */
	@Override
	public LinkReserveData findReserveData(String partno, Date acptDate,boolean hopeDateCheck) {
		try{
			String sql = "SELECT a.patno,a.patname,a.sex,a.hopedate,a.resno FROM " + IUserService.OCS_USER_TABLE + " a WHERE a.patno = :patno ";
			if(acptDate != null){
				sql += "AND TO_CHAR(a.acptdate,'YYYY/MM/DD') = :acptdate ";
			}else{
				sql += "AND a.acptdate is null ";
			}
			
			if(hopeDateCheck){
				sql += "AND TO_CHAR(a.hopedate,'YYYY/MM/DD') >= :hopedate ";
			}
			
			sql += " Order by a.hopedate asc";
					
			Query query = em.createNativeQuery(sql);
			query.setParameter("patno", partno);
			if(acptDate != null)
				query.setParameter("acptdate", fm.format(acptDate));
			if(hopeDateCheck)
				query.setParameter("hopedate", fm.format(new Date()));
				
				
			List<Object> results = query.getResultList();
			if(results.size() == 0){
				return null;
			}
			LinkReserveData reserve = new LinkReserveData();
			for(Object row : results){
				Object[] cols = (Object[]) row;
				reserve.setPatno((String)cols[0]);
				reserve.setPatName((String)cols[1]);
				reserve.setSex((String)cols[2]);
				reserve.setHopeDate((Date)cols[3]);
				reserve.setResno((String)cols[4]);
				reserve.setAcptDate(acptDate);
				break;
				//reserve.setResno(resno);
			}
			return reserve;
		}catch(Exception e){
			log.error("Fetch ocs user error ",e);
			return null;
		}
	}

	public void insertUserUsePatient(LinkReserveData reserve) throws Exception{
		User user = new User();
		
		user.setName(reserve.getPatName());
		user.setActive(true);
		user.setCreated(new Date());
		
		user.setUpdated(new Date());
		
		user.setLoginId(CommonUtils.encryptTripleDes(reserve.getResno()));
		user.setPassword(null);
		
		addPatientNo(user,reserve.getPatno());
		addRoleToUser(user,Role.USER_ROLE);
		
		user.setLastReserveDate(reserve.getHopeDate());
		user.setLastReserveNo(reserve.getReserveNo());
		
		if("F".equals(reserve.getSex())){
			user.setGender(Gender.FEMALE);
		}else{
			user.setGender(Gender.MALE);
		}
		user.setInkbn(reserve.getInkbn());
		
		// 이부분은 실제 db link에서 데이타를 가져온다. 없어서 주석처리함.
//		UserOptions options = getUserOptions(user.getResno(),reserve.getPatno());
//		
//		user.setSamsungEmployee(options.isSamsungEmployee());
//		if(options.isNeedSleep() || options.isNeedStress()){
//			//둘중에 하나만 true여도 둘다 문진을 진행한다고 생각한다.
//			user.setNeedSleepTest(true);
//			user.setNeedStressTest(true);
//		}else{
//			user.setNeedSleepTest(false);
//			user.setNeedStressTest(false);
//		}
//		user.setNeedAgree(options.isNeedAgree());
		
		user.setSamsungEmployee(true);
		user.setNeedSleepTest(true);
		user.setNeedStressTest(true);
		user.setNeedAgree(true);
		
		createUser(user);
	}

	/**
	 * OCS에서 사용자의 건강나이,수면,스트레스,동의서 체크여부를 가져온다.
	 * @param resno
	 * @return
	 */
	public UserOptions getUserOptions(String resno,String patno){
		UserOptions options = new UserOptions();
		
		String sql = "select  a.patno" + 
				", max(UA4009)   health" +
				", max(UA4035)   stress" +
				", max(UA4026)   sleep " +        
				"from  (" +       
					"select  a.patno" +
					", case when (d.examcode = 'UA4009' or e.examcode = 'UA4009') then 'Y' end   UA4009 " + 
					", case when (d.examcode = 'UA4035' or e.examcode = 'UA4035') then 'Y' end   UA4035 " +
					", case when (d.examcode = 'UA4026' or e.examcode = 'UA4026') then 'Y' end   UA4026 " +
					"from  surtempt a " +  //--예약정보(종건)
					", su0pkgmt b " +  // --패키지정보
					", appatbat c " +  //--환자마스타
					", su0pkgdt d " +  //--패키지검사코드정보
					", su1addet e " + 
					"where a.hopedate >= trunc(sysdate) " + 
					"and a.resno = :resno " + // --주민번호 + 
					"and  b.pkgcode  = a.pkgcode " + 
					"and  b.timetype  in ('A', 'B', 'D') " +  //--순수,숙박패키지
					"and  c.patno    = a.patno " +
					"and  c.cpatno is null " +
					"and  to_char(a.hopedate,'yyyy')-to_char(c.birtdate,'yyyy') between '19' and '74' " + 
					"and  d.pkgcode  = a.pkgcode " + 
					"and  d.examcode in ('UA4009','UA4035','UA4026') " +
					"and  a.resvdate = e.resvdate(+) " +
					"and  a.patno    = e.patno   (+) " +
					"and  e.examcode(+)  = 'UA4009' " + 
				"union all " + 
					"select  a.patno" + 
					", case when (d.examcode = 'UA4009' or e.examcode = 'UA4009') then 'Y' end   UA4009 " +
					", case when (d.examcode = 'UA4035' or e.examcode = 'UA4035') then 'Y' end   UA4035 " +
					", case when (d.examcode = 'UA4026' or e.examcode = 'UA4026') then 'Y' end   UA4026 " +
					"from  surtempt a " +  //--예약정보(종건)
					", su0pkgmt b " +  //--패키지정보
					", appatbat c " +  //--환자마스타
					", su0pkgdt d " +  //--패키지검사코드정보
					", su1addet e " + 
					"where a.hopedate >= trunc(sysdate) " + 
					"and a.resno = :resno " + // --주민번호 + 
					"and  b.pkgcode  = a.pkgcode " + 
					"and  b.timetype  in ('A', 'B', 'D') " +  //--순수,숙박패키지
					"and  c.patno    = a.patno " + 
					"and  c.cpatno is null " + 
					"and  to_char(a.hopedate,'yyyy')-to_char(c.birtdate,'yyyy') between '19' and '74' " + 
					"and  d.pkgcode  = a.pkgcode " + 
					"and  d.examcode in ('UA4009','UA4035','UA4026') " +
					"and  a.resvdate = e.resvdate(+) " + 
					"and  a.patno    = e.patno   (+) " +
					"and  e.examcode(+)  = 'UA4035' " + 
				"union all " + 
					"select  a.patno" + 
					", case when (d.examcode = 'UA4009' or e.examcode = 'UA4009') then 'Y' end   UA4009 " +
					", case when (d.examcode = 'UA4035' or e.examcode = 'UA4035') then 'Y' end   UA4035 " +
					", case when (d.examcode = 'UA4026' or e.examcode = 'UA4026') then 'Y' end   UA4026 " +
					"from  surtempt a " +  //--예약정보(종건)
					", su0pkgmt b " +  //--패키지정보
					", appatbat c " +  //--환자마스타
					", su0pkgdt d " +  //--패키지검사코드정보
					", su1addet e " + 
					"where a.hopedate >= trunc(sysdate) " + 
					"and a.resno = :resno " + // --주민번호 + 
					"and  b.pkgcode  = a.pkgcode " + 
					"and  b.timetype  in ('A', 'B', 'D') " + //--순수,숙박패키지
					"and  c.patno    = a.patno " + 
					"and  c.cpatno is null " + 
					"and  to_char(a.hopedate,'yyyy')-to_char(c.birtdate,'yyyy') between '19' and '74' " + 
					"and  d.pkgcode  = a.pkgcode " + 
					"and  d.examcode in ('UA4009','UA4035','UA4026') " + 
					"and  a.resvdate = e.resvdate(+) " + 
					"and  a.patno    = e.patno   (+) " + 
					"and  e.examcode(+)  = 'UA4026' " +      
				") a " + 
				"group by a.patno ";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("resno", resno);
		
		List<Object> results = query.getResultList();
		
		for(Object row : results){
			Object[] cols = (Object[]) row;
			
			String health = "N";
			String stress = "N";
			String sleep = "N";
			
			if(cols[1] != null)
				health = "" + (Character)cols[1];
			
			if(cols[2] != null)
				stress = "" + (Character)cols[2];
			
			if(cols[3] != null)
				sleep = "" + (Character)cols[3];
			
			options.setSamsungEmployee("Y".equals(health) ? true : false);
			options.setNeedStress("Y".equals(stress) ? true : false);
			options.setNeedSleep("Y".equals(sleep) ? true : false);
			
			break;
		}
		
		String sql2 = "select distinct decode(EXPDATE, null, AGRESTAT, 'C') astat " + 
                         "from (" +
                                "SELECT " +
                                        "max(a.patno)" + 
                                       ",max(a.AGREDATE)" +
                                       ",max(a.EXPDATE) as expdate" + 
                                       ",max(a.EXPTEXT)" +
                                       ",a.AGRESTAT" + 
                                       ",decode(max(a.editdate), null, max(a.entdate), max(a.editdate)) as e_date" +
                                       ",a.entid" + 
                                       ",max(a.entip)" +
                                       ",RANK() OVER (ORDER BY decode(max(a.editdate), null, max(a.entdate), max(a.editdate)) DESC) AS E_RANK " +
                                 "FROM SUCOAGRT a " +
                                 "where  patno = :patno " +  //--수진자번호
                                 "group by a.AGRESTAT, a.entid " + 
                                 "order by a.AGRESTAT, a.entid " + 
                              ") " + 
                        "where e_rank = 1 " +
                        "and rownum =1 " ;
		Query query2 = em.createNativeQuery(sql2);
		query2.setParameter("patno", patno);
		
		List<Object> results2 = query2.getResultList();
		for(Object row : results2){
			String stat = (String)row;
			if("A".equals(stat) || "P".equals(stat)){
				//기존에 동의 및 부분동의 한경우임.
				options.setNeedAgree(false);
			}
			break;
		}
		
		return options;
	}
	
	/**
	 * 환자번호를 추가한다.
	 * @param user
	 * @param patientNo
	 */
	public void addPatientNo(User user,String patientNo){
		if(user.getPatientNos() == null){
			user.setPatientNos(new ArrayList<EmbeddedPatientNo>());
		}
		for(EmbeddedPatientNo row : user.getPatientNos()){
			row.setActive(false);
		}
		EmbeddedPatientNo pno = new EmbeddedPatientNo();
		pno.setPatientNo(patientNo);
		pno.setActive(true);
		user.getPatientNos().add(pno);
	}
	
	
	/**
	 * 해당 사용자에게 해당 role을 부여한다.
	 * 이미 해당 role이 할당되어있으면 추가적인 작업을 하지는 않는다.
	 * 이 내부에서는 JPA의 State변화를 일으키지 않은다.
	 * 즉 이 함수를 호출한 측에서 User를 persist나 merge를 호출해야 적용된다. 
	 */
	public void addRoleToUser(User user, String roleName) {
		Set<UserRoles> roles = user.getRoles();
		if(roles == null){
			roles = new HashSet<UserRoles>();
			user.setRoles(roles);
		}
		boolean needAdd = true;
		
		for(UserRoles role : roles){
			if(role.getRoleName().equals(roleName)){
				needAdd = false;
				break;
			}
		}
		
		if(needAdd){
			UserRoles newRole = new UserRoles();
			newRole.setUser(user);
			newRole.setRoleName(roleName);
			roles.add(newRole);
		}
	}

	public User loadUser(String userId) {
		return em.getReference(User.class, userId);
	}

	@Override
	public List<User> getUsers(String role) {
		String queryString = "select userRoles.user from UserRoles as userRoles where userRoles.roleName = :role";
		Query query = em.createQuery(queryString);
		query.setParameter("role", role);
		return (List<User>)query.getResultList();
	}

	@Override
	public List<User> getNurses() {
		return this.getUsers(Role.NURSE_ROLE);
	}

	@Override
	public User findPatient(String patientNo) {
		String hql = "SELECT a FROM User a JOIN a.patientNos pa WHERE pa.patientNo = :patientNo";
		Query query = em.createQuery(hql);
		query.setParameter("patientNo",patientNo);
		query.setHint("org.hibernate.cacheable", true);
		
		List<User> users = (List<User>)query.getResultList();
		if(users.size() > 0){
			return users.get(0);
		}
		return null;
	}

	
	
	@Override
	public void updateOcsProgress(String patno, Date reserveDate, int progress) {
		Session session = (Session)em.unwrap(Session.class);
		
		SQLQuery updateQuery = session.createSQLQuery("UPDATE " + IUserService.OCS_USER_TABLE + " a SET a.webstat = :webStat,a.webdate = sysdate " +
				"WHERE a.patno = :patno AND TO_CHAR(a.hopedate,'YYYY/MM/DD') = :hopeDate")
				.addEntity(LinkReserveData.class);
		
		updateQuery.setParameter("patno", patno);
		updateQuery.setParameter("webStat", ""+progress);
		updateQuery.setParameter("hopeDate", fm.format(reserveDate));
		
		updateQuery.executeUpdate();
		
	}

	@Override
	public String getCompanyName(String patientNo,Date hopeDate) {
		
		String sql = "SELECT distinct(a.custname) FROM su1custt a,surtempt b WHERE a.custcode = b.custcode AND b.patno = :patno AND TO_CHAR(b.hopedate,'YYYY/MM/DD') = :hopeDate";
		Query query = em.createNativeQuery(sql);
		query.setParameter("patno", patientNo);
		query.setParameter("hopeDate", fm.format(hopeDate));
		
		List<Object> results = query.getResultList();
		if(results.size() == 0){
			return null;
		}else{
			String cal = (String)results.get(0);
			return cal;
		}
		
	}

	@Override
	public void updateOcsUser(String patientNo, Date reserveDate,
			ParentProtection protection) {
		if(protection.getSmsAgree() != null && protection.getSmsAgree()){
			Session session = (Session)em.unwrap(Session.class);
			
			String sql = "UPDATE " + IUserService.OCS_USER_TABLE + " a SET a.gudantel = :parentPhone WHERE a.patno = :patno AND TO_CHAR(a.hopedate,'YYYY/MM/DD') = :hopeDate";
			
			SQLQuery updateQuery = session.createSQLQuery(sql).addEntity(LinkReserveData.class);
			
			updateQuery.setParameter("patno", patientNo);
			updateQuery.setParameter("parentPhone", protection.getParentPhone());
			updateQuery.setParameter("hopeDate", fm.format(reserveDate));
			
			updateQuery.executeUpdate();
		}
	}

/*	*//**
	 * 결과지 업데이트시 NULL값은 업데이트 하지 않는다.
	 * 
	 * @param data
	 * @return
	 *//*
	private boolean checkData(String data){

		if(data != null && !"".equals(data) && !"".equals(data.trim())){
			return true;
		}
		return false;
	}*/
	@Override
	public void updateOcsUser(String patientNo, Date reserveDate,
			ResultRequest request) {
		int askType = 0;
		switch(request.getAskType()){
		case EMAIL:
			askType = 4;
			break;
		case POST:
			askType = 2;
			break;
		default :
			askType = 1;
			break;
		}
		
		
		Session session = (Session)em.unwrap(Session.class);
		
		String sql = "UPDATE " + IUserService.OCS_USER_TABLE + " a SET a.telno3 = :cellPhone,a.telno1 = :companyPhone," +
				"a.zipcd = :zipcode,a.zpaddress = :zpAddress,a.address = :address,a.asktype = :askType,a.email = :email,a.emailyn = :emailYn ";
	
		sql = sql + "WHERE a.patno = :patno AND TO_CHAR(a.hopedate,'YYYY/MM/DD') = :hopeDate";
		
		
/*		String sql = "UPDATE " + IUserService.OCS_USER_TABLE + " a SET ";
		
		// 전화번호
		if(checkData(request.getCellPhone())){
			sql += " a.telno3 = :cellPhone,";
		}
		
		// 회사번호
		if(checkData(request.getCompanyPhone())){
			sql += "a.telno1 = :companyPhone,";
		}
		
		// 주소 1
		if(checkData(request.getAddress())){
			sql += "a.address = :address,";
		}
				
		sql += "a.zipcd = :zipcode,a.zpaddress = :zpAddress,a.asktype = :askType,a.email = :email,a.emailyn = :emailYn ";
	
		sql = sql + "WHERE a.patno = :patno AND TO_CHAR(a.hopedate,'YYYY/MM/DD') = :hopeDate";
*/		
		
		SQLQuery updateQuery = session.createSQLQuery(sql).addEntity(LinkReserveData.class);
		
		updateQuery.setParameter("patno", patientNo);
		updateQuery.setParameter("cellPhone", request.getCellPhone());
		updateQuery.setParameter("companyPhone", request.getCompanyPhone());
		updateQuery.setParameter("zipcode", request.getZipCode());
		updateQuery.setParameter("zpAddress", request.getZpAddress());
		updateQuery.setParameter("address", request.getAddress());
		updateQuery.setParameter("askType", askType);
		updateQuery.setParameter("email", request.getEmail());
		updateQuery.setParameter("emailYn", request.getEmailAgree() != null && request.getEmailAgree() ? "Y":"N");
		
		updateQuery.setParameter("hopeDate", fm.format(reserveDate));
		
		updateQuery.executeUpdate();
		
	}
	
	
}
