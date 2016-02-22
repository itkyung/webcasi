package com.kbsmc.webcasi.checkup.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.checkup.ICheckupInstanceDAO;
import com.kbsmc.webcasi.checkup.Su2Qustt;
import com.kbsmc.webcasi.checkup.ZipCode;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.OCSHistoryCode;
import com.kbsmc.webcasi.entity.OCSWebResult;
import com.kbsmc.webcasi.entity.OCSWebResultProgress;
import com.kbsmc.webcasi.entity.ParentProtection;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionResult;
import com.kbsmc.webcasi.entity.ResultRequest;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.EmbeddedPatientNo;
import com.kbsmc.webcasi.identity.entity.LinkReserveData;
import com.kbsmc.webcasi.identity.entity.User;

@Repository
public class CheckupInstanceDAO implements ICheckupInstanceDAO {
	private Log log = LogFactory.getLog(CheckupInstance.class);
	
	@Autowired IUserDAO userDao;
	
	private DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	
	public void createInstance(CheckupInstance instance) {
		instance.setCreateDate(new Date());
		instance.setUpdatedDate(new Date());
		em.persist(instance);
	}

	public void saveInstance(CheckupInstance instance) {
		instance.setUpdatedDate(new Date());
		em.merge(instance);
	}

	public CheckupInstance loadInstance(String id) {
		return em.getReference(CheckupInstance.class, id);
	}

	
	public CheckupInstance findInstance(User user, String reserveNo) {
		try{
			Query query = em.createQuery("FROM CheckupInstance a WHERE a.owner = :owner AND a.reserveNo = :reserveNo");
			query.setParameter("owner", user);
			query.setParameter("reserveNo", reserveNo);
			query.setHint("org.hibernate.cacheable", true);
			List<CheckupInstance> results = query.getResultList();
			if(results.size() > 0){
				return results.get(0);
			}
		}catch(Exception e){
			log.error("Find error :",e);
		}
		
		return null;
	}

	public CheckupInstance findInstance(User user, CheckupMaster master, InstanceStatus[] states) {
		String hql = "FROM CheckupInstance a WHERE a.owner = :owner AND a.master = :master AND " +
				"a.status in (";
		for(int i=0; i < states.length; i++){
			hql += ":status" + i;
			if(i < states.length-1){
				hql += ",";
			}
		}
		hql += ") Order by a.reserveDate desc";
		Query query = em.createQuery(hql);
		
		query.setParameter("owner", user);
		query.setParameter("master", master);
		
		for(int i=0; i < states.length; i++){
			query.setParameter("status"+i, states[i]);
		}
		query.setHint("org.hibernate.cacheable", true);
		
		List<CheckupInstance> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		
		return null;
	}
	
	
	
	@Override
	public CheckupInstance findInstance(User user, Date acptDate,
			InstanceStatus status) {
		
		Query query = em.createQuery("FROM CheckupInstance a WHERE a.owner = :owner AND to_char(a.acptDate,'yyyy-mm-dd') = :acptDate AND a.status = :status");
		query.setParameter("owner", user);
		query.setParameter("acptDate", fm.format(acptDate));
		query.setParameter("status", status);
		query.setHint("org.hibernate.cacheable", true);
		
		List<CheckupInstance> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		
		return null;
	}

	public QuestionResult findResult(User user, CheckupInstance instance,Question question,String objectiveValue,String itemGroup) {
		String hql = "FROM QuestionResult a WHERE a.owner = :owner AND a.instacne = :instance AND a.question = :question AND a.active = :active";
		if(objectiveValue != null){
			hql += " AND a.objectiveValue = :objectiveValue";
		}
		if(itemGroup != null && !"".equals(itemGroup)){
			hql += " AND a.itemGroup = :itemGroup";
		}
		
		Query query = em.createQuery(hql);
		query.setParameter("owner", user);
		query.setParameter("instance", instance);
		query.setParameter("question", question);
		query.setParameter("active", true);
		if(objectiveValue != null){
			query.setParameter("objectiveValue", objectiveValue);
		}
		if(itemGroup != null && !"".equals(itemGroup)){
			query.setParameter("itemGroup", NutritionItemType.valueOf(itemGroup));
		}
		//결과테이블은 캐쉬에서 제거함.
		//query.setHint("org.hibernate.cacheable", true);
		
		List<QuestionResult> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		
		return null;
	}

	public void createResult(QuestionResult result) {
		result.setCreateDate(new Date());
		result.setUpdateDate(new Date());
		em.persist(result);
	}

	public void saveResult(QuestionResult result) {
		result.setUpdateDate(new Date());
		em.merge(result);
	}

	@Override
	public List<QuestionResult> findResult(User user, CheckupInstance instance,Question question,
			QuestionResult exclusiveResult) {
		String hql = "FROM QuestionResult a WHERE a.owner = :owner AND a.instacne = :instance AND a.question = :question AND a.active = :active";
		if(exclusiveResult != null && exclusiveResult.getId() != null){
			hql += " AND a.id != :id";
		}
		
		Query query = em.createQuery(hql);
		query.setParameter("owner", user);
		query.setParameter("question", question);
		query.setParameter("active", true);
		query.setParameter("instance", instance);
	//	query.setHint("org.hibernate.cacheable", true);
		
		if(exclusiveResult != null && exclusiveResult.getId() != null){
			query.setParameter("id", exclusiveResult.getId());
		}
		return query.getResultList();
	}

