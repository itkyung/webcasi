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
	</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<c:choose>
		<c:when test="${previewFlag eq false || nurseEditable eq true}">
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/eventHandler.js"></script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/previewEventHandler.js"></script>
		</c:otherwise>
	</c:choose>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/nutrition/intro.js"></script>
</head>
<body>

<div id="question-container">
	<div class="question-group-title"><span>${questionGroup.title}</span> </div>
	<div id="nutrition-desc">
		<ul>
			<li>다음은 영양설문입니다.</li>
			<li></li>
			<li>응답을 마치시면 지난 1년동안의 평균 1일 섭취 칼로리를 즉시 분석해드립니다.</li>
			<li></li>
			<li>또한 건강검진을 마치신 후에는</li>
			<li>${webcasi:currentUser().name}님의 키,체중,체지방 및 혈액검사 등 검사결과를 확인한후</li>
			<li>칼로리 및 기타 영양소의 권장수준을 분석하여 다음과 같은 영양설문결과표를 제공해드리겠습니다.</li>
			<li></li>
			<li>본 영양설문은 한식 위주로 구성되어있습니다.</li>
		</ul>
	</div>
	<div class="questions">
		<form>
		<c:forEach var="questionRow" items="${questions}" varStatus="status">
			<c:choose>
				<c:when test="${questionRow.type eq 'RADIO' || questionRow.type eq 'RADIO_RADIO' || questionRow.type eq 'RADIO_SUBJ_HOUR_MINUTE'
					|| questionRow.type eq 'RADIO_HOR' || questionRow.type eq 'RADIO_SUBJ_1' || questionRow.type eq 'RADIO_IMAGE'}">
					<c:set var="question" value="${questionRow}" scope="request"/>
					<jsp:include page="/WEB-INF/views/member/checkup/template/radio.jsp" />
					<c:remove var="question" scope="request"/>					
				</c:when>	
			</c:choose>
		</c:forEach>
		</form>
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
			<c:choose>
				<c:when test="${empty navigationInfo.nextUrl}">
					
				</c:when>
				<c:otherwise>
					<a href="javascript:goNext('${navigationInfo.nextUrl}');"><img src="${pageContext.request.contextPath}/resources/images/bt_next.png"/></a>
				</c:otherwise>
			</c:choose>
			
		</div>
	</div>
</div> 
<div id="question-right">
	<%@ include file="/WEB-INF/views/member/checkup/questionDesc.jsp" %>
	<%@ include file="/WEB-INF/views/member/checkup/nutrition/step.jsp" %>
</div>


</body>
</html>