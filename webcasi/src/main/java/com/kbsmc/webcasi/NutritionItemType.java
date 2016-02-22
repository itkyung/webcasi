package com.kbsmc.webcasi;

public enum NutritionItemType {
	FREQUENCY(){
		public String getLabel(){
			return "평균 섭취 빈도";
		}
	},
	
	QUANTITY(){
		public String getLabel(){
			return "평균 섭취 분량";
		}
	},
	MONTH(){
		public String getLabel(){
			return "섭취 개월";
		}
	},
	WHETHER(){
		public String getLabel(){
			return  "섭취 여부";
		}
	}
	;
	
	public String getLabel(){
		return null;
	}

}
