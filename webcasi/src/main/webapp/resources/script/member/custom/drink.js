

$(document).ready(function(){
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
});

function clickCompleted(itemId,existChild){
	if(existChild == 'true') showDepth(itemId);
}

function showDepth(itemId){
	$("html, body").animate({ scrollTop: 0 }, "fast");
	wrapWindowByMask();
	$("#second-depth-question-container").show(500);
	
	var secondFrame = $("#second-depth-frame");
	if(_nurseViewFlag){
		secondFrame.attr("src",_requestPath + "/nurse/custom/drink2Depth/" + itemId + "?instanceId=" + _instanceId);
	}else{
		secondFrame.attr("src",_requestPath + "/member/checkup/custom/drink2Depth/" + itemId + "?previewFlag=" + _previewFlag);
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
		_syncQuestionGroup(_questionGroupId, function(){
			syncFlag = true;
			document.location.href = _requestPath + url;
		});
		
	
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