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
</head>
<body>
<div class="main">
		<div class="content">
			<div class="info">
				<img src="${pageContext.request.contextPath}/resources/assets/images/info04_main.png"/>
				<span class="dot"></span>
				<li class="mgTop20">건강문진표는 건강 위험 요소를 파악하여 질병을 예측하고 예방하기 위해 절대적으로 중요한 도구입니다.<br>작성해주신 문진표를 바탕으로 흡연,음주,신체활동 등의 생활습관 상태 등을 평가하고 그 결과를 알려드립니다.</li>
				
				<img class="mgTop10 mgLeft44" src="${pageContext.request.contextPath}/resources/assets/images/info04_graph.png"/>
			</div>
			<c:url value="${firstUrl}" var="nextUrl"/>
			<div class="home-action">
				<div class="info-button"><a href="${nextUrl}"><img src="${pageContext.request.contextPath}/resources/images/page_bt_start.png"/></a></div>
			</div>
		</div>
	</div>
	
	
</body>
</html>