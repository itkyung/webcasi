$(document).ready(function(){
	
	   // default mode
 //   $('#progress1').anim_progressbar();

    // from second #5 till 15
  //  var iNow = new Date().setTime(new Date().getTime() + 5 * 1000); // now plus 5 secs
  //  var iEnd = new Date().setTime(new Date().getTime() + 10 * 1000); // now plus 15 secs
  //  $('#caloryProgressbar').anim_progressbar({start: iNow, finish: iEnd, interval: 100});

    // we will just set interval of updating to 1 sec
    //$('#progress3').anim_progressbar({interval: 1000});
    
	var params = {};
	var url = "/member/checkup/calculateCalory";
	if(_nurseViewFlag){
		url = "/nurse/calculateCalory";
		params.instanceId = _instanceId;
	}
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + url,
		timeout : 12000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				var calory = result.calory;
				jQuery("#caloryText").text(calory);
				
			}else{
				alert(result.msg);
			}
			
		},
		error : function(response, status, err){
			//alert("네트워크가 불안정합니다. 다시한번 시도해보세요.");
			
		}
	});	//Ajax로 호출한다.
	
	$(window).unload(function(){
		_syncQuestionGroup(_questionGroupId);
	});
	
});


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
			document.location.href = _requestPath + "/member/checkup/questionGroup/" + groupId;
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