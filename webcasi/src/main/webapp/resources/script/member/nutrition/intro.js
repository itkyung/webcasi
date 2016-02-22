$(document).ready(function(){
	$("input:radio").change(function(){
		var existChild = $(this).attr("existChild");
		if(existChild=="false"){
			//영양설문을 진행하지 않는경우이다.
			alert("영양설문에 응답하지 않으실걸로 체크하셨습니다.");
			//다음버튼의 URL변경.
			//step영역 inactive처리.
			
			
		}
	});
});



function clickCompleted(itemId,existChild){
	if(existChild == 'true') showDepth(itemId);
}

function showDepth(itemId){
	$("html, body").animate({ scrollTop: 0 }, "fast");
	wrapWindowByMask();
	$("#second-depth-question-container").show();
	
	var secondFrame = $("#second-depth-frame");
	secondFrame.attr("src",_requestPath + "/member/checkup/secondDepth/" + itemId);
	
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
	$("#second-depth-frame").attr("src","about:blank");
}

/**
 * 2Depth이하의 질문이 완료된 경우에 호출된다.
 * @param itemId
 */
function complete2Depth(itemId,questionId,isRadio){
	close2Depth();
	checkCompleted(itemId,questionId,isRadio);
}

function goPre(url){
	document.location.href = _requestPath + url;
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
			}
		}
	});
	
	if(canGoNext){
		document.location.href = _requestPath + url;
	}else{
		alert("필수 질문에 답변을 하셔야 이동가능합니다.");
	}
}

function goQuestionGroup(groupId,sortOrder){
	var canGoNext = true;
	if(_lastQuestionGroupSortOrder >= sortOrder){
		
	}else{
		$("input:hidden").each(function(){
			var id = $(this).attr("id");
			if(id.startsWith("complete_flag_")){
				var val = $(this).val();
				if(val == "false"){
					canGoNext = false;
				}
			}
		});
	}
	
	if(canGoNext){
		document.location.href = _requestPath + "/member/checkup/questionGroup/" + groupId;
	}else{
		alert("필수 질문에 답변을 하셔야 이동가능합니다.");
	}
}

function viewHelp(questionGroupId){
	$.fancybox.open({
		href : _requestPath + "/member/checkup/help/" + questionGroupId,type : 'iframe', 
		openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 600,height : 500
	});	
}