<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>삼성병원 건강검진</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		var _previewFlag = ${previewFlag};
		var _nurseViewFlag = ${nurseViewFlag};
		var _nurseEditable = false;
		var _instanceId = "${instanceId}";
		var _patientNo = "${patientNo}";
		var _patientAge = "${patientAge}";
		
		var _questionGroupId = "${questionGroup.id}";
	</script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/progress.css"/>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<c:choose>
		<c:when test="${previewFlag eq false || nurseEditable eq true}">
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/eventHandler.js"></script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/previewEventHandler.js"></script>
		</c:otherwise>
	</c:choose>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/progress.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/nutrition/calory.js"></script>
	
	<c:if test="${not empty showMsg}">
		<script>
			$(document).ready(function(){
				$("#showAlertMsg").slideDown({duration:600,easing:'linear'});
				
				//$("#showAlertMsg").show({duration:600,easing:'linear'});
				
				//setTimeout(function(){
					//$("#showAlertMsg").slideUp({duration:600,easing:'linear'});
				//},2000);
				
			});
			
			function closeMsg(){
				$("#showAlertMsg").slideUp({duration:600,easing:'linear'});
			};
			
		</script>
	</c:if>
	
</head>
<body>
<div id="showAlertMsg">
	<span>이전에 작성중이던 문진이 있습니다. 이어서 작성하세요.</span>
	<span class="close-btn"><a href="javascript:closeMsg();">닫기</a></span>
</div>
<div id="question-container">
	<div class="question-group-title">
		<div class="title">${questionGroup.title}</div> 
	</div>
	<div class="calory-questions">
		<div style="width:95%;text-align:center;height:35px;">
			<img src="${pageContext.request.contextPath}/resources/images/info02_kcaltitle.png"/>
		</div>	
		<div style="height:30px;width:95%;"></div>
		<div class="calory-desc-wrapper">
			<div class="calory-result">
				<span>1일 평균 섭취 칼로리 : </span><span class="caloryText-color" id="caloryText"></span> <span>칼로리</span>
			</div>
			<div class="calory-desc">
				<div>건진을 마치시고 <span class="underline">키, 체중, 체지방 및 혈액검사, 소변검사 등 결과가 확인되면</span>
				<span class="text-bold">개인별 권장 칼로리</span>를 비롯하여 <span class="text-bold">칼슘, 철분, 엽산, 나트륨과 같은 기타 영양소의</span>
				<span class="text-bold">섭취수준과 권장수준</span>이 분석됩니다.<br>
				분석된 결과를 자세하게 설명한 <span class="text-bold">영양설문결과</span>를 제공 받으실 수 있습니다.</div>
			</div>
		</div>	
		
		<div style="margin-top:30px;height:30px;text-align:center;">
			<img src="${pageContext.request.contextPath}/resources/images/info02_endtext.png"/>
		</div>
		
		<div class="calory-question-wrapper">
			<div class="calory-short-desc">
				<span class="text-bold">응답하시면서</span> 평상시 <span class="underline">월 1회 이상</span>
				 섭취하신 음식 중에 <span class="underline">누락된 항목</span><span class="text-bold"> [예: 김밥, 떡볶이, 튀김(오징어, 야채 등), 돈까스, 탕수육, 볶음밥, 스파게티, 호박죽, 쇠고기죽 등]</span>
				 을 적어주시면 앞으로 반영하겠습니다. 감사합니다.
				 
			</div>
			<div class="short-questions">
				<form>
				<c:forEach var="questionRow" items="${questions}" varStatus="status">
					<c:choose>
						<c:when test="${questionRow.type eq 'TEXT_AREA'}">
							<c:set var="question" value="${questionRow}" scope="request"/>
							<c:set var="noTitle" value="true" scope="request"/>
							<jsp:include page="/WEB-INF/views/member/checkup/template/subjective.jsp" />
							<c:remove var="question" scope="request"/>		
							<c:remove var="noTitle" scope="request"/>		
						</c:when>					
					</c:choose>
				</c:forEach>
				</form>
			</div>
		</div>	
	</div>	
	
	<div id="page-navigation">
		<div id="page-pre">
			<c:choose>
				<c:when test="${empty navigationInfo.preUrl}">
					
				</c:when>
				<c:otherwise>
					<a href="javascript:goPre('${navigationInfo.preUrl}');"><img src="${pageContext.request.contextPath}/resources/images/bt_back.png"/></a>
				</c:otherwise>
			</c:choose>
		</div>
		<div id="page-next">
				<a href="javascript:goNext('${navigationInfo.nextUrl}');"><img src="${pageContext.request.contextPath}/resources/images/bt_next.png"/></a>
			
		</div>
	</div>
</div> 
<div id="question-right">
	<%@ include file="/WEB-INF/views/member/checkup/questionDesc.jsp" %>
	<%@ include file="/WEB-INF/views/member/checkup/nutrition/step.jsp" %>
</div>


</body>
</html>