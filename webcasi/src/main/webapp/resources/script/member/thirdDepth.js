$(document).ready(function(){
	$('#third-question-container').jScrollPane();
});

function completed(goPre){
	if(_previewFlag || goPre){
		var isRadio = _type.startsWith("RADIO") ? true :false;
		if(goPre){
			if(confirm("입력한 사항을 취소하시겠습니까?")){
				parent.complete3Depth(_itemId,_questionId,isRadio,true);
			}
		}else{
			parent.complete3Depth(_itemId,_questionId,isRadio,goPre);
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
		parent.complete3Depth(_itemId,_questionId,isRadio);
	}else{
		alert("필수 질문에 답변이 필요합니다.\n붉은색으로 밑줄쳐진 질문에 답변을 하십시요.");
	}
}


function showDepth(itemId){
	
	
}

function hideDepth(itemId){
	
	
}

function close3Depth(){
	
}

function _updateProgress(){
	//dummy
}