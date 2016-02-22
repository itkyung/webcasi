package com.kbsmc.webcasi.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kbsmc.webcasi.CategoryType;
import com.kbsmc.webcasi.CheckupMasterStatus;
import com.kbsmc.webcasi.Gender;
import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionGroupType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.ValidatorType;
import com.kbsmc.webcasi.WebPath;
import com.kbsmc.webcasi.admin.IAdminHomeService;
import com.kbsmc.webcasi.admin.impl.JsonObjectVO;
import com.kbsmc.webcasi.checkup.ICheckupInstanceService;
import com.kbsmc.webcasi.checkup.ICheckupMasterService;
import com.kbsmc.webcasi.checkup.IOCSInterfaceService;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.common.IJSONFactory;
import com.kbsmc.webcasi.common.MIMEUtil;
import com.kbsmc.webcasi.common.ViewUtils;
import com.kbsmc.webcasi.entity.Category;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.entity.CheckupMaster;
import com.kbsmc.webcasi.entity.HelpContents;
import com.kbsmc.webcasi.entity.Question;
import com.kbsmc.webcasi.entity.QuestionGroup;
import com.kbsmc.webcasi.entity.QuestionItem;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.job.IStatusChangeJob;


@Controller
@RequestMapping(value=WebPath.ADMIN_PATH)
public class AdminHomeController {
	private static final Logger logger = LoggerFactory.getLogger(AdminHomeController.class);
	
	@Autowired private ICheckupMasterService service;
	@Autowired private ICheckupInstanceService inService;
	@Autowired private IAdminHomeService adminService;
	@Autowired private IUserService userService;
	@Autowired private IStatusChangeJob jobService;
	@Autowired private IOCSInterfaceService ocsInterfaceService;
	
	@Autowired private IJSONFactory jsonFactory;
	
	private String fileUploadPath;
	
	private Log log = LogFactory.getLog(AdminHomeController.class);
	
	@Value("#{defaultConfig['common.uploadPath']}")
	public void setFileUploadPath(String fileUploadPath) {
		this.fileUploadPath = fileUploadPath;
	}
	
	@RequestMapping(value="home",method=RequestMethod.GET)
	public String home(ModelMap map){
		//어드민 메인
		return "admin/home";
	}
	  
