var _processingFlag = false;

function getStrByte(str) {
	var p, len = 0;
	for(p=0; p<str.length; p++) {
		(str.charCodeAt(p) > 255) ? len+=2 : len++;
	}
	return len;
}

$(document).ready(function(){
	
	$("input:checkbox").change(function(){
		
		processCheckboxEvent($(this));
	});
	
	
	$("input:radio").change(function(){

		processRadioEvent($(this));
		
	});
	
	
	$("input:text").blur(function(){
		processText($(this));
	});
	
	$("input:text").each(function(){
		var questionType = $(this).attr("questionType");
		if(questionType == "SUBJECTIVE_YEAR_MONTH_DAY" || questionType == "SUBJECTIVE_YEAR_MONTH"){
			$(this).attr("readonly",true);
		}
		
	});
	
	$("textarea").keyup(function(){
		var limit = 50;
		// 잆력 값 저장
        var text = $(this).val();
        // 입력값 길이 저장
        var textlength = text.length;
        if(textlength > limit)
        {     
            // 제한 글자 길이만큼 값 재 저장
        	alert("50자까지만 입력가능합니다.");
            $(this).val(text.substr(0,limit));
            return false;
        }
	});
	
	$("textarea").blur(function(){
		var questionId = $(this).attr("id");
		var questionType = $(this).attr("questionType");

		var itemType = $(this).attr("itemType");
		
		var value = $(this).val();
		if(value == null){
			return;
		}
		
		if(getStrByte(value) > 100){
			alert("50자까지만 입력가능합니다.");
			$(this).val("");
			return;
		}
		
		var params = {questionId:questionId,type:questionType};
		params.strValue = value;
		
		
		doSubmit(params,questionId,false,"on",questionId);
	});

	$("select").change(function(){
		var questionId = $(this).attr("questionId");
		if(questionId != undefined){
			processText($("#" + questionId + "_hour_str1"));
		}
	});
	
	$(".SUBJECTIVE_YEAR_MONTH").monthpicker({
		showOn : 'button',
		changeYear : true,
		dateFormat : 'yy-MM',
		buttonImage:  _requestPath + "/resources/images/calendar.png",
		buttonImageOnly: true,
		maxDate : '0',
		monthNames : ['01','02','03','04','05','06','07','08','09','10','11','12'],
		monthNamesShort : ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		onSelect : function(dateText){
			var dateObj = $(this);
			submitSubjective(dateObj,dateText);
		}
	});
		
	
	$(".SUBJECTIVE_YEAR_MONTH_DAY").datepicker({
		showOn : 'button',
		dateFormat : 'yy-MM-dd',
		buttonImage:  _requestPath + "/resources/images/calendar.png",
		buttonImageOnly: true,
		changeYear : true,
		changeMonth : true,
		maxDate : '+0D',
		dayNamesMin : ['일','월','화','수','목','금','토'],
		monthNames : ['01','02','03','04','05','06','07','08','09','10','11','12'],
		monthNamesShort : ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
		onSelect : function(dateText){
			var dateObj = $(this);
			submitSubjective(dateObj,dateText);
		}		
	});
	
	
});

function clickRadioLink(id){
	var radioObj = $("#"+id);
	if(!radioObj.is(':checked') && !radioObj.is(':disabled')){
		radioObj.attr("checked",true);
		processRadioEvent(radioObj);
	}
}

