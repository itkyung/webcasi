package com.kbsmc.webcasi.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.UserCheckObj;
import com.kbsmc.webcasi.identity.entity.User;

/**
 * 로그인관련된것과 Admin초기화 관련된것 처리
 * @author bizwave
 *
 */
@Controller
@RequestMapping(value="/login")
public class LoginController {
	@Autowired private IUserService userService;
	
	private Log log = LogFactory.getLog(LoginController.class);
	
	@RequestMapping(value="/loginForm",method=RequestMethod.GET)
	public String loginForm(Model model){
		
		return "/login";
	}
	
	@RequestMapping(value="/initAdmin",method=RequestMethod.GET)
	public String iniAdmin(){
		
		userService.initAdmin();
		userService.initNurse();
		
		return "/successResult";
	}
	
	/**
	 * 1. DB에 해당 사용자가 존재하는지 확인.(이름과 수진자번호의 조합으로 검색)
	 * 2. 없으면 OCS에 존재하는지 확인해서 있으면 DB에 insert후 비번설정 팝업, OCS에도 없으면 에러처리 팝업.
	 * 3. 있으면 비번설정이 필요한지 확인해서 필요하면 비번설정 팝업.
	 * 4. 비번이 이미 설정되어있으면 정상적인 로그인 프로세스로 안내.
	 * 
	 * 
	 * @param patno -->기존에는 주민번호이었다가 수진자번호로 바뀜.
	 * @return
	 */
	@RequestMapping(value="/checkUser",method=RequestMethod.POST,produces = "application/json;charset=utf-8")
	@ResponseBody
	public String checkUser(@RequestParam("j_username") String patno,
			@RequestParam("patName") String patName){
		UserCheckObj result = new UserCheckObj();
		
		User user = userService.findPatient(patno);
		//User user = userService.findByPlainLoginId(loginId);
		if(user == null || !user.getName().equals(patName)){
			//if(loginId.length() == 13){
				//OCS에서 검색해서 사용자를 Insert시킨다. OCS에도 없으면 에러발생으로처리.
				if(userService.initUserFromOCS(patno,patName)){
					result.setNeedPwInsert(true);	//초기 설정이기 때문에 비번설정필요함.
				}else{
					result.setErrorOccur(true);	//OCS에도 해당 사용자가 없다. 
					result.setMsg("입력한 수진자번호와 성명에 해당하는 예약정보가 존재하지 않습니다.\n로그인문의하기 \n 서울센터 02)2001-1140 \n 수원센터 031)303-0300");
				}
//			}else{
//				result.setErrorOccur(true);	// 해당 사용자가 없다. 
//				result.setMsg("입력한 아이디에 해당하는 사용자데이타가 존재하지 않습니다.");
//			}
		}else{
			if(user.getPassword() == null){
				result.setNeedPwInsert(true);	
			}else{
				result.setSuccess(true);
			}
		}
		
		return CommonUtils.toJson(result);
	}
	
	/**
	 * 해당 유저의 비밀번호를 설정한다. 
	 * @param loginId
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/insertPassword",method=RequestMethod.POST,produces="application/json;charset=utf-8")
	@ResponseBody
	public String insertPassword(@RequestParam("j_username") String patno,@RequestParam("j_password") String password){
		UserCheckObj result = new UserCheckObj();
		
		try{
			userService.insertPassword(patno, password);
			result.setSuccess(true);
		}catch(Exception e){
			result.setSuccess(false);
			result.setMsg(e.getMessage());
			log.error("Change password error : ",e);
		}
		
		return CommonUtils.toJson(result);
	}
	
	 
	@RequestMapping(value="/findPatno",method=RequestMethod.POST,produces="application/json;charset=utf-8")
	@ResponseBody
	public String findPatno(@RequestParam("resno") String resno){
		UserCheckObj result = new UserCheckObj();
		try{
			String patno = userService.findAndSyncPatno(resno);
			if(patno == null){
				result.setSuccess(false);
				result.setMsg("입력한 주민번호에 해당하는 수진자정보가 존재하지 않습니다.");
			}else{
				result.setSuccess(true);
				result.setPatno(patno);
			}
			
		}catch(Exception e){
			result.setSuccess(false);
			result.setMsg("입력한 주민번호에 해당하는 수진자정보가 존재하지 않습니다.");
			log.error("Find Patno : ",e);
		}
		
		return CommonUtils.toJson(result);
	}
	
}
