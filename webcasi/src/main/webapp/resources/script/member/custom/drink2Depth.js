function ascComparator(a,b){
	return a-b;
}

function completed(goPre){
	if(goPre){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		
		if(confirm("입력한 사항을 취소하시겠습니까?")){
			parent.complete2Depth(_itemId,_questionId,isRadio,true);
		}
		
		return;
	}
	
	var timer = null;
	
	if(_processingFlag){
		//진행중이면 1초후에 다시 이함수를 호출한다.
		if(timer != null){
			clearTimeout(timer);
		}
		timer = setTimeout(function(){
			completed(goPre);
		},1000);
		
		return;
	}
	
	var canGoNext = true;
	$("input:hidden").each(function(){
		var id = $(this).attr("id");
		if(id.startsWith("complete_flag_")){
			var val = $(this).val();
			if(val == "false"){
				canGoNext = false;
				var inputNeedId = $(this).attr("questionId");
				$("#question-" + inputNeedId).addClass("required-question");
			}
		}
	});
	
	if(complete3DepthFlag == false){
		var drinkCheckArray = [];
		
		$("input:checkbox").each(function(){
			if($(this).is(':checked')){
				var isTab = $(this).attr("tabQuestion");
				if(isTab != undefined){
					var _id = $(this).attr("id");
					drinkCheckArray.push(_id);
				}
			}
		});
		
		drinkOrgArray.sort(ascComparator);
		drinkCheckArray.sort(ascComparator);
		
		if(drinkOrgArray.join(",") != drinkCheckArray.join(",")){
			alert("술 종류의 선택값을 바꾸셨으면 옆에 확인버튼을 눌러서 세부항목을 먼저 설정하셔야합니다.");
			return;
		}
	
	}
	if(canGoNext){
		var isRadio = true;
		parent.complete2Depth(_itemId,_questionId,isRadio);
	}else{
		alert("필수 질문에 답변이 필요합니다.\n붉은색으로 밑줄쳐진 질문에 답변을 하십시요.");
	}
}


$(document).ready(function(){
	$('#second-question-container').jScrollPane();
	
	
	
	
	
});



function showDepth(questionId){
	$("html, body").animate({ scrollTop: 0 }, "fast");
	
	var itemIds = new Array();
	$("input:checkbox").each(function(){
		if($(this).is(':checked')){
			var itemId = $(this).attr("id");
			itemIds.push(itemId);
		}
	});
	
	if(itemIds.length == 0){
		alert("먼저 항목을 체크하세요.");
		hideMask();
		return;
	}
	
	
	wrapWindowByMask();
	$("#third-depth-question-container").show(500);
	
	var frame = $("#third-depth-frame");
	if(_nurseViewFlag){
		frame.attr("src",_requestPath + "/nurse/custom/drink3Depth/" + questionId + "?selectedItemIds=" + itemIds.join(",")  + "&instanceId=" + _instanceId);
		
	}else{
		frame.attr("src",_requestPath + "/member/checkup/custom/drink3Depth/" + questionId + "?selectedItemIds=" + itemIds.join(",")  + "&previewFlag=" + _previewFlag);
		
	}
	
	frame.load(function(){
		
	});
	
}

function hideDepth(itemId){
	//서버에 해당 item에 대한 대답을 clear시켜주어야한다.
	
	close3Depth();
	
}

function close3Depth(){
	hideMask();
	$("#third-depth-question-container").hide();
	if(!_isMobile()){
		$("#third-depth-frame").attr("src","about:blank");
	}
}

function _updateProgress(){
	//dummy
}

function complete3Depth(itemId,questionId,isRadio,goPre){
	complete3DepthFlag = true;
	close3Depth();
	checkCompleted(itemId,questionId,isRadio);
	if(goPre == true){
		rollback(itemId,questionId,isRadio);
	}
}
/**
 * 음주 2Depth에서 rollback은 tab구조에만 해당한다.
 * @param id
 * @param questionId
 * @param isRadio
 */
function rollback(id,questionId,isRadio){
	
	doCancel(id,questionId,"TAB",isRadio);		//eventHandler를 호출한다.
	$("input:checkbox").each(function(){
		var _subQuestionId = $(this).attr('questionId');
		if(_subQuestionId == questionId){
			var _subItemId = $(this).attr("id");
			$("#"+_subItemId).attr("checked",false);
			uncheck(_subItemId,_subQuestionId,true);
		}
	});
}