	@Override
	public List<QuestionResult> findResult(User user, CheckupInstance instance, QuestionGroup group,
			int depth) {
		String hql = "FROM QuestionResult a WHERE a.owner = :owner AND a.instacne = :instance AND a.question.parentGroup = :group AND a.active = :active ";
		if(depth != 0){
			hql += "AND a.question.depth = :depth ";
		}
		hql += "Order by a.createDate asc";
		
		
		Query query = em.createQuery(hql);
		
		query.setParameter("owner", user);
		query.setParameter("group", group);
		if(depth != 0){
			query.setParameter("depth", depth);
		}
		query.setParameter("instance", instance);
		query.setParameter("active", true);
	//	query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
	}

	@Override
	public List<QuestionResult> findResult(User user, CheckupInstance instance,
			Question question) {
		String hql = "FROM QuestionResult a WHERE a.owner = :owner AND a.instacne = :instance AND a.question = :question AND a.active = :active";
		
		Query query = em.createQuery(hql);
		query.setParameter("owner", user);
		query.setParameter("question", question);
		query.setParameter("active", true);
		query.setParameter("instance", instance);
	//	query.setHint("org.hibernate.cacheable", true);
	
		return query.getResultList();
	}

	/**
	 * realtime이 true이면 itfy가 Y인것중에서 acptdate가 같은것을 찾고.
	 * false이면 itfy가 n인것을 찾는다.
	 */
	@Override
	public OCSWebResult findOcsResult(String patientNo, String version, String askCode, boolean realtime, Date acptDate) {
		String hql = "FROM OCSWebResult a WHERE a.patientNo = :patientNo AND a.pageCd = :pageCd AND a.askCode = :askCode ";
		if(realtime){
			hql += "AND a.itfYn = :itfYn AND to_char(a.acptDate,'yyyy-mm-dd') = :acptDate";
		}else{
			hql += "AND a.itfYn = :itfYn";
		}
		
		Query query = em.createQuery(hql);
		query.setParameter("patientNo", patientNo);
		query.setParameter("pageCd", version);
		query.setParameter("askCode", askCode);
		if(realtime){
			query.setParameter("itfYn", "Y");
			//if(acptDate == null) acptDate = new Date();
			query.setParameter("acptDate", fm.format(acptDate));
		}else{
			query.setParameter("itfYn", "N");
			
		}
		
		//여기는 caching을 하면 안된다. ocs에 업데이트하기 때문이다.
		//query.setHint("org.hibernate.cacheable", true);
	
		List<OCSWebResult> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}

	@Override
	public void createOcsResult(OCSWebResult result) {
		result.setEntDate(new Date());
		result.setEditDate(new Date());
		em.persist(result);
	}

	@Override
	public void saveOcsResult(OCSWebResult result) {
		result.setEditDate(new Date());
		em.merge(result);
	}

	@Override
	public OCSWebResultProgress findWebResultProgress(CheckupInstance instance,
			User user, QuestionGroup group) {
		
		String hql = "FROM OCSWebResultProgress a WHERE a.instance = :instance AND a.owner = :user AND a.questionGroup = :questionGroup";
		
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setParameter("user", user);
		query.setParameter("questionGroup", group);
		query.setHint("org.hibernate.cacheable", true);
	
		List<OCSWebResultProgress> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	
	}
	
	@Override
	public List<OCSWebResultProgress> findWebResultProgress(CheckupInstance instance) {
		String hql = "FROM OCSWebResultProgress a WHERE a.instance = :instance";
		
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setHint("org.hibernate.cacheable", true);
	
		List<OCSWebResultProgress> results = query.getResultList();
		return results;
	}

	@Override
	public void createOcsResult(OCSWebResultProgress progress) {
		progress.setCreateDate(new Date());
		progress.setLastUpdateDate(new Date());
		em.persist(progress);
	}
	
	
	@Override
	public void saveOcsResult(OCSWebResultProgress progress) {
		
		em.persist(progress);
	}

	@Override
	public void initSu1fcwtt(String patientNo, Date acptDate) {
		
		Query query = em.createNativeQuery("DELETE FROM checkup_su1fcwtt a WHERE a.patno like :patno " +
				"AND a.acptdate between to_date(:acptDate,'yyyy-mm-dd') and to_date(:acptDate,'yyyy-mm-dd')");
		query.setParameter("patno", patientNo);
		query.setParameter("acptDate", fm.format(acptDate));
		
		query.executeUpdate();
	}

	@Override
	public void initSu1fsrst(String patientNo, Date acptDate) {
		Query query = em.createNativeQuery("DELETE FROM checkup_su1fsrst a WHERE a.patno like :patno " +
				"AND a.acptdate between to_date(:acptDate,'yyyy-mm-dd') and to_date(:acptDate,'yyyy-mm-dd')");
		query.setParameter("patno", patientNo);
		query.setParameter("acptDate", fm.format(acptDate));
		
		query.executeUpdate();
		
	}

	@Override
	public void initSu1fssmt(String patientNo, Date acptDate) {
		Query query = em.createNativeQuery("DELETE FROM checkup_su1fssmt a WHERE a.patno like :patno " +
				"AND a.acptdate between to_date(:acptDate,'yyyy-mm-dd') and to_date(:acptDate,'yyyy-mm-dd')");
		query.setParameter("patno", patientNo);
		query.setParameter("acptDate", fm.format(acptDate));
		
		query.executeUpdate();
	}

