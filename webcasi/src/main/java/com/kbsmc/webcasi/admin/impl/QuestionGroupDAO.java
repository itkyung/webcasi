package com.kbsmc.webcasi.admin.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.kbsmc.webcasi.admin.IQuestionGroupDAO;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.QuestionGroup;

@Repository
public class QuestionGroupDAO implements IQuestionGroupDAO {
	private Log log = LogFactory.getLog(QuestionGroup.class);
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	public void createQuestionGroup(QuestionGroup group) {
		group.setCreateDate(new Date());
		group.setUpdateDate(new Date());
		
		if(group.getSortOrder() == 999) {
			group.setSortOrder(getQuestionGroupMax(group.getCategory(),group.getMaster()));
		}
		
		em.persist(group);
	}

	public void saveQuestionGroup(QuestionGroup group) {
		group.setUpdateDate(new Date());
		em.merge(group);
	}

	public QuestionGroup loadQuestionGroup(String id) {
		//return em.getReference(QuestionGroup.class, id);
		
		String hql = "FROM QuestionGroup a WHERE a.id = :id ";
		Query query = em.createQuery(hql);
		query.setParameter("id", id);
		return (QuestionGroup)query.getSingleResult();
		
	}
	
	@SuppressWarnings(value="unchecked")
	public List<QuestionGroup> findQuestionGroup(Category category, CheckupMaster master) {
		String hql = "FROM QuestionGroup a WHERE a.active = :active AND a.category = :category AND a.master = :master order by sort_order ";
		
		Query query = em.createQuery(hql);
		query.setParameter("active", true);
		query.setParameter("category", category);
		query.setParameter("master", master);
		
		return query.getResultList();
	}
	
	@SuppressWarnings(value="unchecked")
	private int getQuestionGroupMax(Category category, CheckupMaster master) {
		String hql = "Select nvl(max(sortOrder),-1)+1 as sortOrder FROM QuestionGroup a WHERE a.category = :category AND a.master = :master";
		
		Query query = em.createQuery(hql);
		query.setParameter("category", category);
		query.setParameter("master", master);
		
		Number seq =  (Number)query.getSingleResult();
		return seq.intValue();
	}
	
}