function processText($textObj){
	var tabQuestion = $textObj.attr("tabQuestion");
	if(tabQuestion == "true"){
		//탭질문 하위에 바로 존재하는 text일경우에는 이벤트를 무시한다.
		return;
	}
	
	var itemGroup = $textObj.attr("itemGroup");
	var validator = $textObj.attr("validator");
	
	var questionId = null;
	if(validator == "HOUR_AM_PM"){
		questionId = $textObj.attr("questionId");
	}else{
		questionId = $textObj.attr("id");
	}
	
	if(questionId == null || questionId == undefined){
		questionId = $textObj.attr("questionId");
	}
	var questionType = $textObj.attr("questionType");
	
	var itemType = $textObj.attr("itemType");
	var existChild = $textObj.attr("existChild");
	if(existChild == null || existChild == undefined){
		existChild = false;
	}
	
	var minRange = $textObj.attr("minRange");
	var maxRange = $textObj.attr("maxRange");
	
	if(questionType == "SUBJECTIVE_YEAR_MONTH" || questionType == "SUBJECTIVE_YEAR_MONTH_DAY"){
		return;
	}
	
	var value = $textObj.val();
	if(value == null){
		return;
	}
	
	if(validator == "HOUR_AM_PM"){
		var ampm = $("#"+questionId+"_ampm option:selected").val();
		if(ampm == "am"){
			minRange = 0;
			maxRange = 11;
		}else{
			minRange = 1;
			maxRange = 12;
		}
	}
	
	if(!doValidation(value,validator,minRange,maxRange)){
		$textObj.val("");
		return;
	}
	
	if(itemType == "OBJ_RADIO_SUBJ" || itemType == "CHECK_SUBJ" || itemType == "CHECK_SUBJ_SUBJ" 
		|| itemType == "CHECK_SUBJ_1" || itemType == "CHECK_SUBJ_RADIO_SUBJ" || itemType == "RADIO_SUBJ_HOUR_MINUTE" 
		|| itemType == "RADIO_SUBJ_1"){
		//아이템에 붙어있는 text일 경우에 별도로 처리한다.
		submitItemSubjective($textObj,itemType,existChild);
		return;
	}
	
	
	var params = {questionId:questionId,type:questionType};
	if(itemGroup != undefined){
		params.itemGroup = itemGroup;
	}
	
	if(questionType == "SUBJECTIVE" || questionType == "SUBJECTIVE_YEAR"){
		params.strValue = value;
	}else if(questionType == "SUBJECTIVE_HOUR_MINUTE" || questionType == "SUBJECTIVE_YEAR_MONTH_RANGE" || questionType == "SUBJECTIVE_HOUR_MINUTE_RANGE"){
		var subj1 = $textObj.attr("subj1");
		var subj2 = $textObj.attr("subj2");
		
		if(validator == "HOUR_AM_PM"){
			if(subj1 == "true"){
				//오후일 경우에는 시간을 바꾸어서 commit시킨다.
				var ampm = $("#"+questionId+"_ampm option:selected").val();
				if(ampm == "pm"){
					if(value != "12"){
						params.strValue = 12 + Number(value);
					}else{
						params.strValue = value;
					}
				}else{
					params.strValue = value;
				}
			}
		}else{
			if(subj1 == "true"){
				params.strValue = value;
			}
		}
		
		if(subj2 == "true"){
			params.strValue2 = value;
		}
	}
	
	doSubmit(params,questionId,existChild,"on",questionId);
}


/**
 * Radio버튼이 onChange되거나 또는 Radio에 붙어있는 A link클릭시 호출.
 * @param radioObj
 */
