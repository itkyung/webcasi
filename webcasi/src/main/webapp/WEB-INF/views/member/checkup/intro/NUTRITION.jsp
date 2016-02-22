<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/css/common_static.css"/>	
<title>삼성병원 건강검진</title>
<script>
	var _nextUrl = null;
	
	<c:if test="${nutritionUpdated}">
		_nextUrl = "${nextPageUrl}";
	</c:if>
	
	/* 
	function goSkipNutrition(){
		
		var skipNutrition = $("input[name=skipNutrition]:checked").val();
		
		var params = {skipNutrition :skipNutrition, askCode : 'KEH_45'};
		
		
		$.ajax({
			dataType:  'json', 
			type : 'POST',
			url : _requestPath + "/member/checkup/goSkipNutritionCode/",
			timeout : 8000,
			data : params,
			beforeSubmit : function(){
				
			},				
			success : function(result){
				if(result.success){
					
				}
			},
			error : function(response, status, err){
				alert(err);
				
			}
		});	//Ajax로 호출한다.
		
	}
	*/
	
	function goNext(){
		
		if(_nextUrl == null){
			alert("영양문진 응답여부를 체크해주세요.");
			return;
		}else{
			//goSkipNutrition();
			document.location.href = "${pageContext.request.contextPath}" + _nextUrl;
		}
	}
	
	function clickRadioLink(id){
		var radioObj = $("#"+id);
		if(!radioObj.is(':checked') && !radioObj.is(':disabled')){
			radioObj.attr("checked",true);
			processRadioEvent(radioObj);
		}
	}
	
	function processRadioEvent($radioObj){
		
		if($radioObj.is(':checked')){
			var id = $radioObj.attr("id");
			var flag = "true";
			if(id == "go"){
				flag = "true";
			}else{
				flag = "false";
			}
			var url = "/member/checkup/updateNutritionFlow/" + flag;
			var params = {};
			
			$.ajax({
				dataType:  'json', 
				type : 'POST',
				url : _requestPath + url,
				timeout : 5000,
				data : params,
				beforeSubmit : function(){
					
				},				
				success : function(result){
					var success = result.success;
					if(success){
						_nextUrl = result.nextUrl;
						var progress = result.progressRate;
						_updateProgress(progress);	//Global함수 호출 
						if(flag == "false"){
							//goSkipNutrition();
							//응답을 안할경우 바로 다음으로 이동한다.
							document.location.href = "${pageContext.request.contextPath}" + _nextUrl;
						}
					}else{
						alert(result.msg);
					}
					
				},
				error : function(response, status, err){
					alert(err);
					
				}
			});	//Ajax로 호출한다.
		}
		
	}
	
	$(document).ready(function(){
		$("input:radio").change(function(){
			processRadioEvent($(this));
		});
	});
	
	
</script>

</head>
<body>
<c:url value="${firstUrl}" var="nextUrl"/>

<div class="main">
		<div class="content">
			<div class="info">
				<img class="txt_main" src="${pageContext.request.contextPath}/resources/assets/images/info02_main.png"/>	
				<span class="dot"></span>
				<div class="mgTop10">
					<ul>
						<li class="mgTop15 bold">영양문진 꼭 필요한가요?</li>
						<li class="mgTop10">나쁜 식습관은 암을 비롯한 대부분의 만성질환의 원인으로 잘 알려져 있습니다.
						향후 만성질병 예방을 위한</li>
						<li class="mgLeft5">식사습관 개선에 관심이 있으신 분. 특히, 비만, 당뇨병, 고혈압, 고지혈증, 심혈관질환, 암의 병력 또는 가족력이</li>
						<li class="mgLeft5">있으신 분은 작성하실 것을 권장합니다.</li>
					</ul>
				</div>
				<div class="nurition-benefit-wrapper">
					<ul>
						<li class="mgTop10 bold">◆ 영양문진표 작성 시 제공 받을 수 있는 혜택</li>
	
						<li class="mgTop10 mgLeft10 puple bold">√ 개인별 맞춤 표준 식단<font color="black">(이메일 수령 시에만 해당)</font></li>
						<li class="mgLeft10 puple bold">√ 영양소 부족, 과잉에 따른 개선 방법</li>
						<li class="mgLeft10 puple bold">√ 권장 칼로리 및 영양소별 섭취수준과 권장수준</li>
					</ul>
				</div>
				
				<div class="nurition-radio-wrapper">
				<ul>
					<li class="mgTop10 bold">영양문진표를 작성하시겠습니까?
					<c:choose>
						<c:when test="${nutritionUpdated}">
							<c:choose>
								<c:when test="${skipNutrition}">
									<input type="radio" id="go" name="skipNutrition" style="margin-left:10px;"><a href="javascript:clickRadioLink('go');"> 예</a>
									<input type="radio" id="skip" name="skipNutrition" value="0" class="mgLeft30" style="margin-left:40px;" checked><a href="javascript:clickRadioLink('skip');"> 아니오</a>
								</c:when>
								<c:otherwise>
									<input type="radio" id="go" name="skipNutrition" value="1" style="margin-left:10px;" checked><a href="javascript:clickRadioLink('go');"> 예</a>
									<input type="radio" id="skip" name="skipNutrition" class="mgLeft30" style="margin-left:40px;"><a href="javascript:clickRadioLink('skip');"> 아니오</a>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<input type="radio" id="go" name="skipNutrition" value="99" style="margin-left:10px;"><a href="javascript:clickRadioLink('go');"> 예</a>
							<input type="radio" id="skip" name="skipNutrition" value="99" class="mgLeft30" style="margin-left:40px;"><a href="javascript:clickRadioLink('skip');"> 아니오</a>
						</c:otherwise>
						
					</c:choose>
					</li>		
				</ul>		
				</div>
				<div class="nutrition-graph">
					<img class="mgLeft47 mgTop15" src="${pageContext.request.contextPath}/resources/assets/images/info02_graph.png"/>
				</div>
			</div>
			<c:url value="${firstUrl}" var="nextUrl"/>
			
			<div class="home-action">
				<div class="info-button"><a id="nextBtn" href="javascript:goNext();">
				<img src="${pageContext.request.contextPath}/resources/images/page_bt_start.png"/></a></div>
			</div>
		</div>
	</div>
</div>
	

</body>
</html>