	private String fileUpload(MultipartFile attachFile) throws Exception {
		String fileName = "";
		try {
			if (attachFile != null && !attachFile.isEmpty()) {
				File fileDir = new File(fileUploadPath);
				if(!fileDir.exists()) {
					fileDir.mkdirs();
				}
				fileName = System.currentTimeMillis()  + "_" + attachFile.getOriginalFilename();
				File file = new File(fileUploadPath,fileName);
				attachFile.transferTo(file);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	 * 질문그룹 화면.
	 * @param  categoryType, masterId
	 * @return ModelMap
	 * @throws Exception
	 */
	@RequestMapping(value="/questionGroupList")
	public String questionGroupList(@RequestParam(value="categoryType", required = false) String categoryType, @RequestParam("masterId") String masterId ,ModelMap map) throws Exception {
		CheckupMaster master = service.loadCheckupMaster(masterId);
		CategoryType type = null;
		if(CommonUtils.isEmpty(categoryType)) {
			type = CategoryType.CHECK_LIST;
		} else {
			type = CategoryType.valueOf(categoryType);
		}
		Category category = service.findUniqueCategory(master, type);
		
		//List<QuestionGroup> questionGroups = adminService.findQuestionGroup(category, master);
		
		map.addAttribute("genders", Gender.values());
		map.addAttribute("master", master);
		map.addAttribute("categoryType", category.getType());
		map.addAttribute("categoryTypes", CategoryType.values());
		map.addAttribute("questionGroupTypes", QuestionGroupType.values());
		
		//map.addAttribute("questionGroups", questionGroups);
		
		return "admin/questionGroupList";
	}
	
	/**
	 * 질문그룹의 grid 목록
	 * @param categoryType, masterId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getGroupListData",method=RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getGroupListData(@RequestParam("categoryType") String categoryType, @RequestParam("masterId") String masterId) throws Exception {
		CheckupMaster master = service.loadCheckupMaster(masterId);
		CategoryType type = null;
		if(CommonUtils.isEmpty(categoryType)) {
			type = CategoryType.CHECK_LIST;
		} else {
			type = CategoryType.valueOf(categoryType);
		}
		Category category = service.findUniqueCategory(master, type);

		List<QuestionGroup> list = adminService.findQuestionGroup(category, master);
		JsonObjectVO json = new JsonObjectVO(1, list.size(), 1, list.toArray());
		return CommonUtils.toJson(json);
	}
	
	@RequestMapping(value="/saveQuestionGroup",method=RequestMethod.POST)
	@ResponseBody
	public String saveQuestionGroup(@ModelAttribute("questionGroup") QuestionGroup questionGroup,@ModelAttribute("helpContents") HelpContents helpContents,
			@RequestParam("categoryTp") String categoryType, @RequestParam("masterId") String masterId, @RequestParam("groupId") String groupId,
			@RequestParam("helpId") String helpId, @RequestParam(value="attachFile",required=false) MultipartFile attachFile, BindingResult result) throws Exception {
		
		QuestionGroup group = new QuestionGroup();
		CheckupMaster ckMaster = new CheckupMaster();
		Category cg = new Category();
		HelpContents help = new HelpContents();
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		
		try{
			if(CommonUtils.isValid(helpId)) {
				help = adminService.loadHelpContents(helpId);
			}
			
			help.setAttachFilePath(helpContents.getAttachFilePath());
			help.setContents(helpContents.getContents());
			
			String fileName = fileUpload(attachFile);
			if(CommonUtils.isValid(fileName)) {
				help.setAttachFilePath(fileName);
				help.setFileMimeType(MIMEUtil.getMimeType(fileName));
			}
			if(CommonUtils.isValid(helpId)) {
				adminService.saveHelpContents(help);
			} else {
				adminService.createHelpContents(help);
			}
			if(CommonUtils.isValid(helpContents.getContents())) {
				questionGroup.setDescription(getText(helpContents.getContents()));
			} else {
				questionGroup.setDescription("");
			}
			
			if(CommonUtils.isValid(groupId)) {
				group =  adminService.loadQuestionGroup(groupId);
				
				group.setGroupNo(questionGroup.getGroupNo());
				group.setGroupType(questionGroup.getGroupType());
				group.setTitle(questionGroup.getTitle());
				group.setDescription(questionGroup.getDescription());
				group.setGender(questionGroup.getGender());
				group.setThumbnailImage(questionGroup.getThumbnailImage());
				group.setNurseEditable(questionGroup.getNurseEditable());
				group.setHelp(help);
				
				group.setSortOrder(questionGroup.getSortOrder());
				
				adminService.saveQuestionGroup(group);
			} else {
				ckMaster = service.loadCheckupMaster(masterId);
				cg = service.findUniqueCategory(ckMaster, CategoryType.valueOf(categoryType));
				questionGroup.setActive(true);
				questionGroup.setMaster(ckMaster);
				questionGroup.setCategory(cg);
				questionGroup.setHelp(help);
				adminService.createQuestionGroup(questionGroup);
			}
			map.put("success", "true");
		}catch(Exception e){
			logger.error("saveQuestionGroup : ",e);
			map.put("msg", e.getMessage());
			map.put("success", "false");
		}
		
		String json = CommonUtils.toJson(map);
		return json;
	}
	
	/**
	 * 질문그룹삭제.
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/questionGroupDelete")
	@ResponseBody
	public String questionGroupDelete(@RequestParam("groupId") String groupId) throws Exception {
		QuestionGroup group = adminService.loadQuestionGroup(groupId);
//		group.setActive(!group.isActive());
		group.setActive(false);
		adminService.saveQuestionGroup(group);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		return CommonUtils.toJson(map);
	}
	
	/**
	 * 질문그룹 조회.
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getQuestionGroup", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getQuestionGroup(@RequestParam("groupId") String groupId) throws Exception {
		QuestionGroup group = adminService.loadQuestionGroup(groupId);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		map.put("group", group);
		return CommonUtils.toJson(map);
	}

	/**
	 * 질문 화면.
	 * @param  groupId, depth
	 * @return ModelMap
	 * @throws Exception
	 */
	@RequestMapping(value="/questionList")
	public String questionList(@RequestParam(value="groupId") String groupId, @RequestParam(value="questionId", required = false) String questionId,
			@RequestParam(value="itemId", required = false) String itemId, @RequestParam("depth") String depth,
			@RequestParam(value="searchItemId", required = false) String searchItemId,
			@RequestParam(value="searchQuestionId", required = false) String searchQuestionId, ModelMap map) throws Exception {
		
		String title = "";
		String itemTitle = "";
		if(CommonUtils.isValid(groupId)) {
			QuestionGroup group = adminService.loadQuestionGroup(groupId);
			title = title + group.getTitle();
			map.addAttribute("searchMasterId", group.getMaster().getId());
			map.addAttribute("searchCategoryType", group.getCategory().getType());
		}
		if(CommonUtils.isValid(questionId)) {
			Question question = adminService.loadQuestion(questionId);
			title = question.getTitle();
		}
		if(CommonUtils.isValid(itemId)) {
			QuestionItem item = adminService.loadQuestionItem(itemId);
			title = item.getParentQuestion().getTitle();
			itemTitle = " " +item.getTitle();
			map.addAttribute("items", item.getParentQuestion().getChildItems());
		}
		
		map.addAttribute("title", title);
		map.addAttribute("itemTitle", itemTitle);
		map.addAttribute("groupId", groupId);
		map.addAttribute("questionId", questionId);
		map.addAttribute("itemId", itemId);

		map.addAttribute("searchQuestionId", searchQuestionId);
		map.addAttribute("searchItemId", searchItemId);
		map.addAttribute("depth", depth);
		map.addAttribute("genders", Gender.values());
		map.addAttribute("questionTypes", QuestionType.values());
		map.addAttribute("validatorTypes", ValidatorType.values());
		map.addAttribute("nutritionItemTypes", NutritionItemType.values());
		
		return "admin/questionList";
	}
	
	/**
	 * 질문의 grid 목록
	 * @param groupId, depth
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getQuestionListData",method=RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getQuestionListData(@RequestParam("groupId") String groupId, @RequestParam(value="questionId", required = false) String questionId,
			@RequestParam(value="itemId", required = false) String itemId, @RequestParam("depth") String depth) throws Exception {
		QuestionGroup group = adminService.loadQuestionGroup(groupId);
		
		Question question = null;
		if(CommonUtils.isValid(questionId)) {
			question = adminService.loadQuestion(questionId);
		}
		
		List<Question> list = adminService.findQuestion(group, question, itemId, Integer.parseInt(depth));
		JsonObjectVO json = new JsonObjectVO(1, list.size(), 1, list.toArray());
		return CommonUtils.toJson(json);
	}

	private String getText(String content) {      
	    Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>",Pattern.DOTALL);      
	    Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>",Pattern.DOTALL);      
	    Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");      
	    Pattern nTAGS = Pattern.compile("<<A href=\"file://\\w+\\s+[^<]*\\s\">\\w+\\s+[^<]*\\s</A>*>");      
	    Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");      
	    Pattern WHITESPACE = Pattern.compile("<A href=\"file://\\s\\s\">\\s\\s</A>+");
	          
	    Matcher m;      
	          
	    m = SCRIPTS.matcher(content);      
	    content = m.replaceAll("");      
	    m = STYLE.matcher(content);      
	    content = m.replaceAll("");      
	    m = TAGS.matcher(content);      
	    content = m.replaceAll("");      
	    m = ENTITY_REFS.matcher(content);      
	    content = m.replaceAll("");      
	    m = WHITESPACE.matcher(content);      
	    content = m.replaceAll(" ");              
	          
	    return content;      
	}
	
	@RequestMapping(value="/saveQuestion",method=RequestMethod.POST)
	@ResponseBody
	public String saveQuestion(@ModelAttribute("question") Question question,@ModelAttribute("helpContents") HelpContents helpContents,
			@RequestParam("parentGroupId") String parentGroupId, @RequestParam("parentQuestionId") String parentQuestionId,
			@RequestParam("parentItemId") String parentItemId, @RequestParam("questionId") String questionId,
			@RequestParam("helpId") String helpId, @RequestParam(value="attachFile",required=false) MultipartFile attachFile, BindingResult result) throws Exception {
		
		Question question1 = new Question();
		QuestionGroup parentGroup = new QuestionGroup();
		Question parentQuestion = new Question();
		HelpContents help = new HelpContents();
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		
		try{
			if(CommonUtils.isValid(helpId)) {
				help = adminService.loadHelpContents(helpId);
			}
			
			//help.setAttachFilePath(helpContents.getAttachFilePath());
			help.setContents(helpContents.getContents());
			
			String fileName = fileUpload(attachFile);
			if(CommonUtils.isValid(fileName)) {
				help.setAttachFilePath(fileName);
				help.setFileMimeType(MIMEUtil.getMimeType(fileName));
			}
			if(CommonUtils.isValid(helpId)) {
				adminService.saveHelpContents(help);
			} else {
				adminService.createHelpContents(help);
			}
			if(CommonUtils.isValid(helpContents.getContents())) {
				//helpContents.getContents().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","")
				question.setDescription(getText(helpContents.getContents()));
			} else {
				question.setDescription("");
			}
			
			if(CommonUtils.isValid(questionId)) {
				question1 =  adminService.loadQuestion(questionId);
				
				//상위 답변에 하위에 추가질문이 존재하는지 여부설정을 위해 추가/삭제 된답변 정리.
				HashMap<String,String> oldMap = new HashMap<String, String>();
				if(question1.getParentItems() != null) {
					for(String parentItem : question1.getParentItems()) {
						oldMap.put(parentItem, parentItem);
					}
				}
				
				HashMap<String,String> newMap = new HashMap<String, String>();
				if(question.getParentItems() != null) {
					for(String parentItem : question.getParentItems()) {
						if(oldMap.get(parentItem) == null) {
							newMap.put(parentItem, parentItem);
						} else {
							oldMap.remove(parentItem);
						}
					}
				}
				
				question1.setType(question.getType());
				question1.setQuestionNo(question.getQuestionNo());
				question1.setOcsAskCode(question.getOcsAskCode());
				question1.setOcsNoAnswerCode(question.getOcsNoAnswerCode());
				question1.setOcsAskCode2(question.getOcsAskCode2());
				question1.setOcsNoAnswerCode2(question.getOcsNoAnswerCode2());
				question1.setOcsAskCode3(question.getOcsAskCode3());
				question1.setOcsNoAnswerCode3(question.getOcsNoAnswerCode3());
				question1.setTitle(question.getTitle());
				question1.setGender(question.getGender());
				question1.setValidator(question.getValidator());
				question1.setDescription(question.getDescription());
				question1.setPreText(question.getPreText());
				question1.setPostText(question.getPostText());
				question1.setRequired(question.isRequired());
				question1.setNavigationStressFlag(question.isNavigationStressFlag());
				question1.setNavigationNutritionFlag(question.isNavigationNutritionFlag());
				question1.setParentItems(question.getParentItems());
				question1.setCheckListRequired(question.getCheckListRequired());
				question1.setNurseQuestionType(question.getNurseQuestionType());
				question1.setChildNurseItems(question.getChildNurseItems());
				question1.setNurseOcsAnswer(question.getNurseOcsAnswer());
				question1.setNurseOcsAskCode(question.getNurseOcsAskCode());
				question1.setNurseOcsNoAnswerCode(question.getNurseOcsNoAnswerCode());
				question1.setHelp(help);
				question1.setMinRange(question.getMinRange());
				question1.setMaxRange(question.getMaxRange());
				
				question1.setSortOrder(question.getSortOrder());
				
				adminService.saveQuestion(question1);
				
				//삭제시 상위 답변에 하위에 추가질문이 존재하는지 여부설정.
				Iterator<String> delKeys = oldMap.keySet().iterator();
				while (delKeys.hasNext()) {
					String delKey = delKeys.next();
					
					int count = adminService.findParentItemCount(delKey);
					if(count == 0) {
						QuestionItem it = adminService.loadQuestionItem(delKey);
						if(it.isExistChildQuestion()) {
							it.setExistChildQuestion(false);
							adminService.saveQuestionItem(it);
						}
					}
				}
				//추가시 상위 답변에 하위에 추가질문이 존재하는지 여부설정.
				Iterator<String> newKeys = newMap.keySet().iterator();
				while (newKeys.hasNext()) {
					String newKey = newKeys.next();
					QuestionItem it = adminService.loadQuestionItem(newKey);
					if(!it.isExistChildQuestion()) {
						it.setExistChildQuestion(true);
						adminService.saveQuestionItem(it);
					}
				}	
			} else {
				parentGroup = adminService.loadQuestionGroup(parentGroupId);
				if(CommonUtils.isValid(parentQuestionId)) {
					parentQuestion = adminService.loadQuestion(parentQuestionId);
					question.setParentQuestion(parentQuestion);
				}
				
				question.setActive(true);
				question.setParentGroup(parentGroup);
				question.setHelp(help);
				adminService.createQuestion(question);
				//상위 답변에 하위에 추가질문이 존재하는지 여부설정.
				if(question.getParentItems() != null) {
					for(String parentItem : question.getParentItems()) {
						QuestionItem it = adminService.loadQuestionItem(parentItem);
						if(!it.isExistChildQuestion()) {
							it.setExistChildQuestion(true);
							adminService.saveQuestionItem(it);
						}
					}
				}
			}
			map.put("success", "true");
		}catch(Exception e){
			logger.error("saveQuestion : ",e);
			map.put("msg", e.getMessage());
			map.put("success", "false");
		}
		
		String json = CommonUtils.toJson(map);
		return json;
	}
	
	/**
	 * 질문삭제.
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/questionDelete")
	@ResponseBody
	public String questionDelete(@RequestParam("questionId") String questionId) throws Exception {
		Question question = adminService.loadQuestion(questionId);
//		question.setActive(!question.isActive());
		question.setActive(false);
		adminService.saveQuestion(question);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		return CommonUtils.toJson(map);
	}
	
	/**
	 * 질문 조회.
	 * @param questionId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getQuestion", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getQuestion(@RequestParam("questionId") String questionId) throws Exception {
		Question question = adminService.loadQuestion(questionId);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		map.put("question", question);
		return CommonUtils.toJson(map);
	}
	
	/**
	 * 답변의 grid 목록
	 * @param groupId, masterId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getItemListData",method=RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getItemListData(@RequestParam("questionId") String questionId) throws Exception {
		Question question = adminService.loadQuestion(questionId);
		List<QuestionItem> list = adminService.findQuestionItem(question);
		JsonObjectVO json = new JsonObjectVO(1, list.size(), 1, list.toArray());
		return CommonUtils.toJson(json);
	}
	
	/**
	 * 답변 저장
	 * @param groupId, masterId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/saveQuestionItem",method=RequestMethod.POST)
	@ResponseBody
	public String saveQuestionItem(@ModelAttribute("question") QuestionItem questionItem, 
			@RequestParam("parentQuestionId") String parentQuestionId,
			@RequestParam("itemId") String itemId, BindingResult result) throws Exception {
		
		QuestionItem item = new QuestionItem();
		Question parentQuestion = new Question();
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		
		try{
			if(CommonUtils.isValid(itemId)) {
				item =  adminService.loadQuestionItem(itemId);
				item.setType(questionItem.getType());
				item.setOcsAnswer(questionItem.getOcsAnswer());
				item.setTitle(questionItem.getTitle());
				item.setOcsAskCode(questionItem.getOcsAskCode());
				item.setOcsNoAnswerCode(questionItem.getOcsNoAnswerCode());
				item.setOcsAskCode2(questionItem.getOcsAskCode2());
				item.setOcsNoAnswerCode2(questionItem.getOcsNoAnswerCode2());
				item.setOcsAskCode3(questionItem.getOcsAskCode3());
				item.setOcsNoAnswerCode3(questionItem.getOcsNoAnswerCode3());
				item.setPreText(questionItem.getPreText());
				item.setPostText(questionItem.getPostText());
				item.setOcsAskCode2(questionItem.getOcsAskCode2());
				item.setPreText2(questionItem.getPreText2());
				item.setPostText2(questionItem.getPostText2());
				item.setValidator(questionItem.getValidator());
				item.setThumnailImage(questionItem.getThumnailImage());
				item.setItemGroup(questionItem.getItemGroup());
				item.setNoneFlag(questionItem.getNoneFlag());
				item.setChildItems(questionItem.getChildItems());
				item.setMinRange(questionItem.getMinRange());
				item.setMaxRange(questionItem.getMaxRange());
				
				item.setSortOrder(questionItem.getSortOrder());
				
				adminService.saveQuestionItem(item);
			} else {
				parentQuestion = adminService.loadQuestion(parentQuestionId);
				questionItem.setParentQuestion(parentQuestion);
				questionItem.setActive(true);
				adminService.createQuestionItem(questionItem);
			}
			map.put("success", "true");
		}catch(Exception e){
			logger.error("saveQuestionItem : ",e);
			map.put("msg", e.getMessage());
			map.put("success", "false");
		}
		
		String json = CommonUtils.toJson(map);
		return json;
	}
	/**
	 * 답변 삭제.
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/questionItemDelete")
	@ResponseBody
	public String questionItemDelete(@RequestParam("itemId") String itemId) throws Exception {
		QuestionItem item = adminService.loadQuestionItem(itemId);
//		item.setActive(!item.isActive());
		item.setActive(false);
		adminService.saveQuestionItem(item);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		return CommonUtils.toJson(map);
	}
	
	/**
	 * 답변 조회.
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getQuestionItem", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getQuestionItem(@RequestParam("itemId") String itemId) throws Exception {
		QuestionItem item = adminService.loadQuestionItem(itemId);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		map.put("item", item);
		return CommonUtils.toJson(map);
	}
	
	@RequestMapping(value="goMasterList",method=RequestMethod.GET)
	public String toMaterList(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			ModelMap map){
		map.addAttribute("statuses", CheckupMasterStatus.values());
		return "admin/masterList";
	}
	
	@RequestMapping(value="getMasterList",method=RequestMethod.GET)
	public void getMasterList(
			HttpServletResponse httpServletResponse) throws IOException{
		ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), service.getMaters());
	}
	
	@RequestMapping(value="saveMaster",method=RequestMethod.POST)
	public void saveMaster(
			@ModelAttribute("master") CheckupMaster checkupMaster,
			HttpServletResponse httpServletResponse) throws IOException{
		if(checkupMaster.getStatus().equals(CheckupMasterStatus.ACTIVE) && service.isExistActive(checkupMaster.getId())) {
			ViewUtils.jsonFailure(httpServletResponse, new Exception("상태가 ACTIVE인 문진이 존재 합니다."));
		}
		if(service.isExistSameVersion(checkupMaster.getId(), new Integer(checkupMaster.getVersion()))) {
			ViewUtils.jsonFailure(httpServletResponse, new Exception("같은 버젼이 존재합니다."));
		} else {
			service.saveMaster(checkupMaster);
			ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), checkupMaster);
		}
	}
	
	@RequestMapping(value="cloneMaster",method=RequestMethod.GET)
	public void cloneMaster(@RequestParam(value="id", required=true) String id, ModelMap map) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		service.cloneMaster(id);
	} 
	
	@RequestMapping(value="activeMaster",method=RequestMethod.POST)
	public void activeMaster(
			@RequestParam("id") String id,
			HttpServletResponse httpServletResponse) throws IOException {
		CheckupMaster master = service.activeMaster(id);
		ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), master);
	}

	@RequestMapping(value="inactiveMaster",method=RequestMethod.POST)
	public void inactiveMaster(
			@RequestParam("id") String id,
			HttpServletResponse httpServletResponse) throws IOException {
		CheckupMaster master = service.inactiveMaster(id);
		ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), master);
	}
	
	@RequestMapping(value="goNurseAccountList",method=RequestMethod.GET)
	public String goNurseAccountList(ModelMap map){
		return "admin/nurseAccountList";
	}	
	
	@RequestMapping(value="goPatientAccountList",method=RequestMethod.GET)
	public String goPatientAccountList(ModelMap map){
		return "admin/patientAccountList";
	}	
	
	@RequestMapping(value="getNurseAccountList",method=RequestMethod.GET)
	public void getNurseAccountList(
			HttpServletResponse httpServletResponse) throws IOException{
		ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), userService.getNurses());
	}	
	
	@RequestMapping(value="getPatientAccountList",method=RequestMethod.GET)
	public void getPatientAccountList(
			@RequestParam(value="loginId", required=false) String loginId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="patientNo", required=false) String patientNo,
			HttpServletResponse httpServletResponse) throws IOException{
		
		ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), userService.getPatients(loginId, name, patientNo));
	}
	
	@RequestMapping(value="/previewQuestionGroup/{questionGroupId}",method=RequestMethod.GET)
	public String previewQuestionGroup(
			@PathVariable("questionGroupId") String questionGroupId,ModelMap map) throws IOException{
		
		QuestionGroup group = service.loadQuestionGroup(questionGroupId);
		map.put("questionGroup",group);
		
		Category category = group.getCategory();
		List<QuestionGroup> groups = service.findQuestionGroup(category, Gender.ALL,false,false);
		map.put("questionGroups",groups);
		
		List<Question> questions = service.findQuestions(group, 1, Gender.ALL);
		map.put("questions",questions);
		
		
		String template = group.getGroupType().getTemplate();
		//HACK 식생활 습관만을 위한 유형.
		if(group.getGroupType().equals(QuestionGroupType.NUTRITION_3)){
			//현재 그룹의 2단계 질문의 결과값도 같이 가져와서 itemGroup으로 따로 만든다. 
		
		}
		map.put("previewFlag",true);
		map.put("nurseViewFlag", false);
		map.put("nurseEditable", false);
		
		return template;
	}
	
	/**
	 * 임시로 copyQuestionId를 받아서 하위 질문을 copy한다.
	 * @param parentQuestionId, copyQuestionId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/questionItemCopy")
	@ResponseBody
	public String questionItemCopy(@RequestParam("parentQuestionId") String parentQuestionId, @RequestParam("copyQuestionId") String copyQuestionId) throws Exception {
		
		Question parentQuestion = adminService.loadQuestion(parentQuestionId);
		Question copyQuestion = adminService.loadQuestion(copyQuestionId);
		
		// 항목 존재하면 지우기.
		for(QuestionItem delItem : parentQuestion.getChildItems()) {
			delItem.setActive(false);
			adminService.saveQuestionItem(delItem);
		}
		
		// 항목 복사하기.
		for(QuestionItem questionItem : copyQuestion.getChildItems()) {
			
			QuestionItem item = new QuestionItem();
			
			item.setType(questionItem.getType());
			item.setOcsAnswer(questionItem.getOcsAnswer());
			item.setTitle(questionItem.getTitle());
			item.setOcsAskCode(questionItem.getOcsAskCode());
			item.setOcsNoAnswerCode(questionItem.getOcsNoAnswerCode());
			item.setOcsAskCode2(questionItem.getOcsAskCode2());
			item.setOcsNoAnswerCode2(questionItem.getOcsNoAnswerCode2());
			item.setOcsAskCode3(questionItem.getOcsAskCode3());
			item.setOcsNoAnswerCode3(questionItem.getOcsNoAnswerCode3());
			item.setPreText(questionItem.getPreText());
			item.setPostText(questionItem.getPostText());
			item.setOcsAskCode2(questionItem.getOcsAskCode2());
			item.setPreText2(questionItem.getPreText2());
			item.setPostText2(questionItem.getPostText2());
			item.setValidator(questionItem.getValidator());
			item.setThumnailImage(questionItem.getThumnailImage());
			item.setItemGroup(questionItem.getItemGroup());
			item.setNoneFlag(questionItem.getNoneFlag());
			item.setChildItems(questionItem.getChildItems());
			
			item.setSortOrder(questionItem.getSortOrder());
			
			item.setParentQuestion(parentQuestion);
			item.setActive(true);
			
			adminService.createQuestionItem(item);
		}
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		return CommonUtils.toJson(map);
	}
	
	
	
	
	/**
	 * 임시로 copyQuestionId를 받아서 질문을 copy한다.
	 * @param parentQuestionId, copyQuestionId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/questionCopy",method=RequestMethod.POST)
	@ResponseBody
	public String questionCopy(
				@RequestParam("parentGroupId") String parentGroupId,
				@RequestParam("parentQuestionId") String parentQuestionId,
				@RequestParam("parentItemId") String parentItemId,
				@RequestParam("depth") String depth,
				@RequestParam("copyQuestionId") String copyQuestionId) throws Exception {
				
		QuestionGroup parentGroup = new QuestionGroup();
		Question parentQuestion = new Question();
		
		Question question = new Question();
		Question copyQuestion =  adminService.loadQuestion(copyQuestionId);
					
		question.setType(copyQuestion.getType());
		question.setQuestionNo(copyQuestion.getQuestionNo());
		question.setOcsAskCode(copyQuestion.getOcsAskCode());
		question.setOcsNoAnswerCode(copyQuestion.getOcsNoAnswerCode());
		question.setOcsAskCode2(copyQuestion.getOcsAskCode2());
		question.setOcsNoAnswerCode2(copyQuestion.getOcsNoAnswerCode2());
		question.setOcsAskCode3(copyQuestion.getOcsAskCode3());
		question.setOcsNoAnswerCode3(copyQuestion.getOcsNoAnswerCode3());
		question.setTitle(copyQuestion.getTitle());
		question.setGender(copyQuestion.getGender());
		question.setValidator(copyQuestion.getValidator());
		question.setDescription(copyQuestion.getDescription());
		question.setPreText(copyQuestion.getPreText());
		question.setPostText(copyQuestion.getPostText());
		question.setRequired(copyQuestion.isRequired());
		question.setNavigationStressFlag(copyQuestion.isNavigationStressFlag());
		question.setNavigationNutritionFlag(copyQuestion.isNavigationNutritionFlag());
		question.setCheckListRequired(copyQuestion.getCheckListRequired());
		question.setNurseQuestionType(copyQuestion.getNurseQuestionType());
		question.setChildNurseItems(copyQuestion.getChildNurseItems());
		question.setNurseOcsAnswer(copyQuestion.getNurseOcsAnswer());
		question.setNurseOcsAskCode(copyQuestion.getNurseOcsAskCode());
		question.setNurseOcsNoAnswerCode(copyQuestion.getNurseOcsNoAnswerCode());
		
		question.setSortOrder(999);
		question.setDescription("");
		question.setDepth(Integer.parseInt(depth));
		
		
		parentGroup = adminService.loadQuestionGroup(parentGroupId);
		if(CommonUtils.isValid(parentQuestionId)) {
			parentQuestion = adminService.loadQuestion(parentQuestionId);
			question.setParentQuestion(parentQuestion);
		}
		
		if(CommonUtils.isValid(parentItemId)) {
			List<String> parentItems = new ArrayList<String>();
			parentItems.add(parentItemId);
			question.setParentItems(parentItems);
		}
		
		question.setActive(true);
		question.setParentGroup(parentGroup);
		
		adminService.createQuestion(question);

		//상위 답변에 하위에 추가질문이 존재하는지 여부설정.
		if(question.getParentItems() != null) {
			for(String parentItem : question.getParentItems()) {
				QuestionItem it = adminService.loadQuestionItem(parentItem);
				if(!it.isExistChildQuestion()) {
					it.setExistChildQuestion(true);
					adminService.saveQuestionItem(it);
				}
			}
		}
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("success", "true");
		map.put("questionId", question.getId());
		return CommonUtils.toJson(map);
	}
	
	/**
	 * 배치작업을 테스트로 돌린다.
	 * @return
	 */
	@RequestMapping("/testJob")
	public String testJob(){
		jobService.changeToCompleted();
		return "";
	}
	
	@RequestMapping(value="savePatientPassword")
	public void savePatientPassword(
			@RequestParam("id") String id,
			@RequestParam("password") String password,
			HttpServletResponse httpServletResponse) throws Exception {
		userService.insertPasswordById(id, password);
		ViewUtils.jsonSuccess(httpServletResponse, jsonFactory.create(), null);
	}	
	
	//yyyy-mm-dd 
	@RequestMapping(value="/syncInstance")
	public void syncInstance(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws IOException{
		
		List<CheckupInstance> results = inService.syncAndSearchInstance(startDate, endDate);
		
		for(CheckupInstance result  : results){
			log.info("== Start Sync == [" + result.getPatno() + "]");
			try{
				ocsInterfaceService.syncAllOcsWebDb(result);
			}catch(Exception e){
				log.error("Error :",e);
			}
		}
	}
	
	//yyyy-mm-dd 
	@RequestMapping(value="/syncInstanceForUser")
	public void syncInstanceForUser(@RequestParam("patno") String patno,@RequestParam("hopeDate") String hopeDate) throws IOException{
		
		CheckupInstance instance = inService.findInstanceByReserveDateStr(userService.findPatient(patno), hopeDate);
		
		if(instance != null){
			log.info("== Start Sync == [" + instance.getPatno() + "]");
			try{
				ocsInterfaceService.syncAllOcsWebDb(instance);
			}catch(Exception e){
				log.error("Error :",e);
			}
		}
	}
}
