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
				<img src="${pageContext.request.contextPath}/resources/assets/images/info03_main.png"/>
				<span class="dot"></span>
				<li class="mgTop20">마음건강 문진표는 우울 같은 정서적 측면,일상생활에서 경험하게 되는 스트레스, 수면의 질, 직무스트레스, 도박성향과 같은<br>정신건강의 다양한 측면을 평가하게 됩니다.
				작성된 문진표를 바탕으로 마음건강 상태를 평가하고 그 결과를 알려드립니다.</li>
				<img class="mgTop30 mgLeft42" src="${pageContext.request.contextPath}/resources/assets/images/info03_graph.png"/>
			</div>
			<c:url value="${firstUrl}" var="nextUrl"/>
			<div class="home-action">
				<div class="info-button"><a href="${nextUrl}">
				<img src="${pageContext.request.contextPath}/resources/images/page_bt_start.png"/></a></div>
			</div>
		</div>
	</div>
	
</body>
</html>