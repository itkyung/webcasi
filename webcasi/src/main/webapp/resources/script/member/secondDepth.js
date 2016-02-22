$(document).ready(function(){
	$('#second-question-container').jScrollPane();
});

 
function completed(goPre){
	if(_previewFlag || goPre){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		if(goPre){
			if(confirm("입력한 사항을 취소하시겠습니까?")){
				parent.complete2Depth(_itemId,_questionId,isRadio,true);
			}
		}else{
			parent.complete2Depth(_itemId,_questionId,isRadio,goPre);
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
	
	if(canGoNext){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		//alert("complete");
		parent.complete2Depth(_itemId,_questionId,isRadio);
	}else{
		alert("필수 질문에 답변이 필요합니다.\n붉은색으로 밑줄쳐진 질문에 답변을 하십시요.");
	}
}


function showDepth(itemId){
	$("html, body").animate({ scrollTop: 0 }, "fast");
	wrapWindowByMask();
	$("#third-depth-question-container").show(500);
	
	var frame = $("#third-depth-frame");
	
	if(_nurseViewFlag){
		frame.attr("src",_requestPath + "/nurse/thirdDepth/" + itemId + "?instanceId=" + _instanceId);
	}else{
		frame.attr("src",_requestPath + "/member/checkup/thirdDepth/" + itemId + "?previewFlag=" + _previewFlag);
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
	close3Depth();
	checkCompleted(itemId,questionId,isRadio);
	if(goPre == true){
		rollback(itemId,questionId,isRadio);
	}
}

function rollback(id,questionId,isRadio){
	var type = $("#"+id).attr("itemType");
	doCancel(id,questionId,type,isRadio);		//eventHandler를 호출한다.
}

function clickCompleted(itemId,existChild){
	if(existChild == 'true') showDepth(itemId);
}
