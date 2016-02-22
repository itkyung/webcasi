package com.kbsmc.webcasi.checkup.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.CheckupMasterStatus;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.checkup.ICheckupMasterDAO;
import com.kbsmc.webcasi.common.ClassClone;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;

@Repository
public class CheckupMasterDAO implements ICheckupMasterDAO {
	private Log log = LogFactory.getLog(CheckupMasterDAO.class);
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	@SuppressWarnings(value="unchecked")
	public List<CheckupMaster> searchMaster(CheckupMasterStatus status) {
		try{
			Query query = em.createQuery("FROM CheckupMaster a WHERE a.active = :active AND a.status = :status");
			query.setParameter("active", true);
			query.setParameter("status", status);
			query.setHint("org.hibernate.cacheable", true);
			
			return query.getResultList();
		}catch(Exception e){
			log.error("Find master error : ",e);
		}
		
		return null;
	}

	public Category findUniqueCategory(CheckupMaster master, CategoryType type) {
		try{
			Query query = em.createQuery("FROM Category a WHERE a.active = :active AND a.master = :master AND a.type = :type");
			query.setParameter("active", true);
			query.setParameter("master", master);
			query.setParameter("type", type);
			query.setHint("org.hibernate.cacheable", true);
			
			return (Category)query.getSingleResult();
		}catch(Exception e){
			log.error("Category not found : ",e);
		}
		return null;
	}

