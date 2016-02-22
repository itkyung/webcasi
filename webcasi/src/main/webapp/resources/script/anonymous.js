var canLogin = false;

$(document).ready(function(){



});

//checkUser = function(){
//	var resno1 = $("#resno1").val();
//	var resno2 = $("#resno2").val();
//	
//	if(resno1.length == 6 && resno2.length == 7){
//		var j_username = resno1 + resno2;
//		$.ajax({
//			dataType:  'json', 
//			type : 'POST',
//			url : _requestPath + '/login/checkUser',
//			timeout : 5000,
//			data : {j_username : j_username},
//			beforeSubmit : function(){
//				
//			},				
//			success : function(result){
//				var needPwInsert = result.needPwInsert;
//				var success = result.success;
//				var errorOccur = result.errorOccur;
//				
//				if(success){
//					canLogin = true;
//					$("#j_username").val(j_username);
//					$("#j_password").attr("disabled",false);
//				}else{
//					if(needPwInsert){
//						$("#j_username").val(j_username);
//						$("#j_password").attr("disabled",false);
//						$("#j_password").focus();
//						passwordSetting();
//					}else if(errorOccur){
//						var msg = result.msg;
//						alert(msg);
//						$("#resno1").val("");
//						$("#resno2").val("");
//					}
//				}
//			},
//			error : function(response, status, err){
//				alert(err);
//				$("#resno1").val("");
//				$("#resno2").val("");
//			}
//		});	//Ajax로 호출한다.
//	}
//};


checkUser = function(){

	var patno = $("#patno").val();
	var name = $("#name").val();
	
	if(patno.length > 0 && name.length > 0){
		var j_username = patno;
		$.ajax({
			dataType:  'json', 
			type : 'POST',
			url : _requestPath + '/login/checkUser',
			timeout : 5000,
			data : {j_username : j_username, patName : name},
			beforeSubmit : function(){
				
			},				
			success : function(result){
				var needPwInsert = result.needPwInsert;
				var success = result.success;
				var errorOccur = result.errorOccur;
				
				if(success){
					canLogin = true;
					$("#j_username").val(j_username);
					$("#name").attr("readonly",true);
					$("#j_password").attr("disabled",false);
				}else{
					if(needPwInsert){
						$("#j_username").val(j_username);
						$("#j_password").attr("disabled",false);
						$("#j_password").focus();
						passwordSetting();
					}else if(errorOccur){
						var msg = result.msg;
						alert(msg);
						$("#j_username").val("");
						$("#patno").val("");
						$("#name").val("");
						$("#name").attr("readonly",false);
					}
				}
			},
			error : function(response, status, err){
				alert(err);
				$("#patno").val("");
				$("#j_username").val("");
				$("#patno").val("");
				$("#name").val("");
				$("#name").attr("readonly",false);
			}
		});	//Ajax로 호출한다.
	}
};

passwordSetting = function(){
	hideLogin();
	
	$.fancybox.open({
		href : "#password-popup",type : 'inline', modal : 'true',
		openEffect : 'elastic',closeEffect : 'elastic', width : 500, height :300,
		helpers : {
			title : {type : 'float'}
			
		}
	});
};

submitPassword = function(){
	var j_username = $("#j_username").val();
	
	var nPassword = $("#nPassword").val();
	var rPassword = $("#rPassword").val();
	
	//TODO 비밀번호 규칙 필요?
	if(nPassword == null || nPassword.length == 0){
		alert("비밀번호를 입력하세요.");
		return;
	}
	
	if(nPassword.length < 4){
		alert("비밀번호를 최소 4자리이상 입력하세요.");
		return;
	}
	
	if(nPassword != rPassword){
		alert("비밀번호가 서로 다릅니다.");
		return;
	}
	
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/login/insertPassword',
		timeout : 5000,
		data : {j_username : j_username, j_password : nPassword},
		beforeSubmit : function(){
			
		},				
		success : function(result){
			if(result.success){
				//alert("비밀번호 설정이 완료되었습니다.로그인하세요.");
				$("#j_password").val(nPassword);
				_login();
				$.fancybox.close();
			}else{
				alert(result.msg);
			}
			
		},
		error : function(response, status, err){
			alert(err);
			
		}
	});	//Ajax로 호출한다.
};

_login = function(){
	
	var loginId = $("#j_username").val();
	
	var password = $("#j_password").val();
	
	if(loginId == null || loginId.length == 0){
		var patno = $("#patno").val();
		if(patno == null || patno.length == 0){
			alert("수진자번호를 입력하세요");
			return;
		}else{
			$("#j_username").val(patno);
		}
	}
	
	if(password == null || password.length == 0){
		alert("비밀번호를 입력하세요.");
		return;
	}
	
	$("#loginForm").submit();
};

searchPatno = function(){
	if(!$("#agree").is(':checked')){
		alert("'개인정보이용동의'에 체크하셔야 수진자번호를 부여 받으실 수 있습니다.");
		return;
	}
	
	var resno1 = $("#resno1").val();
	var resno2 = $("#resno2").val();
	
	if(resno1.length == 6 && resno2.length == 7){
	
		$.ajax({
			dataType:  'json', 
			type : 'POST',
			url : _requestPath + '/login/findPatno',
			timeout : 5000,
			data : {resno : resno1+resno2},
			beforeSubmit : function(){
				
			},				
			success : function(result){
				if(result.success){
					var patno = result.patno;
					alert("수진자번호는 " + patno + "입니다.");
					$.fancybox.close();
				}else{
					alert(result.msg);
				}
				
			},
			error : function(response, status, err){
				alert(err);
				
			}
		});	//Ajax로 호출한다.
	
	}else{
		alert("주민등록번호를 전부 입력하세요.");
	}
	
};