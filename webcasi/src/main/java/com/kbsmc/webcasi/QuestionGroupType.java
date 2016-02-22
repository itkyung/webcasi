package com.kbsmc.webcasi;

/**
 * 각 질문그룹의 유형.
 * 해당 질문그룹을 처리할 화면의 URL을 리턴한다.
 * @author bizwave
 *
 */
public enum QuestionGroupType {
	NORMAL_1(){
		//한 화면에서 1Depth의 Question만 나오는 유형 
		public String getTemplate(){
			return "/member/checkup/questionGroup";
		}
		
		public String getLabel(){
			return "일반형";
		}
	},
	NORMAL_DENTAL(){
		public String getTemplate(){
			return "/member/checkup/questionGroup";
		}
		
		public String getLabel(){
			return "일반형(치과)";
		}
	},
	NORMAL_WOMAN(){
		
		public String getTemplate(){
			return "/member/checkup/questionGroup";
		}
		
		public String getLabel(){
			return "일반형(여성설문)";
		}
	},
	
	NORMAL_2(){
		//한 화면에서 1Depth + 2Depth의 Question가 동시에 나오는 유형 
		public String getTemplate(){
			return "/member/checkup/questionGroup2";
		}		
		
		public String getLabel(){
			return "일반형2";
		}
	},
	SLEEP(){
		//수면페이지 
		public String getTemplate(){
			return "/member/checkup/custom/sleep";
		}		
		
		public String getLabel(){
			return "수면페이지";
		}
	},
	
	CUSTOM_1(){
		//실제로 하드코딩되는 질문그룹 페이지에 대한 template이름을 리턴한다.
		public String getTemplate(){
			return "/member/checkup/custom/drink";
		}
		
		public String getLabel(){
			return "음주[Custom]";
		}
	},
	CUSTOM_2(){
		public String getTemplate(){
			return "/member/checkup/custom/body";
		}
		
		public String getLabel(){
			return "질병력[몸]";
		}
	},
	CUSTOM_3(){
		public String getTemplate(){
			return "/member/checkup/custom/family";
		}
		
		public String getLabel(){
			return "가족력";
		}
	},
	CUSTOM_4(){
		public String getTemplate(){
			return "/member/checkup/custom/sleepacc";
		}
		
		public String getLabel(){
			return "수면정밀";
		}
	},
	NUTRITION_0(){
		public String getTemplate(){
			return "/member/checkup/nutrition/intro";
		}
		
		public String getLabel(){
			return "영양문진(인트로)";
		}
	},
	NUTRITION_1(){
		public String getTemplate(){
			return "/member/checkup/nutrition/rice";
		}
		
		public String getLabel(){
			return "영양문진(밥)";
		}		
	},
	NUTRITION_2(){
		public String getTemplate(){
			return "/member/checkup/nutrition/normal";
		}
		
		public String getLabel(){
			return "영양문진(일반)";
		}		
	},
	NUTRITION_3(){
		public String getTemplate(){
			return "/member/checkup/nutrition/habit";
		}
		
		public String getLabel(){
			return "영양문진(식생활)";
		}		
	},
	NUTRITION_4(){
		public String getTemplate(){
			return "/member/checkup/nutrition/calory";
		}
		
		public String getLabel(){
			return "영양문진(칼로리)";
		}		
	};	
	
	
	public String getTemplate(){
		return null;
	}
	
	public String getLabel(){
		return null;
	}
	
}
