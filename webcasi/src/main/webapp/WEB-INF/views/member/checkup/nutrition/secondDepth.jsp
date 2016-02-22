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
		var _patientAge = "${patientAge}";
		var _nurseViewFlag = "${nurseViewFlag}";
	</script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/nutrition/secondDepth.js"></script>
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	
</head>
<body id="second-body">
<div class="second-depth-bar">
	<div class="nutrition-second-depth-title"></div>
	<div class="nutrition-second-depth-button">
	<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt3.png"/></a>
	<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt2.png"/></a>
	</div>
	
</div>
<div id="second-question-container">
	<form>
		<div class="question">
			<div class="question-title">
				<span>${secondInfo.topQuestionTitle}</span>
			</div>
		<!--  Tab영역 -->
		</div>
		<div id="tabs">
			<ul class="tabs">
			<c:forEach var="itemIdRow" items="${secondInfo.itemIds}" varStatus="status">
				<c:set var="nutritionItem" value="${secondInfo.itemInfos[itemIdRow]}" scope="request"/>
		    	<li id="tab-item-${nutritionItem.questionId}"><a href="#tab${status.count}">${webcasi:substring(nutritionItem.itemTitle,5)}</a></li>
		    	<c:remove var="nutritionItem" scope="request"/>
			</c:forEach>
			</ul>
			<c:forEach var="itemIdRow" items="${secondInfo.itemIds}" varStatus="status">
			
				<c:set var="nutritionItem" value="${secondInfo.itemInfos[itemIdRow]}" scope="request"/>
				<c:set var="loopCount" value="${status.count}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/nutrition/secondContent.jsp" />
				<c:remove var="nutritionItem" scope="request"/>
				<c:remove var="loopCount" scope="request"/>
			</c:forEach>
		</div>
	</form>
</div>
<div class="bottom-btn">
	<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt3.png"/></a>
	<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt2.png"/></a>
</div>
</body>
</html>