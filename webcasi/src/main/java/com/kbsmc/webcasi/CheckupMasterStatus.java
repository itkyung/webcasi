package com.kbsmc.webcasi;

public enum CheckupMasterStatus {
	SAVED,	//마스터 작업중인 상태.
	COMPLETED, //마스터 작업이 완료된 상태. 아직 활성화가 되지는 않았음.
	ACTIVE,	//실제로 활성화된 상태.
	INACTIVE
}
