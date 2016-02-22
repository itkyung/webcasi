package com.kbsmc.webcasi;

public enum ValidatorType {
	NUMBER_0_9,//숫자인데 0~9까지
	NUMBER,//숫자만 가능
	AGE,	//숫자이면서 나이입력 (0 ~ 100)
	NONE,
	YEAR,
	MONTH,
	HOUR,
	MINUTE
}
