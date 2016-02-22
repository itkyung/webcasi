var noneCheck = false;


$(document).ready(function(){

	
	$('.questions').jScrollPane();
	
	
	$("input:checkbox").change(function(){
		processCheckboxEvent($(this));
	});
	
	$("input:checkbox").each(function(){
		var noneFlag = $(this).attr("noneFlag");
		if(noneFlag == "true" && $(this).is(':checked')){
			noneCheck = true;
		}
	});
	
	if(noneCheck){
		$("input:checkbox").each(function(){
			var noneFlag = $(this).attr("noneFlag");
			if(noneFlag != "true"){
				$(this).removeAttr("checked");
				$(this).attr("disabled",true);
			}
		});
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
	
	
	if(_nurseViewFlag){
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
	
	if(_needRequest == "true"){
		$.fancybox.open({
			href : _requestPath + "/member/resultRequestForm",type : 'iframe', 
			openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 720,height : 800
		});
	}
});

function clickCheckboxLink(id){
	if(_nurseViewFlag == "true"){
		return;
	}
	var checkObj = $("#"+id);
	if(checkObj.is(':disabled')){
		return;
	}
	
	if(!checkObj.is(':checked')){
		checkObj.attr("checked",true);
	}else{
		checkObj.attr("checked",false);
	}
	processCheckboxEvent(checkObj);
}

function processCheckboxEvent($checkObj){
	var toggleFlag = "";
	var questionItemId = $checkObj.attr("id");
	var noneFlag = $checkObj.attr("noneFlag");
	
	if(noneFlag == "true"){
		if($checkObj.is(':checked')){
			toggleFlag = "true";
			//다른 값들을 다 check해제하고 disable처리한다.
			doToggle(questionItemId,true);
			noneCheck = true;
		}else{
			toggleFlag = "false";
			noneCheck = false;
			//다른 값들을 enable처리한다.
			doToggle(questionItemId,false);
		}
		//서버에 Ajax를 이용해서 현재 질문의 답을 submit한다.
		var params = {questionItemId:questionItemId,toggleFlag:toggleFlag};
		doSubmit(params,questionItemId);
	}
}

function doToggle(questionItemId,disable){
	$("input:checkbox").each(function(){
		var itemId = $(this).attr("id");
		if(questionItemId != itemId){
			if(disable){
				$(this).removeAttr("checked");
				$(this).attr("disabled",true);
			}else{
				$(this).removeAttr("disabled");
			}
		}
	});
}


function doSubmit(params,itemId){
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		async : false,
		url : _requestPath + '/member/checkup/nutrition/toggleNoneFlag',
		timeout : 12000,
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
			alert("네트워크가 불안정합니다. 다시한번 시도해보세요.");
			
		}
	});	//Ajax로 호출한다.
}

function clickCompleted(itemId,existChild){
	if(existChild == 'true') showDepth(itemId);
}

function showDepth(){
	
	if(noneCheck){
		_syncQuestionGroup(_questionGroupId,function(){
			syncFlag = true;
			document.location.href = _requestPath + _nextUrl;
		});
		
//		setTimeout(function(){
//			//다음페이지로 이동한다.
//			document.location.href = _requestPath + _nextUrl;
//		},100);
		return;
	}
	
	$("html, body").animate({ scrollTop: 0 }, "fast");
	wrapWindowByMask();
	
	
	var questionId;
	var itemIds = new Array();
	$("input:checkbox").each(function(){
		if($(this).is(':checked')){
			var itemId = $(this).attr("id");
			itemIds.push(itemId);
			questionId = $(this).attr("questionId");
		}
	});
	
	if(itemIds.length == 0){
		alert("먼저 항목을 체크하세요.");
		hideMask();
		return;
	}
	
	$("#second-depth-question-container").show(500);
	
	var secondFrame = $("#second-depth-frame");
	
	if(_nurseViewFlag){
		secondFrame.attr("src",_requestPath + "/nurse/nutrition/secondDepth/" + questionId + "?selectedItemIds=" + itemIds.join(",") + "&instanceId=" + _instanceId);
	}else{
		secondFrame.attr("src",_requestPath + "/member/checkup/nutrition/secondDepth/" + questionId + "?selectedItemIds=" + itemIds.join(","));
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
	//갤럭시 노트에서만 이것을 안하게 해야할듯...
	if(!_isMobile()){
		$("#second-depth-frame").attr("src","about:blank");
	}
}

/**
 * 2Depth이하의 질문이 완료된 경우에 호출된다.
 * @param itemId
 */
function complete2Depth(itemId,questionId,isRadio,goNext){
	
	close2Depth();
	checkCompleted(itemId,questionId,isRadio);
	if(goNext){
		
		_syncQuestionGroup(_questionGroupId);
		syncFlag = true;
		
		setTimeout(function(){
			//다음페이지로 이동한다.
			document.location.href = _requestPath + _nextUrl;
		},100);
		
	}
}

function goPre(url){
	_syncQuestionGroup(_questionGroupId);
	syncFlag = true;
	
	setTimeout(function(){
		//다음페이지로 이동한다.
		document.location.href = _requestPath + url;
	},100);
	
	
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
		_syncQuestionGroup(_questionGroupId);
		syncFlag = true;
		setTimeout(function(){
			//다음페이지로 이동한다.
			document.location.href = _requestPath + url;
		},100);

	}else{
		alert("필수 질문에 답변을 하셔야 이동가능합니다.");
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
					}
				}
			});
		}
		
		if(canGoNext){
			_syncQuestionGroup(_questionGroupId);
			syncFlag = true;
			setTimeout(function(){
				//다음페이지로 이동한다.
				document.location.href = _requestPath + "/member/checkup/questionGroup/" + groupId;
			},100);

		}else{
			alert("필수 질문에 답변을 하셔야 이동가능합니다.");
		}
	}
}

function viewHelp(questionGroupId){
	$.fancybox.open({
		href : _requestPath + "/member/checkup/help/" + questionGroupId,type : 'iframe', 
		openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 600,height : 500
	});	
}


function wrapWindowByMask(){
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

function checkCompleted(){
	//TODO 
}