


$(document).ready(function(){
	if(_isMobileDevice == "false"){
		var browserHeight = $(window).height(); 
		var questionsHeight = browserHeight - 230;
		if(questionsHeight > 600)
			$("#question-container .questions").height(questionsHeight);
		
	}
	
	$('.questions').jScrollPane();
	
	if ( window.addEventListener ){
		window.addEventListener('unload', function(){
			if(syncFlag == false) _syncQuestionGroup(_questionGroupId);
		},false);
	}else{
		window.attachEvent('onunload',function(){
			if(syncFlag == false) _syncQuestionGroup(_questionGroupId);
		});
	}
	
//	$(window).bind('beforeunload', function(){
//		if(syncFlag == false)
//			_syncQuestionGroup(_questionGroupId);
//	});
	
	if(_needRequest == "true"){
		$.fancybox.open({
			href : _requestPath + "/member/resultRequestForm",type : 'iframe', 
			openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 720,height : 800
		});
	}
});

function clickCompleted(itemId,existChild){
	if(existChild == 'true') showDepth(itemId);
}

function showDepth(itemId){
	$("html, body").animate({ scrollTop: 0 }, "fast");
	wrapWindowByMask();
	
	$("#second-depth-question-container").show(500);
	
	var secondFrame = $("#second-depth-frame");
	if(_isMobile()){
		
		//secondFrame.attr("src","about:blank");
	}
	
	if(_nurseViewFlag){
		secondFrame.attr("src",_requestPath + "/nurse/secondDepth/" + itemId + "?instanceId=" + _instanceId);
	}else{
		secondFrame.attr("src",_requestPath + "/member/checkup/secondDepth/" + itemId + "?previewFlag=" + _previewFlag);
	}
	
	secondFrame.load(function(){
		
		
		
	});
	
	
}

function hideDepth(itemId){
	//서버에 해당 item에 대한 대답을 clear시켜주어야한다.
	
	close2Depth();
	
}

function close2Depth(){
	hideMask();
	$("#second-depth-question-container").hide();
	if(!_isMobile()){
		$("#second-depth-frame").attr("src","about:blank");
	}
	
}

/**
 * 2Depth이하의 질문이 완료된 경우에 호출된다.
 * @param itemId
 */
function complete2Depth(itemId,questionId,isRadio,goPre){
	
	close2Depth();
	checkCompleted(itemId,questionId,isRadio);
	if(goPre == true){
		rollback(itemId,questionId,isRadio);
	}
}

function rollback(id,questionId,isRadio){
	var type = $("#"+id).attr("itemType");

	doCancel(id,questionId,type,isRadio);		//eventHandler를 호출한다.

}

function goPre(url){
	_syncQuestionGroup(_questionGroupId,function(){
		syncFlag = true;
		document.location.href = _requestPath + url;
	});
	
	
}

function goNext(url){
	var timer = null;
	
	if(_processingFlag){
		//진행중이면 1초후에 다시 이함수를 호출한다.
		if(timer != null){
			clearTimeout(timer);
		}
		timer = setTimeout(function(){
			goNext(url);
		},1000);
		
		return;
	}
	
	//모든것을 다 했는지 체크해야한다.
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
		_syncQuestionGroup(_questionGroupId,function(){
			syncFlag = true;
			document.location.href = _requestPath + url;
		} );
	
		
	}else{
		
		alert("필수 질문에 답변을 하셔야 이동가능합니다.\n붉은색으로 밑줄쳐진 질문에 답변을 하십시요.");
	}
}

function goQuestionGroup(groupId,sortOrder){
	if(_nurseViewFlag==true || _nurseViewFlag == "true"){
		document.location.href = _requestPath + "/nurse/view/" + _patientNo + "?instanceId=" + _instanceId + "&questionGroupId=" + groupId;
	}else{
	
		var canGoNext = true;
		if(_lastQuestionGroupSortOrder >= sortOrder){
			
		}else{
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
		}
		
		if(canGoNext){
			_syncQuestionGroup(_questionGroupId,function(){
				syncFlag = true;
				document.location.href = _requestPath + "/member/checkup/questionGroup/" + groupId;
			});
		
			
			
		}else{
			alert("필수 질문에 답변을 하셔야 이동가능합니다.\n붉은색으로 밑줄쳐진 질문에 답변을 하십시요.");
		}
	}
}

function viewHelp(questionGroupId){
	$.fancybox.open({
		href : _requestPath + "/member/checkup/help/" + questionGroupId,type : 'iframe', 
		openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 600,height : 500
	});	
}