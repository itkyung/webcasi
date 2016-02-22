<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:url value="${nextPage}" var="nextUrl"/>

<html>
<head>
	<title>삼성병원문진</title>
	<script>
		setTimeout(function(){
			document.location.href = "${pageContext.request.contextPath}${nextPage}";
		},2000);
	</script>
</head>
<body>
	<div style="margin : 0 auto;text-align:center;">
		<a href="${pageContext.request.contextPath}${nextPage}">
		<c:choose>
			<c:when test="${_isMobile eq true}">
				<img src="${pageContext.request.contextPath}/resources/images/intro_loading.jpg"/>
			</c:when>
			<c:otherwise>
				<img src="${pageContext.request.contextPath}/resources/images/intro_loading_big.jpg"/>
			</c:otherwise>
		</c:choose>
		</a>
	</div>
</body>
</html>