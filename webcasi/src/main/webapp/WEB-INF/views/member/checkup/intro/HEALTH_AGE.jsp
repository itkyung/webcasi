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
				<img src="${pageContext.request.contextPath}/resources/assets/images/info01_main.png"/>
				<span class="dot"></span>
				<ul class="mgTop40 mgLeft20">
					<li class="subTitle"><strong>건강 나이란?</strong></li>
					<li class="mgTop20 pdLeft5">실제  나이에  비해  건강을  위협하는  질병의  위험  요인을  평가한  나이이며</li>
					<li class="pdLeft5">건강에  해로운 나쁜 생활습관을 개선하면 건강 나이가 젊어 질 수 있습니다.</li>
					<li class="mgTop20 pdLeft5"><strong>[나의 건강나이]</strong> 설문은 문진표의 설문과 일부 종합건진 결과를 바탕으로 평가하게 됩니다.</li>
					<li class="pdLeft5">문진표의 응답이 일부 누락될 경우 정확한 평가가 이루어 지지 않습니다.</li>
				</ul>
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