function processRadioEvent($radioObj){
	var nurseCheck = $radioObj.attr("nurseCheck");
	var questionId = $radioObj.attr("questionId");
	
	if(nurseCheck == "true"){
		var embededItemId = $radioObj.attr("embededItemId");
		doNurseCheck(_instanceId,questionId,embededItemId);
		return;
	}
	
	var itemType = $radioObj.attr("itemType");		
	var existChild = $radioObj.attr("existChild");
	var itemGroup = $radioObj.attr("itemGroup");
	var questionType = $radioObj.attr("questionType");
	
	
	if(itemType.startsWith("OBJ_")){
		//객관식일 경우 객관식 처리를 별도로 한다.
		submitObjective($radioObj,itemType,existChild);
		return;
	}
	
	if(itemType == "CHECK_SUBJ_RADIO_SUBJ"){
		//별도의 처리를 한다,
		submitSubjRadioSubj($radioObj,itemType,existChild);
		return;
	}
	
	if(itemType == "RADIO_RADIO"){
		submitRadioRadio($radioObj,itemType,existChild);
		return;
	}
	
	var questionItemId = $radioObj.attr("id");
	var checked = false;
	var loopCount = $radioObj.attr("loopCount");
	
	if($radioObj.is(':checked')){
		checked = true;
		if(existChild == "true"){
			//showDepth(questionItemId);
			
		}
	}else{
		checked = false;
		if(existChild == "true"){
			hideDepth(questionItemId);
		}
	}
	
	
	//서버에 Ajax를 이용해서 현재 질문의 답을 submit한다.
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType};
	if(itemGroup != undefined){
		params.itemGroup = itemGroup;
	}
	
	if(questionType != undefined){
		params.questionType = questionType;
	}
	
	if(loopCount != undefined){
		params.loopCount = loopCount;
	}
	doSubmit(params,questionItemId,existChild,"on",questionId);
}

function clickCheckboxLink(id){
	var checkObj = $("#"+id);
	if(!checkObj.is(':checked')){
		checkObj.attr("checked",true);
	}else{
		checkObj.attr("checked",false);
	}
	processCheckboxEvent(checkObj);
}

/**
 * Checkbox를 클릭하거나 그 우측의 Checkbox link를 클릭시 호출.
 * @param $checkObj
 */
function processCheckboxEvent($checkObj){
	var nurseCheck = $checkObj.attr("nurseCheck");
	var questionId = $checkObj.attr("questionId");
	if(nurseCheck == "true"){
		doNurseCheck(_instanceId,questionId);
		return;
	}
	
	var itemType = $checkObj.attr("itemType");
	var existChild = $checkObj.attr("existChild");
	var itemGroup = $checkObj.attr("itemGroup");
	var questionType = $checkObj.attr("questionType");
	var tabQuestion = $checkObj.attr("tabQuestion");
	var questionItemId = $checkObj.attr("id");
	
	var onOff = "";
		
	if($checkObj.is(':checked')){
		onOff = "on";
		
		if(existChild == "true"){
			//showDepth(questionItemId);
		}
		
	}else{
		onOff = "off";
		
		if(existChild == "true"){
			hideDepth(questionItemId);
		}
	}
	
	if(tabQuestion == "true"){
		//탭질문 하위에 바로 존재하는 check일경우에는 이벤트를 무시한다.
		//탭이면 단지  check_subj의 subj만 활성화 시킨다.
		
		if(itemType == 'CHECK_SUBJ' || itemType == 'CHECK_SUBJ_1'){
			$("input:text").each(function(){
				var _subType = $(this).attr("itemType");
				var _subQuestionId = $(this).attr("questionId");
				var _subItemId = $(this).attr("itemId");
			
				if(_subQuestionId == questionId 
						&& _subItemId == questionItemId ){
					if(onOff == "on"){
						$(this).attr("disabled",false);
					}else{
						$(this).attr("value","");
						$(this).attr("disabled",true);
					}
				}
			});
		}
		
		return;
	}
	
	//서버에 Ajax를 이용해서 현재 질문의 답을 submit한다.
	var params = {questionId:questionId,questionItemId:questionItemId,onOffFlag:onOff,type:itemType};
	if(itemGroup != undefined){
		params.itemGroup = itemGroup;
	}
	if(questionType != undefined){
		params.questionType = questionType;
	}
	
	doSubmit(params,questionItemId,existChild,onOff,questionId);
	
}


/**
 * 해당 질문을 입력한것을 취소시킨다.inactive처리하고 result에 미결코드로 입력한다.
 * 그리고 답변 유형에 따라서 itemId를 기반으로 checked = false 처리하고 그리고 답변확인 이미지 삭제와 필수필드 completedflag를 원복시킨다.
 * progress도 바꾼다.
 * @param itemId
 * @param questionId
 */
