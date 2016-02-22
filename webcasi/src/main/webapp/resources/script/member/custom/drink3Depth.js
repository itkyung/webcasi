$(document).ready(function(){
	setTimeout(function(){
		$('#third-question-container').jScrollPane();
	},500);
	
	
	$("input:radio").change(function(){
		processRadioEvent($(this));
	});
	
	
	$("input:text").blur(function(){
		if(_nurseViewFlag == "true") return;
		
		var itemId = $(this).attr("id");
		var	questionId = $(this).attr("questionId");
		
		var questionType = $(this).attr("questionType");
		var validator = $(this).attr("validator");
		var itemGroup = $(this).attr("itemGroup");
		
		var minRange = $(this).attr("minRange");
		var maxRange = $(this).attr("maxRange");
		
		var value = $(this).val();
		if(value == null){
			return;
		}
		
		if(!doValidation(value,validator,minRange,maxRange)){
			$(this).val("");
			return;
		}
		
		var loopCount = $(this).attr("loopCount");
		
		var params = {questionId:questionId,type:questionType,itemGroup:itemGroup,questionItemId:itemId};
		params.strValue = value;
		//params.loopCount = loopCount;
		
		doSubmit(params,itemId,false,"on",questionId);
	});
	
	$( "#tabs" ).tabs();
	
	if(_nurseViewFlag == "true"){
		$("input:checkbox").each(function(){
			var nurseCheck = $(this).attr("nurseCheck");
			if(nurseCheck != true){
				$(this).attr('disabled','disabled');
			}
		});
		$("input:radio").each(function(){
			var nurseCheck = $(this).attr("nurseCheck");
			if(nurseCheck != true){
				$(this).attr('disabled','disabled');
			}
		});
		
		$("input:text").each(function(){
			var nurseCheck = $(this).attr("nurseCheck");
			if(nurseCheck != true){
				$(this).attr('disabled','disabled');
			}
		});
	}
});

function clickRadioLink(id){
	if(_nurseViewFlag == "true"){
		return;
	}
	var radioObj = $("#"+id);
	if(!radioObj.is(':checked')){
		radioObj.attr("checked",true);
		processRadioEvent(radioObj);
	}
}


function processRadioEvent($radioObj){
	if(_nurseViewFlag == "true") return;
	
	var itemType = "RADIO";	
	
	var questionItemId = $radioObj.attr("id");
	var questionId = $radioObj.attr("questionId");
	var itemGroup = $radioObj.attr("itemGroup");
	
	//서버에 Ajax를 이용해서 현재 질문의 답을 submit한다.
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType,itemGroup:itemGroup};
	var loopCount = $radioObj.attr("loopCount");
	params.loopCount = loopCount;
	
	doSubmit(params,questionItemId,false,"on",questionId);
}


//주관식에서 입력한 값이 유효한지 검사한다.
function doValidation(value,validator,minRange,maxRange){
	if(validator == "NONE"){
		return true;
	}
	
	if(validator == "NUMBER" || validator == "NUMBER_0_9" || validator == "AGE" ||
		validator == "HOUR" || validator == "MINUTE" || validator == "YEAR" || validator == "MONTH"){
		//숫자인지 검사를 먼저한다.
		if(isNaN(value)){
			alert("숫자만 입력가능합니다.");
			return false;
		}
		
		if(validator == "NUMBER"){
			if(minRange != null && minRange != undefined && minRange != ""){
				if(value < Number(minRange)){
					alert("값이 " + minRange + "보다 커야합니다.");
					return false;
				}
			}
			if(maxRange != null && maxRange != undefined && maxRange != ""){
				if(value > Number(maxRange)){
					alert("값이 " + maxRange + "보다 작아야합니다.");
					return false;
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
		if(validator == "MINUTE"){
			if(value < 0 || value > 60){
				alert("0~60까지만 입력가능합니다.");
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


function doSubmit(params,itemId,existChild,onOff,questionId){
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/member/checkup/doSubmit',
		timeout : 12000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				var itemGroup = result.itemGroup;
				if(onOff == "on"){
					var $cId = $("#complete_flag_" + questionId  + "_" + itemGroup);
					$cId.val("true");
					$("#tab-item-" + questionId).removeClass("required-tab-question");
					
				}else{
					//$("#complete_flag_"+itemGroup).val("false");
				}
				
				if(params.loopCount != undefined){
					var tabIdx = Number(params.loopCount);
					$( "#tabs" ).tabs("option","active",tabIdx);
				}
				
			}else{
				alert(result.msg);
			}
			
		},
		error : function(response, status, err){
			alert("네트워크가 불안정합니다. 다시한번 시도해보세요.");
			
		}
	});	//Ajax로 호출한다.
}


function completed(goPre){
	if(goPre){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		
		if(confirm("입력한 사항을 취소하시겠습니까?")){
			parent.complete3Depth(_itemId,_questionId,isRadio,true);
		}
		
		return;
	}
	
	var canGoNext = true;
	$("input:hidden").each(function(){
		var id = $(this).attr("id");
		if(id != undefined && id.startsWith("complete_flag_")){
			var val = $(this).val();
			if(val == "false"){
				canGoNext = false;
				var inputNeedId = $(this).attr("questionId");
				$("#tab-item-" + inputNeedId).addClass("required-tab-question");
			}
		}
	});
	
	if(canGoNext){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		parent.complete3Depth(_itemId,_questionId,isRadio);
	}else{
		alert("필수 질문에 답변이 필요합니다.\n붉은색으로 탭에 있는 질문에 답변을 하십시요.");
	}
}
