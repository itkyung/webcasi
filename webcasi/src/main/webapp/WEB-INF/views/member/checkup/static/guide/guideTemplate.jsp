<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/css/common.css"/>	
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	
<title>삼성병원 건강검진</title>
<script>
	function goGuide(url){
		document.location.href = "${pageContext.request.contextPath}" + url;
	}
</script>
</head>
<body>
<div id="question-container">
	<div class="guide-content">
		<c:choose>
			<c:when test="${empty viewNavigation}">
				<a href="javascript:goGuide('${nextUrl}')">
					<img src="${pageContext.request.contextPath}/resources/assets/images/guide/${guideImage}"/> <!-- 여기만 바꾸면됨-->
				</a>
			</c:when>
			<c:otherwise>
				<img src="${pageContext.request.contextPath}/resources/assets/images/guide/${guideImage}"/> <!-- 여기만 바꾸면됨-->
			</c:otherwise>
		</c:choose>
		
	</div>	
	<c:if test="${viewNavigation eq true}">
		<div id="page-navigation">
			<div id="page-pre">
				<c:if test="${not empty preUrl}">
					<a href="javascript:goGuide('${preUrl}');"><img src="${pageContext.request.contextPath}/resources/images/bt_back.png"/></a>
				</c:if>
			</div>
			<div id="page-next">
				<c:if test="${not empty nextUrl}">
					<a href="javascript:goGuide('${nextUrl}');"><img src="${pageContext.request.contextPath}/resources/images/bt_next.png"/></a>
				</c:if>
			</div>
		</div>
	</c:if>
</div>
<div id="question-right">
	<%@ include file="/WEB-INF/views/member/checkup/static/guide/guideStep.jsp" %>
</div>
</body>
</html>