function doCancel(itemId,questionId,type,isRadio){
	var url = _requestPath + '/member/checkup/doCancel';
	var params = {questionId:questionId,questionItemId:itemId,type:type};
	
	if(_nurseEditable){
		//간호사가 로그인한 상태에서 수정하는 URL을 호출하고 params에 instance Id를 넘겨준다.
		url = _requestPath + "/nurse/doCancel";
		params.instanceId = _instanceId;
	}
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : url,
		timeout : 10000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				var progress = result.progressRate;
				_updateProgress(progress);	//Global함수 호출 
				var type = result.type;
				
				if(type == "TAB"){
					//TAB일 경우에는 속도가 느려서 결과가 나오기전에 처리한다.
					
				}else{
					var isCheck = type.startsWith("CHECK") ? true : false;
					$("#"+itemId).attr("checked",false);
					//만약에 CHECK와 RADIO하단에 주관식이 붙어있는 유형의 경우에는 별도의 처리를 한다.
					//이벤트 핸들링이 발생하지 않으면서 값을 바꾸어야한다.
					
					
					uncheck(itemId,questionId,isCheck);
				}
			}else{
				alert(result.msg);
			}
			
		},
		error : function(response, status, err){
			alert("네트워크가 불안정합니다. 다시한번 시도해보세요.[" + status + "][" + err + "]");
		}
	});	//Ajax로 호출한다.
	
}

function submitSubjective($dateObj,value){
	var questionId = $dateObj.attr("id");
	var questionType = $dateObj.attr("questionType");
	
	var params = {questionId:questionId,type:questionType};
	params.dateValue = value;
	doSubmit(params,questionId,false,"on",questionId);
}

/**
 * Objective유형의 답변을 submit을 한다.
 * @param $obj
 */
function submitObjective($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	var embededItemId = $obj.attr("embededItemId");
	var questionType = $obj.attr("questionType");
	
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType,embededItemId:embededItemId};
	if(itemType == "OBJ_RADIO_SUBJ"){
		
		
	}
	
	if(questionType != undefined){
		params.questionType = questionType;
	}
	
	doSubmit(params,questionItemId,existChild,"on",questionId);
}

/**
 * RADIO_RADIO에 대한 처리를 한다.
 * @param $obj
 * @param itemType
 * @param existChild
 */
function submitRadioRadio($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	var embededItemId = $obj.attr("embededItemId");
	var questionType = $obj.attr("questionType");
	
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType};
	
	if(embededItemId != null && embededItemId != undefined){
		params.embededItemId = embededItemId;
	}
	
	if(questionType != undefined){
		params.questionType = questionType;
	}
		
	doSubmit(params,questionItemId,existChild,"on",questionId);
}

/**
 * check_subj_radio_subj일 경우에 radio값을 처리한다.
 * @param $obj
 */
function submitSubjRadioSubj($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	var embededItemId = $obj.attr("embededItemId");
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType,embededItemId:embededItemId,onOffFlag:"on"};
	
	doSubmit(params,questionItemId,existChild,"on",questionId);
}

function submitItemSubjective($obj,itemType,existChild){
	var questionId = $obj.attr("questionId");
	var questionItemId = $obj.attr("itemId");
	
	var value = $obj.val();
	
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType,onOffFlag:"on"};
	if(itemType=='OBJ_RADIO_SUBJ' || itemType == 'CHECK_SUBJ' || itemType == 'CHECK_SUBJ_1' || itemType == 'RADIO_SUBJ_1'){
		params.strValue = value;
	}
	if(itemType=='CHECK_SUBJ_SUBJ' || itemType=='CHECK_SUBJ_RADIO_SUBJ' || itemType == 'RADIO_SUBJ_HOUR_MINUTE'){
		var subj1 = $obj.attr("subj1");
		var subj2 = $obj.attr("subj2");
		if(subj1 == "true"){
			params.strValue = value;
		}
		if(subj2 == "true"){
			params.strValue2 = value;
		}
	}

	
	doSubmit(params,questionItemId,existChild,"on",questionId);
}

