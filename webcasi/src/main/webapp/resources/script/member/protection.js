
$(document).ready(function(){
	
	
	if(!$("#smsAgree").is(":checked")){
		$("#parentPhone").attr("disabled",true);
	}
	
	
	$("#smsAgree").change(function(){
		if($(this).is(":checked")){
			$("#parentPhone").attr("disabled",false);
		}else{
			$("#parentPhone").attr("disabled",true);
		}
		
	});
});

function close(){
	parent.closeBox();
}

function confirmPhone(flag){
	if(flag){
		_submit(true,true);
	}else{
		$.fancybox.close();
	}
	
}

function _submit(confirmFlag,reflag){
	var parentPhone = $("#parentPhone").val();
	var smsAgree = false;
	if($("#smsAgree").is(':checked')){
		smsAgree = true;
	}
	
	if(smsAgree && parentPhone.length == 0){
		alert("문자서비스에 동의하실경우에는 보호자연락처를 꼭 입력하셔야합니다.");
		return;
	}
	
	if(smsAgree && isNaN(parentPhone)){
		alert("보호자연락처는 숫자만 입력가능합니다.");
		return;
	}
	
	if(reflag == false && confirmFlag && smsAgree == true){
		$("#confirm-phone").text(parentPhone);
		
		$.fancybox.open({
			href : "#confirmDiv",type : 'inline',modal:true,
			openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 180,height : 120
		});
		
		return;
	}
	var instanceId = $("#instanceId").val();
	var protectionId = $("#protectionId").val();
	
	var params = {instanceId :instanceId,id : protectionId, smsAgree : smsAgree,parentPhone : parentPhone
	};
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + "/member/saveProtection",
		timeout : 8000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			if(result.success){
				$("#protectionId").val(result.id);
				
				if(smsAgree){
					alert("신청이 완료되었습니다.\n감사합니다.");
					$.fancybox.close();
				}else{
					alert("신청이 취소되었습니다.\n감사합니다.");
				}
					
				
			}
		},
		error : function(response, status, err){
			alert(err);
			
		}
	});	//Ajax로 호출한다.
	
	
}