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

import com.kbsmc.webcasi.admin.IQuestionDAO;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;

@Repository
public class QuestionDAO implements IQuestionDAO {
	private Log log = LogFactory.getLog(QuestionDAO.class);
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	public void createQuestion(Question question) {
		question.setCreateDate(new Date());
		question.setUpdateDate(new Date());
		
		if(question.getSortOrder() == 999) {
			question.setSortOrder(getQuestionMax(question.getParentGroup()));
		}
		
		em.persist(question);
	}

	public void saveQuestion(Question question) {
		question.setUpdateDate(new Date());
		em.merge(question);
	}

	public Question loadQuestion(String id) {
		//return em.getReference(Question.class, id);
		
		String hql = "FROM Question a WHERE a.id = :id ";
		Query query = em.createQuery(hql);
		query.setParameter("id", id);
		return (Question)query.getSingleResult();
	}
	
	@SuppressWarnings(value="unchecked")
	public List<Question> findQuestion(QuestionGroup group, Question question, String itemId, int depth) {
		String hql = "FROM Question a ";
		
		if(CommonUtils.isValid(itemId)) {
			hql = hql + " JOIN a.parentItems as c with c = :itemId ";
		}

		hql = hql + " WHERE a.active = :active AND a.parentGroup = :parentGroup AND a.depth = :depth ";
		
		if(question != null) {
			hql = hql + " AND a.parentQuestion = :parentQuestion ";
		}
		
		hql = hql + "  order by a.sortOrder ";
		
		Query query = em.createQuery(hql);
		if(CommonUtils.isValid(itemId)) {
			query.setParameter("itemId", itemId);
		}
		query.setParameter("active", true);
		query.setParameter("parentGroup", group);
		query.setParameter("depth", depth);
		if(question != null) {
			query.setParameter("parentQuestion", question);
		}
		
		return query.getResultList();
	}
	
	@SuppressWarnings(value="unchecked")
	private int getQuestionMax(QuestionGroup group) {
		String hql = "Select nvl(max(sortOrder),-1)+1 as sortOrder FROM Question a WHERE a.parentGroup = :parentGroup ";
				
		Query query = em.createQuery(hql);
		query.setParameter("parentGroup", group);
		
		Number seq =  (Number)query.getSingleResult();
		return seq.intValue();
	}
	
	@SuppressWarnings(value="unchecked")
	public int findParentItemCount(String itemId) {
		String hql = "Select count(id) FROM Question a JOIN a.parentItems as c with c = :itemId WHERE a.active = :active ";
		
		Query query = em.createQuery(hql);
		query.setParameter("itemId", itemId);
		query.setParameter("active", true);
		
		Number seq =  (Number)query.getSingleResult();
		return seq.intValue();
	}
}
