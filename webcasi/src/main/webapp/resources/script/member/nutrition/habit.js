

$(document).ready(function(){
	if(_isMobileDevice == "false"){
		var browserHeight = $(window).height(); 
		var questionsHeight = browserHeight - 230;
		if(questionsHeight > 600)
			$("#question-container .questions").height(questionsHeight);
		
	}
	
	if ( window.addEventListener ){
		window.addEventListener('unload', function(){
			if(syncFlag == false) _syncQuestionGroup(_questionGroupId);
		},false);
	}else{
		window.attachEvent('onunload',function(){
			if(syncFlag == false) _syncQuestionGroup(_questionGroupId);
		});
	}
	
	
	setTimeout(function(){
		$('.questions').jScrollPane();
	},500);
	
});

function goPre(url){
	_syncQuestionGroup(_questionGroupId,function(){
		syncFlag = true;
		document.location.href = _requestPath + url;
	});
	
}

function goNext(url){
	//모든것을 다 했는지 체크해야한다.
	var canGoNext = true;
	$("input:hidden").each(function(){
		var id = $(this).attr("id");
		if(id.startsWith("complete_flag_")){
			var val = $(this).val();
			if(val == "false"){
				canGoNext = false;
				var inputNeedId = $(this).attr("questionId");
				var tabFlag = $(this).attr("tab");
				if(tabFlag == "true"){
					$("#tab-item-" + inputNeedId).addClass("required-tab-question");
				}else{
					$("#question-" + inputNeedId).addClass("required-question");
				}
			}
		}
	});
	
	if(canGoNext){
		_syncQuestionGroup(_questionGroupId,function(){
			syncFlag = true;
			document.location.href = _requestPath + url;
		});
			
	}else{
		alert("필수 질문에 답변을 하셔야 이동가능합니다.\n붉은색 표시 항목에 응답하여 주십시오.");
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
						var tabFlag = $(this).attr("tab");
						if(tabFlag == "true"){
							$("#tab-item-" + inputNeedId).addClass("required-tab-question");
						}else{
							$("#question-" + inputNeedId).addClass("required-question");
						}
						
					}
				}
			});
		}
		
		if(canGoNext){
			_syncQuestionGroup(_questionGroupId, function(){
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