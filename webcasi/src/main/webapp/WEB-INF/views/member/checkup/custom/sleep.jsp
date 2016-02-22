\<%@ page language="java" contentType="text/html; charset=UTF-8"
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
		var _nurseEditable = ${nurseEditable};
		var _instanceId = "${instanceId}";
		var _patientNo = "${patientNo}";
		var _patientAge = "${patientAge}";
		
		var _questionGroupId = "${questionGroup.id}";
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/questionGroup.js"></script>
</head>
<body>

<div id="question-container">
	<div class="question-group-title">
		<div class="title">${questionGroup.title}</div> 

	</div>
	<div class="questions">
		<form>
		<c:forEach var="questionRow" items="${questions}" varStatus="status">
			
			<c:choose> 

				<c:when test="${(questionRow.type eq 'CHECK_SUBJ') || (questionRow.type eq 'CHECK_SUBJ_SUBJ') ||
					(questionRow.type eq 'CHECK_SUBJ_1') || (questionRow.type eq 'CHECK') || (questionRow.type eq 'CHECK_SUBJ_RADIO_SUBJ') 
					|| (questionRow.type eq 'CHECK_VER')}">
					<c:set var="question" value="${questionRow}" scope="request"/>
					<c:set var="questionIndex" value="${status.index}" scope="request"/>
					
					<jsp:include page="/WEB-INF/views/member/checkup/template/checkboxSubj.jsp" />
					<c:remove var="questionIndex" scope="request"/>
					
					<c:remove var="question" scope="request"/>
				</c:when>				
				<c:when test="${questionRow.type eq 'RADIO' || questionRow.type eq 'RADIO_RADIO' || questionRow.type eq 'RADIO_SUBJ_HOUR_MINUTE'
					|| questionRow.type eq 'RADIO_HOR' || questionRow.type eq 'RADIO_SUBJ_1' || questionRow.type eq 'RADIO_IMAGE'}">
					<c:set var="question" value="${questionRow}" scope="request"/>
					<c:set var="questionIndex" value="${status.index}" scope="request"/>
					
					<jsp:include page="/WEB-INF/views/member/checkup/template/radio.jsp" />
					<c:remove var="question" scope="request"/>				
					
					<c:remove var="questionIndex" scope="request"/>
				</c:when>	
				<c:when test="${(questionRow.type eq 'SUBJECTIVE') || (questionRow.type eq 'TEXT_AREA') || (questionRow.type eq 'SUBJECTIVE_YEAR') || 
					(questionRow.type eq 'SUBJECTIVE_YEAR_MONTH_RANGE') || 
					(questionRow.type eq 'SUBJECTIVE_HOUR_MINUTE_RANGE')}">
					<c:set var="question" value="${questionRow}" scope="request"/>
					<c:set var="questionIndex" value="${status.index}" scope="request"/>
					<jsp:include page="/WEB-INF/views/member/checkup/template/subjective.jsp" />
					<c:remove var="question" scope="request"/>				
					<c:remove var="questionIndex" scope="request"/>
				</c:when>	
				<c:when test="${questionRow.type eq 'SUBJECTIVE_HOUR_MINUTE'}">
					<c:set var="question" value="${questionRow}" scope="request"/>
					<c:set var="questionIndex" value="${status.index}" scope="request"/>
					<jsp:include page="/WEB-INF/views/member/checkup/template/subjective_sleep_hour.jsp" />
					<c:remove var="question" scope="request"/>				
					<c:remove var="questionIndex" scope="request"/>
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
	
	<%@ include file="/WEB-INF/views/member/checkup/questionStep.jsp" %>
	
</div>


</body>
</html>