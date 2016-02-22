package com.kbsmc.webcasi.admin.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.kbsmc.webcasi.admin.IHelpContentsDAO;
import com.kbsmc.webcasi.entity.HelpContents;

@Repository
public class HelpContentsDAO implements IHelpContentsDAO {
	private Log log = LogFactory.getLog(HelpContents.class);
	
	@PersistenceContext(type=PersistenceContextType.TRANSACTION)
	private EntityManager em;
	
	public void createHelpContents(HelpContents help) {
		help.setCreateDate(new Date());
		help.setUpdatedDate(new Date());
		em.persist(help);
	}

	public void saveHelpContents(HelpContents help) {
		help.setUpdatedDate(new Date());
		em.merge(help);
	}

	public HelpContents loadHelpContents(String id) {
		return em.getReference(HelpContents.class, id);
	}
}
