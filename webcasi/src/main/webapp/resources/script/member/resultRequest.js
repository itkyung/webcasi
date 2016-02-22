
$(document).ready(function(){
	
	$("#postfix").change(function(){
		$("#postfix option:selected").each(function(){
			var val = $(this).text();
			if(val == "직접입력"){
				$("#emailPostfixInputSpan").show();
			}else{
				$("#emailPostfixInputSpan").hide();
			}
		});
	});
	
	if($("#email").is(":checked")){
		changePostInfo(true);
	}else{
		changePostInfo(false);
	}
	
	if($("#post").is(":checked")){
		changeEmailInfo(true);
	}else{
		changeEmailInfo(false);
	}
	
	$("input:radio").change(function(){
		var id = $(this).attr("id");
		if($(this).is(":checked")){
			if(id == "email"){
				changeEmailInfo(false);
			}else{
				changeEmailInfo(true);
			}
		}
	});
	
	$("input:radio").change(function(){
		var id = $(this).attr("id");
		if($(this).is(":checked")){
			if(id == "post"){
				changePostInfo(false);
			}else{
				changePostInfo(true);
			}
		}
	});
	
});

function changeEmailInfo(disabled){
	$("#emailAgree").attr("disabled",disabled);
	$("#prefix").attr("disabled",disabled);
	$("#emailPostfixInput").attr("disabled",disabled);
	$("#postfix").attr("disabled",disabled);
}

function changePostInfo(disabled){
	$("#zpAddress").attr("disabled",disabled);
	$("#address").attr("disabled",disabled);
}

function _searchZipcode(){
	if($("#post").is(":checked")){
	
		$.fancybox.open({
			href : _requestPath + "/member/zipCodeForm",type : 'iframe', modal : true,
			openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 500,height : 400
		});
	}else{
		alert("수령방법이 우편일경우에만 선택가능합니다.");
	}
}

//우편번호 검색후에 호출됨.iframe에서 호출됨.
function setZipcode(zipcode,address){
	$("#zipcode").val(zipcode);
	$("#zpAddress").val(address);
	$.fancybox.close();
}

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
	
	var cellPhone = $("#cellPhone").val();
	var companyPhone = $("#companyPhone").val();
	var zipCode = $("#zipCode").val();
	var zpAddress = $("#zpAddress").val();
	var address = $("#address").val();
	var askType = null;
	if($("#email").is(':checked')){
		askType = "EMAIL";
	}
	if($("#walk").is(':checked')){
		askType = "WALK";
	}
	if($("#post").is(':checked')){
		askType = "POST";
	}
	
	var prefix = $("#prefix").val();
	var postfix = null;
	$("#postfix option:selected").each(function(){
		postfix = $(this).text();
	});
	
	var emailPostfixInput = $("#emailPostfixInput").val();
	var emailAgree = false;
	
	if($("#emailAgree").is(':checked')){
		emailAgree = true;
	}
	
	if(confirmFlag == false && emailAgree == false && askType == "EMAIL"){
		alert("이메일로 수령하시려면 이메일 수령에 동의 하셔야합니다.");
		return;
	}
	
	
	
	if(isNaN(cellPhone)){
		alert("휴대폰번호는 숫자만 입력가능합니다.");
		return;
	}
	
	
	if(isNaN(companyPhone)){
		alert("회사전화번호는 숫자만 입력가능합니다.");
		return;
	}
	
	
	if(cellPhone.trim().length == 0){
		alert("휴대폰번호를 입력하세요.");
		return;
	}
	
	if(askType == "POST" && zpAddress.length == 0){
		alert("결과지 수령주소의 우편번호를 검색하세요.");
		return;
	}
	
	if(askType == "POST" && address.length == 0){
		alert("결과지 수령 상세주소를 입력하세요.");
		return;
	}
	
	if(askType == null){
		alert("수령방법을 선택하세요.");
		return;
	}
	
	if(emailAgree == true && askType == "EMAIL" && prefix.length == 0){
		alert("이메일을 입력하세요");
		return;
	}
	
	if(emailAgree == true && askType == "EMAIL" && postfix == "직접입력" && emailPostfixInput.length == 0){
		alert("직접입력을 선택하실 경우에는 이메일 도메인 주소를 직접 입력하셔야합니다.");
		return;
	}
	
	var instanceId = $("#instanceId").val();
	var resultRequestId = $("resultRequestId").val();
	
	var params = {instanceId :instanceId,id : resultRequestId, cellPhone : cellPhone, companyPhone : companyPhone, zipCode : zipCode, zpAddress : zpAddress, address :address,
			askType : askType, prefix : prefix, postfix : postfix, emailPostfixInput : emailPostfixInput, emailAgree : emailAgree
	};
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + "/member/saveResultRequest",
		timeout : 8000,
		data : params,
		beforeSubmit : function(){
			
		},				
		success : function(result){
			if(result.success){
				$("#resultRequestId").val(result.id);
				alert("스마트 건진 결과지 수령 정보가 저장되었습니다.\n감사합니다.");
			}
		},
		error : function(response, status, err){
			alert(err);
		}
	});	//Ajax로 호출한다.
	
	
}