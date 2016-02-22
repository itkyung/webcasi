$(document).ready(function(){
	
	$("input:radio").change(function(){
		processRadioEvent($(this));
	});
	
	$("input:checkbox").change(function(){
		processCheckboxEvent($(this));
	});
	
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
	
	$( "#tabs" ).tabs();
});

function clickCheckboxLink(id){
	if(_nurseViewFlag == "true"){
		return;
	}
	var checkObj = $("#"+id);
	if(!checkObj.is(':checked')){
		checkObj.attr("checked",true);
	}else{
		checkObj.attr("checked",false);
	}
	processCheckboxEvent(checkObj);
}

function processCheckboxEvent($checkObj){
	
	var questionId = $checkObj.attr("questionId");
	
	var onOff = "";
	var questionItemId = $checkObj.attr("id");
	
	var itemType = "CHECK";
	var itemGroup = $checkObj.attr("itemGroup");
	
	if($checkObj.is(':checked')){
		onOff = "on";
		
		
	}else{
		onOff = "off";
		
		
	}
	//서버에 Ajax를 이용해서 현재 질문의 답을 submit한다.
	var params = {questionId:questionId,questionItemId:questionItemId,onOffFlag:onOff,type:itemType};
	if(itemGroup != undefined){
		params.itemGroup = itemGroup;
	}
	
	doSubmit(params,questionItemId,false,onOff,questionId);
	
}

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
	var itemType = "RADIO";	
	
	var questionItemId = $radioObj.attr("id");
	var questionId = $radioObj.attr("questionId");
	var itemGroup = $radioObj.attr("itemGroup");
	var loopCount = $radioObj.attr("loopCount");
	
	//서버에 Ajax를 이용해서 현재 질문의 답을 submit한다.
	var params = {questionId:questionId,questionItemId:questionItemId,type:itemType,itemGroup:itemGroup};
	
	if(loopCount != undefined){
		params.loopCount = loopCount;
	}
	
	doSubmit(params,questionItemId,false,"on",questionId);
	/**
	if(itemGroup == "QUANTITY" && $radioObj.is(':checked')){
		//평균섭취분량일 경우만..
		var itemSortOrder = $radioObj.attr("itemSortOrder");
		var itemTotalCount = $radioObj.attr("itemTotalCount");
		//처음 또는 마지막을 선택했을 경우에만
		if(itemSortOrder == 0){
			changeOtherValue(true,questionId);
		}else if(itemSortOrder == itemTotalCount-1){
			changeOtherValue(false,questionId);
		}
	}
	**/
}

function changeOtherValue(smallFlag,_questionId){
	var radioValueArray = [];
	var radioName = "";
	var needChange = false;
	var selectedSortOrder = -1;
	$("input:radio").each(function(){
		var itemGroup = $(this).attr("itemGroup");
		var questionId = $(this).attr("questionId");
		if(itemGroup == "FREQUENCY" && questionId == _questionId){
			radioName = $(this).attr("name");
			var itemSortOrder = $(this).attr("itemSortOrder");
			radioValueArray[itemSortOrder] = $(this).attr("value");
			var itemTotalCount = $(this).attr("itemTotalCount");
			if( $(this).is(':checked')){
				if( (smallFlag && itemSortOrder > 0) || (!smallFlag && itemSortOrder < itemTotalCount-1) ){
					//SmallFlag값에 따라서 처음과 마지막이 아닌 경우에만 변경한다.
					needChange = true;
					selectedSortOrder = Number(itemSortOrder);
				}
			}
		}
	});
	
	
	if(needChange){
		if(smallFlag){
			selectedSortOrder = selectedSortOrder - 1;
		}else{
			selectedSortOrder = selectedSortOrder + 1;
		}
		var newValue = radioValueArray[selectedSortOrder];
		$("input:radio[name=" + radioName + "][value=" + newValue + "]").click();
		
	}
	
}


function doSubmit(params,itemId,existChild,onOff,questionId){
	if(_nurseViewFlag == "true"){
		return;
	}
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


function completed(needCheck){
	
	var canGoNext = true;
	$("input:hidden").each(function(){
		var id = $(this).attr("id"); 
		if(id.startsWith("complete_flag_")){
			var val = $(this).val();
			if(val == "false"){
				canGoNext = false;
				var inputNeedId = $(this).attr("questionId");
				$("#tab-item-" + inputNeedId).addClass("required-tab-question");
			}
		}
	});
	
	if(!needCheck){
		canGoNext = true;
	}
	if(canGoNext){
		
		var isRadio = _type.startsWith("RADIO") ? true :false;
		parent.complete2Depth(_itemId,_questionId,isRadio,needCheck);
	}else{
		alert("필수 질문에 답변이 필요합니다.\n붉은색 표시 항목에 응답하여 주십시오.");
	}
}



