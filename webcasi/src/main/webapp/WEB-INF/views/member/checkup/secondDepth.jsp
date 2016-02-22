<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Web CASI</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		var _itemId = "${itemId}";
		var _questionId = "${questionId}";
		var _type = "${type}";
		var _previewFlag = ${previewFlag};
		var _nurseViewFlag = ${nurseViewFlag};
		var _nurseEditable = ${nurseEditable};
		var _instanceId = "${instanceId}";
		var _patientAge = "${patientAge}";
		
	</script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jquery.jscrollpane.css" media="all" />
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.mousewheel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.jscrollpane.min.js"></script>
	
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	
	<c:choose>
		<c:when test="${previewFlag eq false || nurseEditable eq true}">
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/eventHandler.js"></script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/previewEventHandler.js"></script>
		</c:otherwise>
	</c:choose>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/secondDepth.js"></script>
</head>
<body id="second-body">
<div class="second-depth-bar">
	<div class="second-depth-title"></div>
	<div class="second-depth-button">
		<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt4.png"/></a>
		<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt1.png"/></a>
	</div>
</div>
<div id="second-question-container">
	<form>
	<c:forEach var="questionRow" items="${questions}" varStatus="status">
		<c:choose>

			<c:when test="${(questionRow.type eq 'CHECK_SUBJ') || (questionRow.type eq 'CHECK_SUBJ_SUBJ') ||
				(questionRow.type eq 'CHECK_SUBJ_1') || (questionRow.type eq 'CHECK') || (questionRow.type eq 'CHECK_SUBJ_RADIO_SUBJ') 
				|| (questionRow.type eq 'CHECK_VER')}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/checkboxSubj.jsp" />
				<c:remove var="question" scope="request"/>
			</c:when>			
			<c:when test="${questionRow.type eq 'RADIO' || questionRow.type eq 'RADIO_RADIO' || questionRow.type eq 'RADIO_SUBJ_HOUR_MINUTE'
				|| questionRow.type eq 'RADIO_HOR'  || questionRow.type eq 'RADIO_SUBJ_1'}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<c:set var="questionIndex" value="${status.index}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/radio.jsp" />
				<c:remove var="question" scope="request"/>			
				<c:remove var="questionIndex" scope="request"/>			
			</c:when>
			<c:when test="${(questionRow.type eq 'SUBJECTIVE') || (questionRow.type eq 'TEXT_AREA') || (questionRow.type eq 'SUBJECTIVE_YEAR') || 
					(questionRow.type eq 'SUBJECTIVE_HOUR_MINUTE') || (questionRow.type eq 'SUBJECTIVE_YEAR_MONTH_RANGE') || 
					(questionRow.type eq 'SUBJECTIVE_HOUR_MINUTE_RANGE')}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/subjective.jsp" />
				<c:remove var="question" scope="request"/>					
			</c:when>				
			<c:when test="${(questionRow.type eq 'SUBJECTIVE_YEAR_MONTH') || (questionRow.type eq 'SUBJECTIVE_YEAR_MONTH_DAY')}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/subjectiveDate.jsp" />
				<c:remove var="question" scope="request"/>		
			</c:when>			
			<c:when test="${(questionRow.type eq 'OBJ_RADIO') || (questionRow.type eq 'OBJ_RADIO_SUBJ')}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/objective.jsp" />
				<c:remove var="question" scope="request"/>		
			</c:when>				
		</c:choose>
		<c:if test="${nurseViewFlag eq true}">
				<c:choose>
					<c:when test="${questionRow.nurseQuestionType eq 'RADIO' || questionRow.nurseQuestionType eq 'RADIO_HOR' }">
						<c:set var="question" value="${questionRow}" scope="request"/>
						<c:set var="questionIndex" value="${status.index}" scope="request"/>
						<c:set var="questionNo" value="${status.count}" scope="request"/>
						<jsp:include page="/WEB-INF/views/nurse/template/radio.jsp" />
						<c:remove var="questionIndex" scope="request"/>
						<c:remove var="questionNo" scope="request"/>
						<c:remove var="question" scope="request"/>
					</c:when>
					<c:when test="${questionRow.nurseQuestionType eq 'CHECK'}">
						<c:set var="question" value="${questionRow}" scope="request"/>
						<c:set var="questionIndex" value="${status.index}" scope="request"/>
						<c:set var="questionNo" value="${status.count}" scope="request"/>
						<jsp:include page="/WEB-INF/views/nurse/template/checkboxSubj.jsp" />
						<c:remove var="questionIndex" scope="request"/>
						<c:remove var="questionNo" scope="request"/>
						<c:remove var="question" scope="request"/>
					</c:when>	
				</c:choose>
			</c:if>
	</c:forEach>
	</form>
</div>
<div class="bottom-btn">
	<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt4.png"/></a>
	<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt1.png"/></a>
</div>
<div id="third-depth-question-container" class="hidden">
	<iframe id="third-depth-frame" name="third-depth-frame" marginwidth="0" marginheight="0" frameborder="0" style="width:750px;height:500px;"></iframe>
</div>
<div id="mask"></div>
</body>
</html>