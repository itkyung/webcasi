package com.kbsmc.webcasi;

/**
 * 문진 Instance의 진행상태.
 * @author bizwave
 *
 */
public enum InstanceStatus {
	READY,	//예약번호를 기반으로 처음 인스턴스가 생성됨.
	IN_PROGRESS,	//문진 진행중.
	FIRST_COMPLETED,	//환자 문진 완료
	OCS_IF,	//OCS IF완료.	사용안함.
	NURSE_CHECK,	//간호사 체크중. 사용안함.
	COMPLETED,	//간호사 체크까지 완료.
	CLOSED	//강제 종료됨. 예를 들어서 예약을 한후 문진 진행을 하다가 종료를 안한경우 어드민이 강제 종료 또는 신규 예약번호로 문진을 다시할경우 기존것은 종료.
			//그리고 일배치를 돌면서 예약날짜가 오늘이전것이면 무조건 CLOSE상태로 만든다. 
}
