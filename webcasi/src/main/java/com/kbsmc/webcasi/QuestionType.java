package com.kbsmc.webcasi;

/**
 * 질문과 질문 항목의 유형.
 * 
 * @author bizwave
 *
 */
public enum QuestionType {
	CHECK(){
		public String getLabel(){
			return "T_체크_수평";
		}
		
		public String getDescription(){
			return "객관식으로 여러개를 선택살수 있는 일반적인 체크박스";
		}	
	},
	CHECK_VER(){
		public String getLabel(){
			return "T_체크_수직";
		}
		
		public String getDescription(){
			return "객관식으로 여러개를 선택살수 있는 일반적인 체크박스";
		}	
	},
	CHECK_SUBJ(){
		public String getLabel(){
			return "T_체크_주관";
		}
		
		public String getDescription(){
			return "객관식 항목마다 체크박스와 주관식을 입력가능함";
		}
	},
	CHECK_SUBJ_1(){
		public String getLabel(){
			return "T_체크_주관_1";
		}
		
		public String getDescription(){
			return "체크박스 항목중에 체크박스+주관식 입력이 가능한 유형";
		}
	},
	CHECK_SUBJ_SUBJ(){
		public String getLabel(){
			return "T_체크_주관_주관";
		}
		
		public String getDescription(){
			return "T_체크_주관에서 객관식 항목명에 주관식을 더 입력가능함.";
		}
	},
	CHECK_SUBJ_RADIO_SUBJ(){
		public String getLabel(){
			return "T_체크_주관_라디오_주관";
		}
		
		public String getDescription(){
			return "T_체크_주관에서 라디오 항목이 더 붙고 주관식이 하나 더 붙는 유형.";
		}
	},
	RADIO_SUBJ_HOUR_MINUTE(){
		public String getLabel(){
			return "T_라디오_주관_시간_분";
		}
		
		public String getDescription(){
			return "라디오항목과 주관식(시간,분) 항목이 하나씩 있음.";
		}
	},	
	RADIO(){
		public String getLabel(){
			return "T_라디오_수직";
		}
		
		public String getDescription(){
			return "일반적인 라디오 박스. 한개만 선택가능. 수직형";
		}
	},
	RADIO_HOR(){
		public String getLabel(){
			return "T_라디오_수평";
		}
		
		public String getDescription(){
			return "일반적인 라디오 박스. 한개만 선택가능. 수평형";
		}		
	},
	RADIO_SUBJ_1(){
		public String getLabel(){
			return "T_라디오_주관_1";
		}
		
		public String getDescription(){
			return "라디오의 항목중에 라디오 + 주관식입력이 가능한 유형";
		}
	},
	RADIO_RADIO(){
		public String getLabel(){
			return "T_라디오_라디오";
		}
		
		public String getDescription(){
			return "라디오 문항에 라디오 항목이 더 붙는 유형";
		}
	},
	RADIO_IMAGE(){
		public String getLabel(){
			return "T_라디오_이미지";
		}
		
		public String getDescription(){
			return "라디오 문항에 조그만 이미지가 있는 항목";
		}		
	},
	OBJ_RADIO(){
		public String getLabel(){
			return "T_객관_라디오";
		}
		
		public String getDescription(){
			return "객관식 문항마다 라디오 항목이 더 붙은 유형";
		}
	},
	OBJ_RADIO_SUBJ(){
		public String getLabel(){
			return "T_객관_라디오_주관";
		}
		
		public String getDescription(){
			return "T_객관_라디오에 추가로 주관식을 입력받을수 있음";
		}
	},	
	
	SUBJECTIVE(){
		public String getLabel(){
			return "T_주관";
		}
		
		public String getDescription(){
			return "일반적인 주관식";
		}
	},
	SUBJECTIVE_YEAR_MONTH_RANGE(){
		public String getLabel(){
			return "T_연월_기간";
		}
		
		public String getDescription(){
			return "기간을 연월로 입력가능";
		}
	},
	SUBJECTIVE_MONTH_DATE_RANGE(){
		public String getLabel(){
			return "T_월일_기간";
		}
		
		public String getDescription(){
			return "기간을 월일로 입력가능";
		}
	},
	SUBJECTIVE_YEAR_MONTH(){
		public String getLabel(){
			return "T_연월";
		}
		
		public String getDescription(){
			return "연과 월을 선택가능";
		}
	},
	SUBJECTIVE_YEAR_MONTH_DAY(){
		public String getLabel(){
			return "T_연월일";
		}
		
		public String getDescription(){
			return "연,월,일 선택가능";
		}
	},
	
	SUBJECTIVE_YEAR(){
		public String getLabel(){
			return "T_주관_연";
		}
		
		public String getDescription(){
			return "연도를 입력가능";
		}
	},
	SUBJECTIVE_HOUR_MINUTE(){
		public String getLabel(){
			return "T_시_분";
		}
		
		public String getDescription(){
			return "시와 분을 입력가능";
		}
	},
	SUBJECTIVE_HOUR_MINUTE_RANGE(){
		public String getLabel(){
			return "T_시간_분";
		}
		
		public String getDescription(){
			return "시간과 분을 입력가능";
		}
	},	
	
	TEXT_AREA(){
		public String getLabel(){
			return "T_텍스트박스";
		}
		
		public String getDescription(){
			return "긴글을 입력가능함";
		}
	},
	CUSTOM(){
		public String getLabel(){
			return "T_CUSTOM";
		}
		
		public String getDescription(){
			return "하드코딩되는 페이지에 입력하는 질문들";
		}
	},
	TAB(){
		public String getLabel(){
			return "T_TAB";
		}
		
		public String getDescription(){
			return "2단계에 바로 Tab형태의 질문들이 존재할경우에 상위질문유형(예:식생활습관)";
		}		
	},
	TABLE(){
		public String getLabel(){
			return "T_TABLE";
		}
		
		public String getDescription(){
			return "테이블 형태(가족력)";
		}	
	};
	
	
	
	public String getLabel(){
		return null;
	}
	
	public String getDescription(){
		return null;
	}
	
}