function doSubmit(params,itemId,existChild,onOff,questionId){
	
	
	var url = _requestPath + '/member/checkup/doSubmit';
	if(_nurseEditable){
		//간호사가 로그인한 상태에서 수정하는 URL을 호출하고 params에 instance ID를 넘겨준다.
		url = _requestPath + "/nurse/doSubmit";
		params.instanceId = _instanceId;
	}
	
	_processingFlag = true;
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : url,
		timeout : 13000,
		cache : false,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			_processingFlag = false;
			var success = result.success;
			if(success){
				var progress = result.progressRate;
				_updateProgress(progress);	//Global함수 호출 
				var itemGroup = result.itemGroup;
				
				if(onOff == "on"){
					if(itemGroup != undefined){
						var $cId = $("#complete_flag_" + questionId  + "_" + itemGroup);
						$("#tab-item-" + questionId).removeClass("required-tab-question");
						$cId.val("true");
						
						if(params.loopCount != undefined && params.loopCount > -1){
							//radio에서 loopCount가 있는 유형은 tab형 질문이다.
							var tabIdx = Number(params.loopCount);
							$( "#tabs" ).tabs("option","active",tabIdx);
						}
						
					}else{
						if(existChild == "true"){
							//하위에 질문이 존재할 경우에는 하위질문에서 처리를 완료하면 답변완료가 된다.
							var allCompleted = result.allCompleted;
							if(allCompleted == true)
								showDepth(itemId);
						}else{
							var allCompleted = result.allCompleted;
							if(allCompleted == true){
								var isRadio = params.type.startsWith("RADIO") ? true : false;
								var isObj = params.type.startsWith("OBJ") ? true : false;
								checkCompleted(itemId,questionId,isRadio,isObj);
							}else{
								var isCheck = params.type.startsWith("CHECK") || params.type.startsWith("OBJ") ? true : false;
								uncheck(itemId,questionId,isCheck);
							}
						}
						
						
						if(params.type.startsWith("RADIO")){
							//RADIO_RADIO,RADIO_SUBJ_HOUR_MINUTE,RADIO_SUBJ_1
							if(params.questionType == 'RADIO_RADIO'){
								//현재 선택된 item과 다른 item에 존재하는 embededitem을 다 disable처리한다.
								//현재 선택된 item과 같은 녀석은 enable시킨다.
								$("input:radio").each(function(){
									var _subType = $(this).attr("itemType");
									var _subQuestionId = $(this).attr("questionId");
									var _subItemId = $(this).attr("itemId");
									var _embededItemId = $(this).attr("embededItemId");
									if(_subType == "RADIO_RADIO" && _subQuestionId == questionId 
											&& _subItemId != itemId && _embededItemId != undefined){
										//같은 질문인데 다른 itemId를 가지고 있을 경우.
										$(this).removeAttr("checked");
										$(this).attr("disabled",true);
									}else if(_subType == "RADIO_RADIO" && _subQuestionId == questionId 
											&& _subItemId == itemId && _embededItemId != undefined){
										$(this).attr("disabled",false);
									}
								});
							}
							
							if(params.questionType == 'RADIO_SUBJ_1' || params.questionType == 'RADIO_SUBJ_HOUR_MINUTE'){
								$("input:text").each(function(){
									var _subType = $(this).attr("itemType");
									var _subQuestionId = $(this).attr("questionId");
									var _subItemId = $(this).attr("itemId");
									var _validator = $(this).attr("validator");
									
									if(_subType == params.questionType && _subQuestionId == questionId 
											&& _subItemId != itemId ){
										//같은 질문인데 다른 itemId를 가지고 있을 경우.
										$(this).attr("value","");
										$(this).attr("disabled",true);
									}else if(_subType == params.questionType && _subQuestionId == questionId 
											&& _subItemId == itemId ){
										if(_validator == "HOUR"){
											$(this).attr("value","00");
										}
										$(this).attr("disabled",false);
									}
								
								});
							}
							
							
						}
					}
				}else{
					
					var isCheck = params.type.startsWith("CHECK") || params.type.startsWith("OBJ") ? true : false;
					uncheck(itemId,questionId,isCheck);
				}
				
				if(params.type.startsWith("CHECK")){
					if(params.questionType == 'CHECK_SUBJ' || params.questionType == 'CHECK_SUBJ_SUBJ' || params.questionType == 'CHECK_SUBJ_1'
						|| params.questionType == 'CHECK_SUBJ_RADIO_SUBJ'){
						$("input:text").each(function(){
							var _subType = $(this).attr("itemType");
							var _subQuestionId = $(this).attr("questionId");
							var _subItemId = $(this).attr("itemId");
						
							if(_subQuestionId == questionId 
									&& _subItemId == itemId ){
								if(onOff == "on"){
									$(this).attr("disabled",false);
								}else{
									$(this).attr("value","");
									$(this).attr("disabled",true);
								}
							}
						});
					}
					
					if(params.questionType == 'CHECK_SUBJ_RADIO_SUBJ'){
						//RADIO처리를 한다.
						$("input:radio").each(function(){
							var _subType = $(this).attr("itemType");
							var _subQuestionId = $(this).attr("questionId");
							var _subItemId = $(this).attr("itemId");
							var _embededItemId = $(this).attr("embededItemId");
							if(_subQuestionId == questionId 
									&& _subItemId == itemId && _embededItemId != undefined){
								
								if(onOff == "on"){
									$(this).attr("disabled",false);
								}else{
									$(this).removeAttr("checked");
									$(this).attr("disabled",true);
								}
							}
						});
					}
				}
			}else{
				alert(result.msg);
			}
			
		},
		error : function(response, status, err){
			_processingFlag = false;
			alert("네트워크가 불안정합니다. 다시한번 시도해보세요.[" + status + "][" + err + "]");
			
		}
	});	//Ajax로 호출한다.
}


