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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<c:choose>
		<c:when test="${previewFlag eq false || nurseEditable eq true}">
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/eventHandler.js"></script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/previewEventHandler.js"></script>
		</c:otherwise>
	</c:choose>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/custom/body.js"></script>
</head>
<body>

<div id="question-container">
	<div class="question-group-title"><span>${questionGroup.title}</span> </div>
	
	<div class="questions">
		<div class="body-wrapper">
			<div class="body-left">
				<c:forEach var="questionRow" items="${questions}" varStatus="status">
					<c:set var="question" value="${questionRow}" scope="request"/>
					<c:set var="loopCount" value="${status.count}" scope="request"/>
					<jsp:include page="/WEB-INF/views/member/checkup/custom/bodyContent.jsp" />
					<c:remove var="question" scope="request"/>
					<c:remove var="loopCount" scope="request"/>
				</c:forEach>
			</div>
			<div class="body-right">
				<img id="body-img" src="${pageContext.request.contextPath}/resources/images/body/body.gif"/>
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