	@SuppressWarnings(value="unchecked")
	public List<QuestionGroup> findQuestionGroup(Category category,Gender gender) {
		Query query = em.createQuery("FROM QuestionGroup a WHERE a.active = :active AND a.category = :category " +
				"AND a.gender IN (:gender1,:gender2) Order by a.sortOrder asc");
		query.setParameter("active", true);
		query.setParameter("category", category);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	@SuppressWarnings(value="unchecked")
	public List<Question> findQuestions(QuestionGroup group, int depth,Gender gender) {
		Query query = em.createQuery("FROM Question a WHERE a.active = :active AND a.parentGroup = :group AND a.depth = :depth " +
				"AND a.gender in (:gender1,:gender2) Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("group", group);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public QuestionGroup loadQuestionGroup(String groupId) {
		return em.getReference(QuestionGroup.class, groupId);
	}

	
	public QuestionItem loadQuestionItem(String questionItemId) {
		return em.getReference(QuestionItem.class, questionItemId);
	}

	
	public CheckupMaster loadCheckupMaster(String masterId) {
		return em.getReference(CheckupMaster.class, masterId);
	}
	
	public List<Question> findChildQuestions(String questionItemId,
			Gender gender,int depth) {
		//QuestionItem item = loadQuestionItem(questionItemId);
		
		Query query = em.createQuery("FROM Question a JOIN a.parentItems as c with c = :itemId WHERE a.active = :active AND a.depth = :depth " +
				"AND a.gender in (:gender1,:gender2) Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setParameter("itemId", questionItemId);
		query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
	}
	
	@Override
	public List<Question> findOnlyPreChildQuestions(String questionItemId,
			Gender gender, int depth) {
		Query query = em.createQuery("FROM Question a JOIN a.parentItems as c with c = :itemId WHERE a.active = :active AND a.depth = :depth " +
				"AND a.gender in (:gender1,:gender2) AND a.checkListRequired is null Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setParameter("itemId", questionItemId);
		query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
	}

	public Question loadQuestion(String questionId) {
		Query query = em.createQuery("FROM Question a WHERE a.id = :id");
		query.setParameter("id", questionId);
		query.setHint("org.hibernate.cacheable", true);
		
		return (Question)query.getSingleResult();
		
		//return em.getReference(Question.class, questionId);
	}

	@Override
	public List<Category> findCategory(CheckupMaster master) {
		Query query = em.createQuery("FROM Category a WHERE a.master = :master AND a.active = :active Order by a.sortOrder asc");
		query.setParameter("master", master);
		query.setParameter("active", true);
		query.setHint("org.hibernate.cacheable", true);
		
		return query.getResultList();
	}

	@Override
	public List<CheckupMaster> getMaters() {
		return em.createQuery(
			    "select master from CheckupMaster as master order by master.version desc")
			    .getResultList();
	}

	@Override
	public CheckupMaster loadMaster(String id) {
		return em.find(CheckupMaster.class, id);
	}
	
	@Override
	public int countQuestions(CheckupMaster master,Gender gender, int depth) {
		Query query = em.createQuery("SELECT count(*) FROM Question a WHERE a.parentGroup.master = :master AND a.active = :active AND a.required = :required " +
				"AND a.gender in (:gender1,:gender2) AND a.depth = :depth");
		query.setParameter("active", true);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setParameter("master", master);
		query.setParameter("required", true);
		query.setHint("org.hibernate.cacheable", true);
		
		Number count =  (Number)query.getSingleResult();
		
		return count.intValue();
	}

	@Override
	public int countQuestions(CheckupMaster master,Gender gender, int depth, Category category) {
		Query query = em.createQuery("SELECT count(*) FROM Question a WHERE a.parentGroup.master = :master AND a.active = :active AND a.required = :required " +
				"AND a.parentGroup.category = :category AND a.gender in (:gender1,:gender2) AND a.depth = :depth");
		query.setParameter("active", true);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setParameter("master", master);
		query.setParameter("required", true);
		query.setParameter("category", category);
		query.setHint("org.hibernate.cacheable", true);
		
		Number count =  (Number)query.getSingleResult();
		
		return count.intValue();
	}

	@Override
	public int countQuestions(CheckupMaster master,Gender gender, int depth, QuestionGroup group) {
		Query query = em.createQuery("SELECT count(*) FROM Question a WHERE a.parentGroup.master = :master AND a.active = :active AND a.required = :required " +
				"AND a.parentGroup = :group AND a.gender in (:gender1,:gender2) AND a.depth = :depth");
		query.setParameter("active", true);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setParameter("master", master);
		query.setParameter("required", true);
		query.setParameter("group", group);
		query.setHint("org.hibernate.cacheable", true);
		
		Number count =  (Number)query.getSingleResult();
		
		return count.intValue();
	}

	@Override
	public List<QuestionGroup> findStressTestGroup(CheckupMaster master) {
		Query query = em.createQuery("FROM QuestionGroup a WHERE a.master = :master AND a.active = :active AND a.stressTestGroup = :stressTestGroup");
		query.setParameter("master", master);
		query.setParameter("active", true);
		query.setParameter("stressTestGroup", true);
		query.setHint("org.hibernate.cacheable", true);
		
		List<QuestionGroup> result = query.getResultList();
		return result;
	}

	@Override
	public List<QuestionGroup> findQuestionGroup(Category category,
			Gender gender, boolean skipStress) {
		String hql = "FROM QuestionGroup a WHERE a.active = :active AND a.category = :category " +
				"AND a.gender IN (:gender1,:gender2) ";
		if(skipStress){
			hql += "AND a.stressTestGroup = :stressTestGroup ";
		}
		hql += "Order by a.sortOrder asc";
		
		Query query = em.createQuery(hql);
		
		query.setParameter("active", true);
		query.setParameter("category", category);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		
		if(skipStress){
			query.setParameter("stressTestGroup", !skipStress);
		}
		
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void saveMaster(CheckupMaster checkupMaster) {
		Date now = new Date();
		if(checkupMaster.getId() != null && checkupMaster.getId().length() > 0) {
			CheckupMaster dbMaster = em.find(CheckupMaster.class, checkupMaster.getId());
			dbMaster.setTitle(checkupMaster.getTitle());
			dbMaster.setVersion(checkupMaster.getVersion());
			dbMaster.setActive(checkupMaster.isActive());
			dbMaster.setUpdateDate(now);
			dbMaster.setStatus(checkupMaster.getStatus());
			if(dbMaster.getStatus() == CheckupMasterStatus.COMPLETED) {
				dbMaster.setMaleQuestionCount(this.getMaleQuestionCount(checkupMaster.getId()));
				dbMaster.setFemaleQuestionCount(this.getFemaleQuestionCount(checkupMaster.getId()));
			}
			
			em.merge(dbMaster);
		} else {
			checkupMaster.setId(null);
			checkupMaster.setCreateDate(now);
			checkupMaster.setUpdateDate(now);
			em.persist(checkupMaster);
			
			Category checkList = new Category();
			checkList.setActive(true);
			checkList.setCreateDate(now);
			checkList.setMaster(checkupMaster);
			checkList.setSortOrder(0);
			checkList.setTitle(CategoryType.CHECK_LIST.getLabel());
			checkList.setType(CategoryType.CHECK_LIST);
			checkList.setUpdateDate(now);
			em.persist(checkList);
			
			Category healthAge = new Category();
			healthAge.setActive(true);
			healthAge.setCreateDate(now);
			healthAge.setMaster(checkupMaster);
			healthAge.setSortOrder(1);
			healthAge.setTitle(CategoryType.HEALTH_AGE.getLabel());
			healthAge.setType(CategoryType.HEALTH_AGE);
			healthAge.setUpdateDate(now);		
			em.persist(healthAge);
			
			Category healthCheckup = new Category();
			healthCheckup.setActive(true);
			healthCheckup.setCreateDate(now);
			healthCheckup.setMaster(checkupMaster);
			healthCheckup.setSortOrder(2);
			healthCheckup.setTitle(CategoryType.HEALTH_CHECKUP.getLabel());
			healthCheckup.setType(CategoryType.HEALTH_CHECKUP);
			healthCheckup.setUpdateDate(now);		
			em.persist(healthCheckup);			
			
			Category mentalHealth = new Category();
			mentalHealth.setActive(true);
			mentalHealth.setCreateDate(now);
			mentalHealth.setMaster(checkupMaster);
			mentalHealth.setSortOrder(3);
			mentalHealth.setTitle(CategoryType.MENTAL_HEALTH.getLabel());
			mentalHealth.setType(CategoryType.MENTAL_HEALTH);
			mentalHealth.setUpdateDate(now);		
			em.persist(mentalHealth);			
			
			Category nutrition = new Category();
			nutrition.setActive(true);
			nutrition.setCreateDate(now);
			nutrition.setMaster(checkupMaster);
			nutrition.setSortOrder(4);
			nutrition.setTitle(CategoryType.NUTRITION.getLabel());
			nutrition.setType(CategoryType.NUTRITION);
			nutrition.setUpdateDate(now);		
			em.persist(nutrition);		
			
			Category sleep = new Category();
			sleep.setActive(true);
			sleep.setCreateDate(now);
			sleep.setMaster(checkupMaster);
			sleep.setSortOrder(5);
			sleep.setTitle(CategoryType.SLEEP.getLabel());
			sleep.setType(CategoryType.SLEEP);
			sleep.setUpdateDate(now);		
			em.persist(sleep);		
			
			Category stress = new Category();
			stress.setActive(true);
			stress.setCreateDate(now);
			stress.setMaster(checkupMaster);
			stress.setSortOrder(6);
			stress.setTitle(CategoryType.STRESS.getLabel());
			stress.setType(CategoryType.SLEEP);
			stress.setUpdateDate(now);		
			em.persist(stress);				
		}
	}
	
	@Override
	@Transactional
	public void saveMasterOnly(CheckupMaster checkupMaster) {
		Date now = new Date();
		if(checkupMaster.getId() != null && checkupMaster.getId().length() > 0) {
			CheckupMaster dbMaster = em.find(CheckupMaster.class, checkupMaster.getId());
			dbMaster.setTitle(checkupMaster.getTitle());
			dbMaster.setVersion(checkupMaster.getVersion());
			dbMaster.setActive(checkupMaster.isActive());
			dbMaster.setUpdateDate(now);
			dbMaster.setStatus(checkupMaster.getStatus());
			if(dbMaster.getStatus() == CheckupMasterStatus.COMPLETED) {
				dbMaster.setMaleQuestionCount(this.getMaleQuestionCount(checkupMaster.getId()));
				dbMaster.setFemaleQuestionCount(this.getFemaleQuestionCount(checkupMaster.getId()));
			}
			
			em.merge(dbMaster);
		} else {
			checkupMaster.setId(null);
			checkupMaster.setCreateDate(now);
			checkupMaster.setUpdateDate(now);
			em.persist(checkupMaster);
		}
	}	

	@Override
	@Transactional
	public CheckupMaster activeMaster(String id) {
		CheckupMaster checkupMaster = em.find(CheckupMaster.class, id);
		checkupMaster.setActive(true);
		checkupMaster.setUpdateDate(new Date());
		em.merge(checkupMaster);
		
		return checkupMaster;
	}

	@Override
	@Transactional
	public CheckupMaster inactiveMaster(String id) {
		CheckupMaster master = em.find(CheckupMaster.class, id);
		master.setActive(false);
		master.setUpdateDate(new Date());
		em.merge(master);
		
		return master;
	}

	@Override
	@Transactional
	public int getMaleQuestionCount(String masterId) {
		String queryString = "select count(*) from Question q where q.gender = 'MALE' and q.depth = 1 and q.active = true and q.parentGroup.master.id = :id";
		Query query = em.createQuery(queryString);
		query.setParameter("id", masterId);
		Long count = (Long)query.getSingleResult();
		
		return count.intValue();
	}

	@Override
	@Transactional
	public int getFemaleQuestionCount(String masterId) {
		String queryString = "select count(*) from Question q where q.gender = 'FEMALE' and q.depth = 1 and q.active = true and q.parentGroup.master.id = :id";
		Query query = em.createQuery(queryString);
		query.setParameter("id", masterId);
		Long count = (Long)query.getSingleResult();
		
		return count.intValue();
	}

	@Override
	public int countSameVersion(String id, Integer version) {
		String queryString = null;
		if(id != null && id.length() >0) {
			queryString = "select count(*) from CheckupMaster cm where cm.id != :id and cm.version = :version";
		} else {
			queryString = "select count(*) from CheckupMaster cm where cm.version = :version";
		}
		Query query = em.createQuery(queryString);
		if(id != null && id.length() >0) {
			query.setParameter("id", id);
		}
		query.setParameter("version", version.toString());
		Long count = (Long)query.getSingleResult();
		return count.intValue();
	}
	
	@Override
	public int countActive(String id) {
		String queryString = null;
		if(id != null && id.length() >0) {
			queryString = "select count(*) from CheckupMaster cm where cm.id != :id and cm.status = :status";
		} else {
			queryString = "select count(*) from CheckupMaster cm where cm.status = :status";
		}
		Query query = em.createQuery(queryString);
		if(id != null && id.length() >0) {
			query.setParameter("id", id);
		}
		query.setParameter("status", CheckupMasterStatus.ACTIVE);
		Long count = (Long)query.getSingleResult();
		return count.intValue();
	}	

	@Override
	public List<Question> findQuestions(QuestionGroup group, Gender gender) {
		Query query = em.createQuery("FROM Question a WHERE a.active = :active AND a.parentGroup = :group " +
				"AND a.gender in (:gender1,:gender2) Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("group", group);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	@Override
	public int getMaxVersion() {
		String queryString = "select max(version) from CheckupMaster";

		Query query = em.createQuery(queryString);
		Long count = (Long)query.getSingleResult();
		return count.intValue();
	}

	/**
	 * 검진당일이 아닌 경우에 볼수있는 질문만 리턴한다.
	 */
	@Override
	public List<Question> findOnlyPreQuestions(QuestionGroup group, int depth,
			Gender gender) {
		
		Query query = em.createQuery("FROM Question a WHERE a.active = :active AND a.parentGroup = :group AND a.depth = :depth " +
				"AND a.gender in (:gender1,:gender2) AND a.checkListRequired is null Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("group", group);
		query.setParameter("depth", depth);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		//query.setParameter("checkListRequired", true);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
		
	}

	@Override
	public QuestionGroup findQuestionGroup(CheckupMaster master,
			QuestionGroupType type) {
		Query query = em.createQuery("FROM QuestionGroup a WHERE a.active = :active AND a.master = :master AND a.groupType = :type");
		query.setParameter("active", true);
		query.setParameter("master", master);
		query.setParameter("type", type);
		query.setHint("org.hibernate.cacheable", true);
		
		List<QuestionGroup> groups = query.getResultList();
		
		if(groups.size() > 0){
			return groups.get(0);
		}
		
		return null;
	}

	@Override
	public List<Question> findQuestions(QuestionGroup group) {
		Query query = em.createQuery("FROM Question a WHERE a.active = :active AND a.parentGroup = :group " +
				" Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("group", group);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	@Override
	public List<Question> findQuestions(Category category, Gender gender) {
		Query query = em.createQuery("FROM Question a WHERE a.active = :active AND a.parentGroup.category = :category " +
				"AND a.parentGroup.gender IN (:gender1,:gender2) Order by a.sortOrder asc");
		
		query.setParameter("active", true);
		query.setParameter("category", category);
		query.setParameter("gender1", gender);
		query.setParameter("gender2", Gender.ALL);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}
	
	
}
