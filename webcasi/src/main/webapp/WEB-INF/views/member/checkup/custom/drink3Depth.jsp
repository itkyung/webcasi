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
		var _nurseViewFlag="${nurseViewFlag}";
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/custom/drink3Depth.js"></script>
	
	
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	
</head>
<body>
<div class="third-depth-bar">
	<div class="third-depth-title"></div>
	<div class="third-depth-button">
		<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/3depth_bt4.png"/></a>
		<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/3depth_bt1.png"/></a>
	</div>
</div>
<div id="third-question-container">
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
				<c:set var="tabItem" value="${secondInfo.itemInfos[itemIdRow]}" scope="request"/>
		    	<li id="tab-item-${tabItem.questionId}"><a href="#tab${status.count}">${webcasi:substring(tabItem.itemTitle,5)}</a></li>
		    	<c:remove var="tabItem" scope="request"/>
			</c:forEach>
			</ul>
			<c:forEach var="itemIdRow" items="${secondInfo.itemIds}" varStatus="status">
				<c:set var="tabItem" value="${secondInfo.itemInfos[itemIdRow]}" scope="request"/>
				<c:set var="loopCount" value="${status.count}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/custom/drink3Content.jsp" />
				<c:remove var="tabItem" scope="request"/>
				<c:remove var="loopCount" scope="request"/>
			</c:forEach>
		</div>
		<div class="question-short-desc">
		<div>* 술 계량법(음주 양 기입 시 참조하십시오.)</div>
			<div class="drink-weigh">		
				<table>
					<tr>
						<th>술 종류</th>
						<th>1잔당 용량</th>
						<th></th>
					</tr>
					<tr>
						<td class="title">소주1잔</td>
						<td class="title">50cc</td>
						<td class="contents">소주 1병 (350cc) = 7잔, 2병 = 14잔</td>
					</tr>
					<tr>
						<td class="title">맥주1잔</td>
						<td class="title">200cc</td>
						<td class="contents">맥주 1병 (350cc) = 1.7잔</td>
					</tr>
					<tr>
						<td class="title"></td>
						<td class="title"></td>
						<td class="contents">생맥주 1병 (500cc) = 2.5잔</td>
					</tr>
					<tr>
						<td class="title">막걸리 1잔</td>
						<td class="title">250cc</td>
						<td class="contents">막걸리 1되 (1800cc) = 7잔</td>
					</tr>
					<tr>
						<td class="title">정종 1잔</td>
						<td class="title">50cc</td>
						<td class="contents"></td>
					</tr>
					<tr>
						<td class="title">포도주 1잔</td>
						<td class="title">90cc</td>
						<td class="contents">포도주 1병 (750cc) = 8잔</td>
					</tr>
					<tr>
						<td class="title">양주 1잔</td>
						<td class="title">30cc</td>
						<td class="contents">양주 1병 (700cc) = 23잔</td>
					</tr>
					<tr>
						<td class="title">과실주 1잔</td>
						<td class="title">50cc</td>
						<td class="contents"></td>
					</tr>
				</table>
			</div>
		</div>
	</form>
</div>
<div class="bottom-btn">
			<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/3depth_bt4.png"/></a>
		<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/3depth_bt1.png"/></a>
</div>
</body>
</html>