	@Override
	public void initSu1fskmt(String patientNo, Date acptDate) {
		Query query = em.createNativeQuery("DELETE FROM checkup_su1fskmt a WHERE a.patno like :patno " +
				"AND a.acptdate between to_date(:acptDate,'yyyy-mm-dd') and to_date(:acptDate,'yyyy-mm-dd')");
		query.setParameter("patno", patientNo);
		query.setParameter("acptDate", fm.format(acptDate));
		
		query.executeUpdate();
		
	}

	@Override
	public void executeProcedure(final String patientNo, final Date acptDate)
			throws Exception {
		Session session = (Session) em.getDelegate();
		
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				
				/**
				CallableStatement cs0 = connection.prepareCall("{CALL CHECKUP_SU2QUSTT_UPDATE2(?,?,?,?,?,?,?)}");
				cs0.registerOutParameter(7, Types.VARCHAR);
				cs0.setString(1, "ADD");
				cs0.setString(2, "WEB");
				cs0.setString(3, "127.0.0.1");
				cs0.setString(4, fm.format(acptDate));
				cs0.setString(5, fm.format(acptDate));
				cs0.setString(6, patientNo);
				
				cs0.execute();
				String err = cs0.getString(7);
				if(err != null && "Y".equals(err)){
					//에러발생.
					throw new SQLException("CHECKUP_SU2QUSTT_UPDATE2 에러");
				}
				
				connection.commit();
				**/
				
				CallableStatement cs = connection.prepareCall("{CALL CHECKUP_SU1FCWTT_INSERT(?,?,?,?,?,?,?)}");
				cs.registerOutParameter(7, Types.VARCHAR);
				cs.setString(1, "ADD");
				cs.setString(2, "WEB");
				cs.setString(3, "127.0.0.1");
				cs.setString(4, fm.format(acptDate));
				cs.setString(5, fm.format(acptDate));
				cs.setString(6, patientNo);
				
				cs.execute();
				String err = cs.getString(7);
				if(err != null && "Y".equals(err)){
					//에러발생.
					throw new SQLException("CHECKUP_SU1FCWTT_INSERT 에러");
				}
				
				connection.commit();
				
				CallableStatement cs2 = connection.prepareCall("{CALL CHECKUP_SU1FSRST_INSERT(?,?,?,?,?,?,?)}");
				cs2.registerOutParameter(7, Types.VARCHAR);
				cs2.setString(1, "ADD");
				cs2.setString(2, "WEB");
				cs2.setString(3, "127.0.0.1");
				cs2.setString(4, fm.format(acptDate));
				cs2.setString(5, fm.format(acptDate));
				cs2.setString(6, patientNo);
				
				cs2.execute();
				err = cs2.getString(7);
				if(err != null && "Y".equals(err)){
					//에러발생.
					throw new SQLException("CHECKUP_SU1FSRST_INSERT 에러");
				}
				connection.commit();
				
				/*
				CallableStatement cs3 = connection.prepareCall("{CALL CHECKUP_SU1FSSMT_INSERT(?,?,?,?,?,?,?)}");
				cs3.registerOutParameter(7, Types.VARCHAR);
				cs3.setString(1, "ADD");
				cs3.setString(2, "WEB");
				cs3.setString(3, "127.0.0.1");
				cs3.setString(4, fm.format(acptDate));
				cs3.setString(5, fm.format(acptDate));
				cs3.setString(6, patientNo);
				
				cs3.execute();
				err = cs3.getString(7);
				if(err != null && "Y".equals(err)){
					//에러발생.
					throw new SQLException("CHECKUP_SU1FSSMT_INSERT 에러");
				}
				connection.commit();
				
				CallableStatement cs4 = connection.prepareCall("{CALL CHECKUP_SU1FSKMT_INSERT(?,?,?,?,?,?,?)}");
				cs4.registerOutParameter(7, Types.VARCHAR);
				cs4.setString(1, "ADD");
				cs4.setString(2, "WEB");
				cs4.setString(3, "127.0.0.1");
				cs4.setString(4, fm.format(acptDate));
				cs4.setString(5, fm.format(acptDate));
				cs4.setString(6, patientNo);
				
				cs4.execute();
				err = cs4.getString(7);
				if(err != null && "Y".equals(err)){
					//에러발생.
					throw new SQLException("CHECKUP_SU1FSSMT_INSERT 에러");
				}
				connection.commit();
				
				CallableStatement cs5 = connection.prepareCall("{CALL CHECKUP_SU1FSSMT_INUPDATE(?,?,?,?,?,?,?)}");
				cs5.registerOutParameter(7, Types.VARCHAR);
				cs5.setString(1, "ADD");
				cs5.setString(2, "WEB");
				cs5.setString(3, "127.0.0.1");
				cs5.setString(4, fm.format(acptDate));
				cs5.setString(5, fm.format(acptDate));
				cs5.setString(6, patientNo);
				
				cs5.execute();
				err = cs5.getString(7);
				if(err != null && "Y".equals(err)){
					//에러발생.
					throw new SQLException("CHECKUP_SU1FSSMT_INUPDATE 에러");
				}
				connection.commit();
				*/
			}
			
		});
		
	}

	@Override
	public long getCalory(String patientNo, Date acptDate) {
		Query query = em.createNativeQuery("SELECT c.patno, ROUND ( (c.ss02*4) + (c.ss03*9) + (c.ss04*4), -1) ss01 " +
				"FROM ( SELECT a.patno,a.acptdate,a.qstno,a.askseq,a.htype, " +
				"ROUND(SUM(sb01),3) ss01, ROUND(SUM(sb02),3) ss02, ROUND(SUM(sb03),3) ss03,ROUND(SUM(sb04),3) ss04 " +
				"FROM checkup_su1fsrst a WHERE 1 =1 AND a.acptdate = to_date(:acptDate,'yyyy-mm-dd') " +
				"AND a.patno = :patno GROUP BY a.patno,a.acptdate,a.qstno,a.askseq,a.htype) c");
		query.setParameter("patno", patientNo);
		query.setParameter("acptDate", fm.format(acptDate));
		
		List<Object> results = query.getResultList();
		if(results.size() == 0){
			return 0;
		}
		
		for(Object row : results){
			Object[] cols = (Object[]) row;
			Number cal = (Number)cols[1];
			return cal.longValue();
		}
		
		return 0;
	}


	/**
	 * OCS Histstory에서 과거이력을 찾는다.
	 */
	@Override
	public OCSHistoryCode findOcsHistory(User user, String askCode) {
		
		Query query = em.createQuery("FROM OCSHistoryCode a WHERE a.owner = :user AND a.ocsAskCode = :askCode");
		query.setParameter("user", user);
		query.setParameter("askCode", askCode);
		query.setHint("org.hibernate.cacheable", true);
		
		List<OCSHistoryCode> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		
		return null;
	}

	
	
	@Override
	public void initHistoryCode(User user) {
	
		Session session = (Session)em.unwrap(Session.class);
		SQLQuery sqlQuery = session.createSQLQuery("DELETE from CHECKUP_OCS_HISTORY a WHERE a.owner = :userId")
				.addEntity(OCSHistoryCode.class);
		sqlQuery.setParameter("userId", user.getId());
		sqlQuery.executeUpdate();
		
	}

	/**
	 * 일단 해당 사용자의 기존 ocsWebResult중에서 itfyn이 N인 녀석을 다 Y로 업데이트한다.
	 * 과거 이력을 OCS DB에서 읽어와서 ocsHistory에 넣는다.
	 * Async로 호출된다.
	 */
	
	@Override
	public void initOcsHistoryCode(User user, String patno,Date reserveDate) {
		
		Session session = (Session)em.unwrap(Session.class);
		
		SQLQuery updateQuery = session.createSQLQuery("UPDATE checkup_su2qustt a SET a.itfyn = 'Y' WHERE a.patno = :patno AND a.itfyn = 'N'")
				.addEntity(OCSWebResult.class);
		updateQuery.setParameter("patno", patno);
		updateQuery.executeUpdate();
		
//		
//		SQLQuery sqlQuery = session.createSQLQuery("DELETE from CHECKUP_OCS_HISTORY a WHERE a.owner = :userId")
//				.addEntity(OCSHistoryCode.class);
//		sqlQuery.setParameter("userId", user.getId());
//		sqlQuery.executeUpdate();
		
		String sql = 
				"SELECT c.askcode,c.answer " +
						" from (" +
							"SELECT  a.askcode askcode" + 
							", a.ASKNAME ASKNAME" +
							", a.divcode divcode" + 
							", a.divnum divnum"+   
							", '    '|| b.anscode || '.'|| b.ansname askname2" +  
							", b.anscode anscode" +
							", b.anscode seqno" + 
							", a.askno askno" +     
							", a.anstype anstype" +
							", substr(a.ASKCODE,1,3) askgroup " + 
							" FROM su0asktt a" +  //--문진질문코드정보
							", su0anstt b " +  //--문진답변코드정보
							"WHERE  1=1 " +  //-- a.divcode is not null       
							"AND   a.askcode = b.askcode " +      
							"AND ( " + 
							"((substr(a.ASKCODE,1,3)='KMD') and DIVCODE = '004') " +            //-- KMD = 약물부작용(DIVCODE="004") 
							"OR ((substr(a.ASKCODE,1,3)='KCA')) " +                            //-- KCA = 암질병력 
							"OR ((substr(a.ASKCODE,1,3)='KOP')) " +                            //-- KOP = 수술력  
							"OR ( a.ASKCODE in (  'KPS1_1','KAL2_1','KDI1_2','KDI2_4','KDI4_2','KDI4_43','KDI5_7','KDI15_5','KFT0_1', " +            
			                                   "'KFT10_1','KFT10_2','KFT10_3','KFT10_4','KFT10_5','KFT10_6','KFT10_7','KFT10_8', " +      
			                                   "'KFT11_1','KFT11_2','KFT11_3','KFT11_4','KFT11_5','KFT11_6','KFT11_7','KFT11_8', " +      
			                                   "'KFT12_1','KFT12_2','KFT12_3','KFT12_4','KFT12_5','KFT12_6','KFT12_7','KFT12_8', " +      
			                                   "'KFT13_1','KFT13_2','KFT13_3','KFT13_4','KFT13_5','KFT13_6','KFT13_7','KFT13_8', " +      
			                                   "'KFT14_1','KFT14_2','KFT14_3','KFT14_4','KFT14_5','KFT14_6','KFT14_7','KFT14_8', " +      
			                                   "'KFT15_1','KFT15_2','KFT15_3','KFT15_4','KFT15_5','KFT15_6','KFT15_7','KFT15_8', " +      
			                                   "'KFT16_2','KFT16_3','KFT16_4','KFT16_5','KFT16_6','KFT16_7','KFT16_8','KFT17_2',  " +     
			                                   "'KFT17_3','KFT17_4','KFT17_5','KFT17_6','KFT17_7','KFT17_8','KFT18_2','KFT18_3',  " +     
			                                   "'KFT18_4','KFT18_5','KFT18_6','KFT18_7','KFT18_8','KFT19_1','KFT19_3','KFT19_4',  " +     
			                                   "'KFT19_5','KFT19_6','KFT19_7','KFT19_8','KFT1_1','KFT1_2','KFT1_3','KFT1_4',  " +         
			                                   "'KFT1_5','KFT1_6','KFT1_7','KFT1_8','KFT20_1','KFT20_2','KFT20_3','KFT20_4','KFT20_5'," +
			                                   "'KFT20_6','KFT20_7','KFT20_8','KFT21_1','KFT21_2','KFT21_3','KFT21_4','KFT21_5'," +      
			                                   "'KFT21_6','KFT21_7','KFT21_8','KFT22_1','KFT22_2','KFT22_3','KFT22_4','KFT22_5'," +      
			                                   "'KFT22_6','KFT22_7','KFT22_8','KFT23_1','KFT23_2','KFT23_3','KFT23_4','KFT23_5'," +      
			                                   "'KFT23_6','KFT23_7','KFT23_8','KFT24_1','KFT24_2','KFT24_3','KFT24_4','KFT24_5'," +      
			                                   "'KFT24_6','KFT24_7','KFT24_8','KFT25_1','KFT25_2','KFT25_3','KFT25_4','KFT25_5'," +      
			                                   "'KFT25_6','KFT25_7','KFT25_8','KFT2_1','KFT2_2','KFT2_3','KFT2_4','KFT2_5'," +           
			                                   "'KFT2_6','KFT2_7','KFT2_8','KFT3_1','KFT3_2','KFT3_3','KFT3_4','KFT3_5','KFT3_6'," +     
			                                   "'KFT3_7','KFT3_8','KFT40_1','KFT40_2','KFT40_3','KFT40_4','KFT40_5','KFT40_6'," +        
			                                   "'KFT40_7','KFT40_8','KFT4_1','KFT4_2','KFT4_3','KFT4_4','KFT4_5','KFT4_6'," +            
			                                   "'KFT4_7','KFT4_8','KFT5_1','KFT5_2','KFT5_3','KFT5_4','KFT5_5','KFT5_6'," +              
			                                   "'KFT5_7','KFT5_8','KFT6_1','KFT6_2','KFT6_3','KFT6_4','KFT6_5','KFT6_6','KFT6_7'," +     
			                                   "'KFT6_8','KFT7_1','KFT7_2','KFT7_3','KFT7_4','KFT7_5','KFT7_6','KFT7_7','KFT7_8'," +     
			                                   "'KFT8_1','KFT8_2','KFT9_1','KFT9_2','KFT9_3','KFT9_4','KFT9_5','KFT9_6','KFT9_7', " +    
			                                   "'KFT9_8','KFL1_1','KFL5_1','KFL5_2','KFL5_3','KFL5_4','KFL5_5','KFL5_6','KFL6_1', " +    
			                                   "'KSO3_1','KSD1_1','KSD2_1','KRA2_1','KRA3_1','KRA4_1','KRA4_2','KRA5_1','KRA6_1'," +            
			                                   "'KOP3_1','KOP3_4','KOP4_1','KOP4_4','KOP5_1','KOP5_4','KOP6_1','KOP6_4','KOP7_1'," +
			                                   "'KOP7_4','KOP8_4','KOP9A_4','KOP9B_4','KOP9C_1','KOP9C_4','KOP9D_1','KOP9D_4'," +
			                                   "'KOP9E_1','KOP9E_2','KOP9E_3','KOP9E_4' ) " +     
			                                   ") " +
			                           ")" +   
			                 ") a" +
			                 ",("  +
			                 	"select n.acptdate,n.patno,n.askcode,n.answer " +                                    
			                 	"from  su2qustt n " +                                                     
			                 	"where  patno    = :patno " +  //--:IsPatno                                           
			                 	"and  n.qstno = 'K' " +   //--종합건진 
			                 	"and  n.acptdate = ( select max(a.ACPTDATE) " +                          
			                 						"from su2qustt a" +    //--문진결과(종건)                             
			                 						",su1resvt b " +    //--접수정보(종건)                             
			                 						" where 1=1 " +                                       
			                 						"and a.acptdate = b.hopedate " + // --접수일자        
			                 						"and a.patno    = b.patno " +                      
			                 						"and to_char(a.acptdate,'yyyy-mm-dd') < :hopeDate " + ///*:IsSdate*/
			                 						"and a.patno    = :patno " + ///*:IsPatno*/  
			                 						")" + 
			                 	") c " +                          
      							"WHERE    a.askcode = c.askcode(+) " +  //-- 과거답안        
      							"order by DIVCODE, ASKNO";
		
		Query query = em.createNativeQuery(sql)
				.setParameter("patno", patno)
				.setParameter("hopeDate", fm.format(reserveDate));
		
		List<Object> results = query.getResultList();
		
		for(Object row : results){
			Object[] cols = (Object[]) row;
 			if((String)cols[0] == null) continue;
			OCSHistoryCode code = new OCSHistoryCode();
			code.setOwner(user);
			code.setOcsAskCode((String)cols[0]);
			code.setOcsAnswer((String)cols[1]);
			session.saveOrUpdate(code);
		}
	}

	@Override
	public CheckupInstance findInstanceByReserveDateStr(User user,
			String reserveDateStr) {
		
		Query query = em.createNativeQuery("Select a.id From CHECKUP_INSTANCE a Where a.owner = :owner AND to_char(a.reserve_date,'yyyy-mm-dd') = :reserveDate");
		query.setParameter("owner", user.getId());
		query.setParameter("reserveDate", reserveDateStr);
		
		List<Object[]> results = query.getResultList();
		if(results.size() > 0){
			Object col = (Object)results.get(0);
			String id = (String)col;
			
			CheckupInstance instance = loadInstance(id);
			return instance;
		}
		
		return null;
	}

	@Override
	public void createNurseResult(NurseCheckResult result) {
		result.setCreateDate(new Date());
		em.persist(result);
	}

	@Override
	public void saveNurseResult(NurseCheckResult result) {
		result.setUpdateDate(new Date());
		em.merge(result);
	}

	@Override
	public NurseCheckResult findNurseResult(Question question,
			CheckupInstance instance, User patient) {
		Query query = em.createQuery("FROM NurseCheckResult a WHERE a.active = :active AND a.question = :question AND a.instance = :instance AND a.patient = :patient");
		query.setParameter("active", true);
		query.setParameter("question", question);
		query.setParameter("instance", instance);
		query.setParameter("patient", patient);
		query.setHint("org.hibernate.cacheable", true);
		
		List<NurseCheckResult> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	@Override
	public List<NurseCheckResult> findNurseResult(CheckupInstance instance,QuestionGroup group, int depth) {
		String hql = "FROM NurseCheckResult a WHERE a.active = :active AND a.question.parentGroup = :group " +
				"AND a.instance = :instance ";
		
		if(depth != -1){
			hql += "AND a.question.depth = :depth";
		}
		
		Query query = em.createQuery(hql);
		
		query.setParameter("active", true);
		query.setParameter("group", group);
		query.setParameter("instance", instance);
		if(depth != -1)
			query.setParameter("depth", depth);
		query.setHint("org.hibernate.cacheable", true);
		
		List<NurseCheckResult> results = query.getResultList();
		
		return results;
	}

	@Override
	public void updateRemoteSu2qustt(String patno, Date acptDate,
			String askCode, String answer, String version) {
		boolean isExist = false;
		
		Query selectQuery = em.createNativeQuery("SELECT count(*) FROM su2qustt a WHERE a.patno = :patno " +
				"AND qstno = 'K' AND a.askseq = 1 AND a.askcode = :askcode AND to_char(a.acptdate,'yyyy-mm-dd') = :acptDate");
		selectQuery.setParameter("patno", patno);
		selectQuery.setParameter("askcode", askCode);
		selectQuery.setParameter("acptDate", fm.format(acptDate));
		
		List<Object> results = selectQuery.getResultList();
		if(results.size() > 0){
			Object col = (Object)results.get(0);
			Number count = (Number)col;
			if(count.intValue() > 0){
				isExist = true;
			}
		}
		
		if(isExist){
			Query updateQuery = em.createNativeQuery("UPDATE su2qustt a SET a.answer = :answer,a.editdate = sysdate WHERE a.patno = :patno " +
				"AND qstno = 'K' AND a.askseq = 1 AND a.askcode = :askcode AND to_char(a.acptdate,'yyyy-mm-dd') = :acptDate");
			updateQuery.setParameter("answer", answer);
			updateQuery.setParameter("patno", patno);
			updateQuery.setParameter("askcode", askCode);
			updateQuery.setParameter("acptDate", fm.format(acptDate));
			updateQuery.executeUpdate();
		}else{
			//이 경우는 존재하면 안된다.
			//로컬 임시 테스트 용도로 추가한다.
//			Query insertQuery = em.createNativeQuery("INSERT INTO su2qustt(patno,acptdate,qstno,askcode,askseq,answer,inkbn,pagecd,entdate) " +
//					"VALUES(:patno,:acptdate,'K',:askcode,1,:answer,'1',:version,sysdate)");
//			insertQuery.setParameter("patno", patno);
//			insertQuery.setParameter("acptdate", acptDate);
//			insertQuery.setParameter("askcode", askCode);
//			insertQuery.setParameter("answer", answer);
//			insertQuery.setParameter("version", version);
//			
//			insertQuery.executeUpdate();
			
		}
	}

	@Override
	public void removeOcsResult(OCSWebResult result) {
		em.remove(result);
	}

	@Override
	public void removeRemoteSu2qustt(String patno, Date acptDate,
		String askCode, String answer, String version) {
	
		Query updateQuery = em.createNativeQuery("DELETE FROM su2qustt WHERE a.patno = :patno " +
			"AND qstno = 'K' AND a.askseq = 1 AND a.askcode = :askcode AND to_char(a.acptdate,'yyyy-mm-dd') = :acptDate");
		updateQuery.setParameter("answer", answer);
		updateQuery.setParameter("patno", patno);
		updateQuery.setParameter("askcode", askCode);
		updateQuery.setParameter("acptDate", fm.format(acptDate));
		updateQuery.executeUpdate();
		
	}

	@Override
	public List<CheckupInstance> findInstance(boolean existAcptDate,InstanceStatus... states) {
		
		String hql = "FROM CheckupInstance a WHERE a.status in (";
		for(int i=0; i < states.length; i++){
			hql += ":status" + i;
			if(i < states.length-1){
				hql += ",";
			}
		}
		hql += ") ";
		if(existAcptDate){
			hql += "AND a.acptDate is not null";
		}else{
			hql += "AND a.acptDate is null";
		}
		hql += " Order by a.reserveDate desc";
		
		Query query = em.createQuery(hql);

		
		for(int i=0; i < states.length; i++){
			query.setParameter("status"+i, states[i]);
		}
		query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
		
	}

	/**
	 * acptDate가 null이 아니면서 아직 complete되지 않은 문진을 찾아서 ocs에 확인을 해서 해당 날짜의 문진이 acptDate가 null로 바뀌엇는지 확인.
	 * 안바뀌엇으면 완료. 바뀌엇으면 acptDate를 바꿈.
	 * 
	 */
	@Override
	public void completeCheckupInstance() {
		List<CheckupInstance> results = findInstance(true,InstanceStatus.IN_PROGRESS,InstanceStatus.READY,InstanceStatus.FIRST_COMPLETED);
		for(CheckupInstance instance : results){
			if(isChangeToAcptdateNull(instance)){
				instance.setAcptDate(null);
				
			}else{
			
				instance.setStatus(InstanceStatus.COMPLETED);
				saveInstance(instance);
			}
		}
	}

	private boolean isChangeToAcptdateNull(CheckupInstance instance){
		String patno = null;
		for(EmbeddedPatientNo pno : instance.getOwner().getPatientNos()){
			if(pno.isActive()){
				patno = pno.getPatientNo();
				break;
			}
		}
		
		String sql = "SELECT a.acptdate FROM " + IUserService.OCS_USER_TABLE + 
				" a WHERE TO_CHAR(a.hopedate,'YYYY/MM/DD') = :hopedate AND a.patno = :patno";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("hopedate", fm.format(instance.getReserveDate()));
		query.setParameter("patno", patno);
		
		List<Object> results = query.getResultList();
		
		if(results.size() > 0){
			Date acptDate = (Date)results.get(0);
			if(acptDate == null) return true;
		}
		return false;
	}
	
	@Override
	public List<CheckupInstance> findHistory(User user, boolean isNotNullAcptDate) {
		
		String hql = "FROM CheckupInstance a WHERE a.owner = :owner ";
		
		if(isNotNullAcptDate){
			hql += "AND a.acptDate is not null Order by a.acptDate desc";
		}else{
			hql += "Order by a.reserveDate desc";
		}
		
		Query query = em.createQuery(hql);
		query.setParameter("owner", user);
		query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
	}

	@Override
	public List<CheckupInstance> findInstance(Date acptDate, List<String> patnos,int start,int limit) {
		String hql = "FROM CheckupInstance a WHERE ";
		if(acptDate != null){
			hql += "to_char(a.acptDate,'yyyy-mm-dd') = :acptDate ";
		}
		
		if(patnos.size() > 0){
			if(acptDate != null){
				hql += "AND ";
			}
			hql += "a.patno in (";
			for(int i=0; i < patnos.size(); i++){
				hql+= ":patno" + i;
				if(i < patnos.size()-1)
					hql += ",";
			}
			hql += ")";
		}
		
		hql += "Order by a.acptDate desc";
		
		Query query = em.createQuery(hql);
		
		if(acptDate != null){
			query.setParameter("acptDate", fm.format(acptDate));
		}
		
		if(patnos.size() > 0){
			for(int i=0; i < patnos.size(); i++){
				query.setParameter("patno"+i, patnos.get(i));
			}
		}
		query.setHint("org.hibernate.cacheable", true);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		
		return query.getResultList();
	}

	/*
	 * result테이블에 instance의 철자가 instacne 로 이미 생성되어있어서 어쩔수없이 틀린 철자를 일단 그대로 이용함.
	 * (non-Javadoc)
	 * @see com.kbsmc.webcasi.checkup.ICheckupInstanceDAO#countRequiredResult(com.kbsmc.webcasi.entity.CheckupInstance)
	 */
	@Override
	public int countRequiredResult(CheckupInstance instance) {
		String hql = "SELECT count(distinct a.question) FROM QuestionResult a WHERE a.instacne = :instance AND a.question.depth = :depth " +
				"AND a.question.required = :required AND a.active = :active";
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setParameter("depth", 1);
		query.setParameter("required", true);
		query.setParameter("active", true);
//		query.setHint("org.hibernate.cacheable", true);
		
		Number count =  (Number)query.getSingleResult();
		
		return count.intValue();
	}

	@Override
	public ParentProtection loadProtection(String id) {
		return em.getReference(ParentProtection.class, id);
	}

	@Override
	public void saveProtection(ParentProtection protection) {
		protection.setUpdateDate(new Date());
		em.merge(protection);
	}

	@Override
	public void createProtection(ParentProtection protection) {
		protection.setCreateDate(new Date());
		protection.setUpdateDate(new Date());
		em.persist(protection);
	}

	@Override
	public ParentProtection findProtection(CheckupInstance instance) {
		String hql = "FROM ParentProtection a WHERE a.instance = :instance";
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setHint("org.hibernate.cacheable", true);
		
		List<ParentProtection> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		
		return null;
	}

	@Override
	public List<ZipCode> searchZipCode(String dongName) {
		List<ZipCode> codes = new ArrayList<ZipCode>();
		
		String sql = "SELECT a.zipcode,a.sido,a.gugun,a.dong,a.ri,a.bunji FROM su0postt a WHERE a.dong like :dong OR a.ri like :dong Order by a.sido,a.gugun,a.dong,a.ri asc";
		Query query = em.createNativeQuery(sql);
		query.setParameter("dong", dongName + "%");
		
		List<Object> results = query.getResultList();
		
		for(Object row : results){
			Object[] cols = (Object[]) row;
			String zipcode = (String)cols[0];
			String sido = (String)cols[1];
			String gugun = (String)cols[2];
			String dong = (String)cols[3];
			String ri = (String)cols[4];
			String bunzi = (String)cols[5];
			
			ZipCode code = new ZipCode();
			code.setZipCode(zipcode);
			code.setBunzi(bunzi == null ? "-" : bunzi);
			if(ri == null || ri.equals("null")) ri = "";
			code.setAddress(sido + " " + gugun + " " + dong + " " + ri);
			codes.add(code);
			
			
		}
		
		return codes;
	}

	@Override
	public ResultRequest loadResultResult(String id) {
		return em.getReference(ResultRequest.class,id);
	}

	@Override
	public void saveResultRequest(ResultRequest request) {
		request.setUpdateDate(new Date());
		em.merge(request);
	}

	@Override
	public void createResultRequest(ResultRequest request) {
		request.setCreateDate(new Date());
		request.setUpdateDate(new Date());
		em.persist(request);
	}

	@Override
	public ResultRequest findResultRequest(CheckupInstance instance) {
		String hql = "FROM ResultRequest a WHERE a.instance = :instance";
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setHint("org.hibernate.cacheable", true);
		
		List<ResultRequest> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		
		return null;
	}

	@Override
	public List<CheckupInstance> findInstance(String startDate, String endDate) {
		
		Query query = em.createQuery("FROM CheckupInstance a WHERE " +
				"to_char(a.reserveDate,'yyyy-mm-dd') >= :startDate AND to_char(a.reserveDate,'yyyy-mm-dd') <= :endDate AND a.status in (:status1,:status2)");
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("status1", InstanceStatus.IN_PROGRESS);
		query.setParameter("status2", InstanceStatus.FIRST_COMPLETED);
		query.setHint("org.hibernate.cacheable", true);
		
		List<CheckupInstance> results = query.getResultList();
		return results;
	}

	@Override
	public String getIdOcsResult(String patientNo, String version,
			String askCode, boolean realtime, Date acptDate) {
		String hql = "Select a.id FROM OCSWebResult a WHERE a.patientNo = :patientNo AND a.pageCd = :pageCd AND a.askCode = :askCode ";
		if(realtime){
			hql += "AND a.itfYn = :itfYn AND to_char(a.acptDate,'yyyy-mm-dd') = :acptDate";
		}else{
			hql += "AND a.itfYn = :itfYn";
		}
		
		Query query = em.createQuery(hql);
		query.setParameter("patientNo", patientNo);
		query.setParameter("pageCd", version);
		query.setParameter("askCode", askCode);
		if(realtime){
			query.setParameter("itfYn", "Y");
			//if(acptDate == null) acptDate = new Date();
			query.setParameter("acptDate", fm.format(acptDate));
		}else{
			query.setParameter("itfYn", "N");
			
		}
		
		//여기는 caching을 하면 안된다. ocs에 업데이트하기 때문이다.
		//query.setHint("org.hibernate.cacheable", true);
		try{
			return (String)query.getSingleResult();
		}catch(NoResultException e){
			return null;
		}catch(NonUniqueResultException ne){
			log.error("===== askCode : " + askCode + ",patientNo:" + patientNo);
			throw ne;
		}
	}

	@Override
	public OCSWebResult loadOcsResult(String id) {
		return em.getReference(OCSWebResult.class, id);
	}

	@Override
	public List<QuestionResult> findResult(User user, CheckupInstance instance,
			Category category) {
		String hql = "FROM QuestionResult a WHERE a.owner = :owner AND a.instacne = :instance AND a.question.parentGroup.category = :category AND a.active = :active ";
		
		hql += "Order by a.createDate asc";
		
		
		Query query = em.createQuery(hql);
		
		query.setParameter("owner", user);
		query.setParameter("category", category);
	
		query.setParameter("instance", instance);
		query.setParameter("active", true);
		query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
	}

	@Override
	public List<NurseCheckResult> findNurseResult(CheckupInstance instance,
			Category category) {
		String hql = "FROM NurseCheckResult a WHERE a.active = :active AND a.question.parentGroup.category = :category " +
				"AND a.instance = :instance ";
		
	
		Query query = em.createQuery(hql);
		
		query.setParameter("active", true);
		query.setParameter("category", category);
		query.setParameter("instance", instance);
	
		query.setHint("org.hibernate.cacheable", true);
		
		List<NurseCheckResult> results = query.getResultList();
		
		return results;
	}

	@Override
	public String getIdOcsResult(CheckupInstance instance,String askCode) {
		String hql = "Select a.id FROM OCSWebResult a WHERE a.instance = :instance AND a.askCode = :askCode ";
		
		
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setParameter("askCode", askCode);
		
		//여기는 caching을 하면 안된다. ocs에 업데이트하기 때문이다.
		//query.setHint("org.hibernate.cacheable", true);
		try{
			return (String)query.getSingleResult();
		}catch(NoResultException e){
			return null;
		}catch(NonUniqueResultException ne){
			log.error("===== askCode : " + askCode + ",patientNo:" + instance.getPatno());
			throw ne;
		}
	}

	@Override
	public OCSWebResult findOcsResult(CheckupInstance instance,String askCode) {
		String hql = "FROM OCSWebResult a WHERE a.instance = :instance AND a.askCode = :askCode";
		
		Query query = em.createQuery(hql);
		query.setParameter("instance", instance);
		query.setParameter("askCode", askCode);

		//여기는 caching을 하면 안된다. ocs에 업데이트하기 때문이다.
		//query.setHint("org.hibernate.cacheable", true);
	
		List<OCSWebResult> results = query.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	
}
