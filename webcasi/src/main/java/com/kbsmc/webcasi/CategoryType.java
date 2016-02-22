package com.kbsmc.webcasi;

public enum CategoryType {
	CHECK_LIST(){
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "검진준비사항";
		}		
	},
	HEALTH_CHECKUP(){	
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "건강문진";
		}		
	},
	MENTAL_HEALTH(){
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "마음건강문진";
		}		
	},
	NUTRITION(){
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "영양문진";
		}		
	},
	HEALTH_AGE(){
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "건강나이";
		}			
	},
	SLEEP(){
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "수면정밀문진";
		}			
	},
	STRESS(){
		public String getUrl(){
			return null;
		}
		public String getLabel(){
			return "스트레스 및 피로";
		}			
	};
	
	
	public String getUrl(){
		return null;
	}
	public String getLabel(){
		return null;
	}
}
