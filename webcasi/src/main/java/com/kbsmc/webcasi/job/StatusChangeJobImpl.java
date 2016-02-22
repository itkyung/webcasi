package com.kbsmc.webcasi.job;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kbsmc.webcasi.InstanceStatus;
import com.kbsmc.webcasi.checkup.ICheckupInstanceDAO;
import com.kbsmc.webcasi.entity.CheckupInstance;
import com.kbsmc.webcasi.identity.IUserDAO;
import com.kbsmc.webcasi.identity.IUserService;
import com.kbsmc.webcasi.identity.entity.LinkReserveData;

@Service("statusChangeJob")
public class StatusChangeJobImpl implements IStatusChangeJob{
	@Autowired ICheckupInstanceDAO dao;
	@Autowired IUserDAO userDao;
	@Autowired IUserService userService;
	
	/**
	 *   매일 야간 11시에 구동된다. 야간 11시 전에 구동되면 안된다. 문진이 완료가 되기 때문이다.
	 *   1.instance중에서 acptDate가 null이면서 문진이 진행중인것을 찾아서 surempt의 acptDate가 오늘자인 예약정보가 있으면 instance에 acptDate에 update한다.
	 *   	--이것은 문진을 접수한 이후에 아직 우리 시스템에 로그인을 하지 않은 사용자를 위한 처리이다.
	 *   2.instance의 acpteDate가 null이 아니면서 아직 complete되지 않은 instance를 다시한번 OCS에 접수취소가 되었는지 확인해서 취소되었으면 acptDate를 null로 하고 hopedate를 변경한다. 
	 *     접수 취소가 안되었으면 완료처리한다.
	 */
	@Transactional
	public void changeToCompleted(){
		List<CheckupInstance> results = dao.findInstance(false, InstanceStatus.IN_PROGRESS,InstanceStatus.READY,InstanceStatus.FIRST_COMPLETED);
		
		for(CheckupInstance instance : results){
			LinkReserveData data = userDao.findReserveData(instance.getPatno(), new Date(),false);
			if(data != null){
				instance.setAcptDate(data.getAcptDate());
				instance.setReserveDate(data.getHopeDate());
				dao.saveInstance(instance);
			}
		}
		
		dao.completeCheckupInstance();
	}

	/**
	 * 	사용 안함.
	 *   CheckupInstance에서 reserveDate가 오늘을 포함한 이전날짜인것을 체크해서 문진의 완료 상태가
	 *   FIRST_COMPLETE이면  surtempt테이블을 뒤져서 acptDate가 null이 아닌 instance의 상태를 COMPLETED로 바꾼다. 
	 *   
	 */
	@Deprecated
	@Transactional
	@Override
	public void changeToClosed() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
