package com.kbsmc.webcasi.admin.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.admin.IAdminHomeService;
import com.kbsmc.webcasi.admin.IHelpContentsDAO;
import com.kbsmc.webcasi.admin.IQuestionDAO;
import com.kbsmc.webcasi.admin.IQuestionGroupDAO;
import com.kbsmc.webcasi.admin.IQuestionItemDAO;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.HelpContents;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;

@Service("AdminHomeService")
public class AdminHomeService implements IAdminHomeService {
	private Log log = LogFactory.getLog(AdminHomeService.class);
	
	@Autowired private IQuestionGroupDAO groupDao;
	@Autowired private IHelpContentsDAO helpDao;
	@Autowired private IQuestionDAO questionDao;
	@Autowired private IQuestionItemDAO itemDao;
	
	@Override
	public List<QuestionGroup> findQuestionGroup(Category category, CheckupMaster master) {
		return groupDao.findQuestionGroup(category, master);
	}

	@Override
	@Transactional
	public void createQuestionGroup(QuestionGroup group) {
		groupDao.createQuestionGroup(group);
	}
	
	@Override
	@Transactional
	public void saveQuestionGroup(QuestionGroup group) {
		groupDao.saveQuestionGroup(group);
	}
	
	@Override
	public QuestionGroup loadQuestionGroup(String id) {
		return groupDao.loadQuestionGroup(id);
	}
	
	@Override
	public List<Question> findQuestion(QuestionGroup group, Question question, String itemId, int depth) {
		return questionDao.findQuestion(group, question, itemId, depth);
	}
	
	@Override
	@Transactional
	public void createQuestion(Question question) {
		questionDao.createQuestion(question);
	}
	
	@Override
	@Transactional
	public void saveQuestion(Question question) {
		questionDao.saveQuestion(question);
	}
	
	@Override
	public Question loadQuestion(String id) {
		return questionDao.loadQuestion(id);
	}
	
	@Override
	public List<QuestionItem> findQuestionItem(Question question) {
		return itemDao.findQuestionItem(question);
	}
	
	@Override
	@Transactional
	public void createQuestionItem(QuestionItem item) {
		itemDao.createQuestionItem(item);
	}
	
	@Override
	@Transactional
	public void saveQuestionItem(QuestionItem item) {
		itemDao.saveQuestionItem(item);
	}
	
	@Override
	public QuestionItem loadQuestionItem(String id) {
		return itemDao.loadQuestionItem(id);
	}
	
	@Override
	@Transactional
	public void createHelpContents(HelpContents help) {
		helpDao.createHelpContents(help);
	}
	
	@Override
	@Transactional
	public void saveHelpContents(HelpContents help) {
		helpDao.saveHelpContents(help);
	}
	
	@Override
	public HelpContents loadHelpContents(String id) {
		return helpDao.loadHelpContents(id);
	}
	
	@Override
	public int findParentItemCount(String itemId) {
		return questionDao.findParentItemCount(itemId);
	}
	
}