/**
 * 해당 item에 대한 답변이 완료되었음을 표시해준다.
 * @param itemId
 * @param existChild
 */
function checkCompleted(itemId,questionId,isRadio,isObj){
	if(itemId == null) return;
	var existChild = $("#" + itemId +"_submitted").attr("existChild");
	
	$("#question-" + questionId).removeClass("required-question");
	
	if(isObj){
		if(existChild == "true"){
			$("#" + itemId +"_submitted").html("<img src='" + _requestPath + "/resources/images/bt_reply.png'/>");
		}
		$("#complete_flag_" + itemId).val("true");
	}else{
		if(isRadio){
			$("a").each(function(){
				var _questionId = $(this).attr("questionId");
				if(questionId == _questionId){
					$(this).text("");
				}
			});
		}
		if(existChild == "true"){
			$("#" + itemId +"_submitted").html("<img src='" + _requestPath + "/resources/images/bt_reply.png'/>");
		}
		$("#complete_flag_" + questionId).val("true");
	}
}

/**
 * 해당 item에 대한 답변을 안한거로 처리한다. 
 * @param itemId
 * @param questionId
 * @param isObj
 * @returns
 */
function uncheck(itemId,questionId,isCheck){
	$("#" + itemId +"_submitted").text("");
	if(isCheck){
		//해당 question에 속하는 모든 item이 다 check해제가 되었을때에만 false처리한다.
		var canUncheck = true;
		$("input:checkbox").each(function(){
			var _questionId = $(this).attr("questionId");
			if(_questionId == questionId){
				if($(this).is(':checked')){
					canUncheck = false;
				}
			}
		});
		
		if(canUncheck){
			var requiredFlag = $("#complete_flag_" + questionId);
			if(requiredFlag == "true")
				$("#complete_flag_" + questionId).val("false");
		}
		
	}else{
		$("#complete_flag_" + questionId).val("false");
	}
	
	
}

