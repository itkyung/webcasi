package com.kbsmc.webcasi.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kbsmc.webcasi.NutritionItemType;
import com.kbsmc.webcasi.QuestionType;
import com.kbsmc.webcasi.entity.NurseCheckResult;
import com.kbsmc.webcasi.entity.QuestionResult;

public class ResultMaker {

	/**
	 * QuestionId를 Key로 하는 답변맵을 만든다.
	 * 이것은 Subjective를 제외한 나머지인경우이다.
	 * Subjective는 별도로 만든다.
	 * @param results
	 * @return
	 */
	public static Map<String,Map<String,QuestionResult>> makeResultMap(List<QuestionResult> results){
		Map<String, Map<String,QuestionResult>> map = new HashMap<String,Map<String,QuestionResult>>();
		
		for(QuestionResult result : results){
			QuestionType type = result.getType();
			boolean isContinue = false;
			
			switch(type){
			case SUBJECTIVE:
			case SUBJECTIVE_HOUR_MINUTE:
			case SUBJECTIVE_HOUR_MINUTE_RANGE:	
			case SUBJECTIVE_MONTH_DATE_RANGE:
			case SUBJECTIVE_YEAR:
			case SUBJECTIVE_YEAR_MONTH:
			case SUBJECTIVE_YEAR_MONTH_DAY:
			case SUBJECTIVE_YEAR_MONTH_RANGE:
				isContinue = true;
			}
			
			
			if(isContinue) continue;
			Map<String,QuestionResult> subResults = map.get(result.getQuestion().getId());
			if(subResults == null){
				subResults = new HashMap<String,QuestionResult>();
				map.put(result.getQuestion().getId(), subResults);
			}
			subResults.put(result.getObjectiveValue(),result);
		}
		
		return map;
	}
	

	/**
	 * QuestionId를 Key로 하는 답변맵을 만든다.
	 * Subjective종류만 구해서 만든다.
	 * @param results
	 * @return
	 */
	public static Map<String,QuestionResult> makeSubjectiveMap(List<QuestionResult> results){
		Map<String,QuestionResult> map = new HashMap<String,QuestionResult>();
		
		for(QuestionResult result : results){
			QuestionType type = result.getType();
			boolean isContinue = false;
			
			switch(type){
			case SUBJECTIVE:
			case SUBJECTIVE_HOUR_MINUTE:
			case SUBJECTIVE_HOUR_MINUTE_RANGE:
			case SUBJECTIVE_MONTH_DATE_RANGE:
			case SUBJECTIVE_YEAR:
			case SUBJECTIVE_YEAR_MONTH:
			case SUBJECTIVE_YEAR_MONTH_DAY:
			case SUBJECTIVE_YEAR_MONTH_RANGE:
			case TEXT_AREA:
				isContinue = false;
				break;
			default :
				isContinue = true;
			}
			
			if(isContinue) continue;
			if(!map.containsKey(result.getQuestion().getId())){
				map.put(result.getQuestion().getId(),result);
			}
		}
		return map;
	}
	/**
	 * 
	 * 영양문진에서 관련된 답변이다. 이것은 답변이 하나밖에 존재하지 않는다.
	 * @param results
	 * @return
	 */
	public static Map<String,String> makeResultMap(List<QuestionResult> results,NutritionItemType itemGroup){
		Map<String, String> map = new HashMap<String,String>();
		for(QuestionResult result : results){
			if(itemGroup.equals(result.getItemGroup())){
				if(itemGroup.equals(NutritionItemType.WHETHER)){
					//이경우에는 체크박스 유형이기 때문에 답변이 두개이상 될수가 있다. ,로 구분한다.
					if(map.containsKey(result.getQuestion().getId())){
						String orgValue = map.get(result.getQuestion().getId());
						orgValue = orgValue + "," + result.getObjectiveValue();
						map.put(result.getQuestion().getId(),orgValue);
					}else{
						map.put(result.getQuestion().getId(),result.getObjectiveValue());
					}
				}else{
					map.put(result.getQuestion().getId(),result.getObjectiveValue());
				}
			}
		}
		
		return map;
	}
	
	public static Map<String,String> makeResultStringMap(List<QuestionResult> results,NutritionItemType itemGroup){
		Map<String, String> map = new HashMap<String,String>();
		for(QuestionResult result : results){
			if(itemGroup.equals(result.getItemGroup())){
				map.put(result.getQuestion().getId(),result.getStrValue());
			}
		}
		
		return map;
	}
	
	
	public static Map<String,NurseCheckResult> makeNurseResult(List<NurseCheckResult> results){
		Map<String, NurseCheckResult> map = new HashMap<String,NurseCheckResult>();
		
		for(NurseCheckResult result : results){
			
			if(!map.containsKey(result.getQuestion().getId())){
				map.put(result.getQuestion().getId(), result);
			}
		}
		
		return map;
	}
}
