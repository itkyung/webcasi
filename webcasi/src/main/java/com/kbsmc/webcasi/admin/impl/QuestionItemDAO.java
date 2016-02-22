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

import com.kbsmc.webcasi.admin.IQuestionItemDAO;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionItem;

@Repository
public class QuestionItemDAO implements IQuestionItemDAO {
	private Log log = LogFactory.getLog(QuestionItemDAO.class);
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	public void createQuestionItem(QuestionItem item) {
		item.setCreateDate(new Date());
		item.setUpdateDate(new Date());
		
		if(item.getSortOrder() == 999) {
			item.setSortOrder(getQuestionItemMax(item.getParentQuestion()));
		}
		
		em.persist(item);
	}

	public void saveQuestionItem(QuestionItem item) {
		item.setUpdateDate(new Date());
		em.merge(item);
	}

	public QuestionItem loadQuestionItem(String id) {
		//return em.getReference(QuestionItem.class, id);
		
		String hql = "FROM QuestionItem a WHERE a.id = :id ";
		Query query = em.createQuery(hql);
		query.setParameter("id", id);
		return (QuestionItem)query.getSingleResult();
	}
	
	@SuppressWarnings(value="unchecked")
	public List<QuestionItem> findQuestionItem(Question question) {
		String hql = "FROM QuestionItem a WHERE a.active = :active AND a.parentQuestion = :parentQuestion order by sortOrder ";
		
		Query query = em.createQuery(hql);
		query.setParameter("active", true);
		query.setParameter("parentQuestion", question);
		
		return query.getResultList();
	}
	
	@SuppressWarnings(value="unchecked")
	private int getQuestionItemMax(Question question) {
		String hql = "Select nvl(max(sortOrder),-1)+1 as sortOrder FROM QuestionItem a WHERE a.parentQuestion = :parentQuestion";
		
		Query query = em.createQuery(hql);
		query.setParameter("parentQuestion", question);
		
		Number seq =  (Number)query.getSingleResult();
		return seq.intValue();
	}
	
}