function doNurseCheck(instanceId,questionId,embededItemId){
	var params = {instanceId:instanceId,questionId:questionId};
	
	if(embededItemId != null && embededItemId != undefined ){
		params.embededItemId = embededItemId;
	}

	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + "/nurse/updateNurseCheck",
		timeout : 10000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert("네트워크가 불안정합니다. 다시한번 시도해보세요.[" + status + "][" + err + "]");
			
		}
	});	//Ajax로 호출한다.
	
	
}

//주관식에서 입력한 값이 유효한지 검사한다.
function doValidation(value,validator,minRange,maxRange){
	if(validator == "NONE"){
		return true;
	}
	
	if(value == null || value.length == 0){
		return true;
	}
	
	if(validator == "NUMBER" || validator == "NUMBER_0_9" || validator == "AGE" ||
		validator == "HOUR" || validator == "MINUTE" || validator == "YEAR" || validator == "MONTH" || validator == "HOUR_AM_PM"){
		//숫자인지 검사를 먼저한다.
		if(isNaN(value)){
			alert("숫자만 입력가능합니다.");
			return false;
		}
		
		if(validator == "AGE"){
			//현재 환자의 나이보다 적게 설정가능하게 한다.
			if(_patientAge != "-1"){
				if(value > Number(_patientAge)){
					alert("현재 나이보다 적은 나이만 입력가능합니다.");
					return false;
				}
				if(value <= 0){
					alert("나이는 1세 이상을 입력해야합니다.");
					return false;
				}
			}
		}
		
		if(validator == "NUMBER"){
			if(minRange != null && minRange != undefined && minRange != ""){
				if(Number(value) < Number(minRange)){
					alert("값이 " + minRange + "보다 커야합니다.");
					return false;
				}
			}
			if(maxRange != null && maxRange != undefined && maxRange != ""){
				if(Number(value) > Number(maxRange)){
					alert("값이 " + maxRange + "보다 작아야합니다.");
					return false;
				}
				if(minRange == null || minRange == ""){
					if(Number(value) <= 0){
						alert("값이 0보다 커야합니다.");
						return false;
					}
				}
			}
			
			
		}
		
		if(validator == "NUMBER_0_9"){
			if(value < 0 || value > 9){
				alert("0~9까지만 입력가능합니다.");
				return false;
			}
		}
		if(validator == "HOUR"){
			if(value < 0 || value > 24){
				alert("0~24까지만 입력가능합니다.");
				return false;
			}
		}
		if(validator == "HOUR_AM_PM"){
			if(value < Number(minRange) || value > Number(maxRange)){
				alert(minRange + "~" + maxRange + "까지만 입력가능합니다.");
				return false;
			}
		}
		
		if(validator == "MINUTE"){
			if(value < 0 || value > 59){
				alert("0~59까지만 입력가능합니다.");
				return false;
			}
		}		
		if(validator == "MONTH"){
			if(value < 0 || value > 12){
				alert("0~12까지만 입력가능합니다.");
				return false;
			}
		}		
	}
	
	return true;
}

function wrapWindowByMask(){
	//$.fancybox.helpers.overlay.update();
	
    //화면의 높이와 너비를 구한다.
    var maskHeight = $(document).height();  
    var maskWidth = $(window).width();  

    //마스크의 높이와 너비를 화면 것으로 만들어 전체 화면을 채운다.
    $('#mask').css({'width':maskWidth,'height':maskHeight});  

    //애니메이션 효과
    $('#mask').show();      

}

function hideMask(){
	$('#mask, .window').hide(); 
}