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
				<img src="${pageContext.request.contextPath}/resources/assets/images/info06_main.png"/>
				<span class="dot"></span>
				<li class="mgTop20">수면정밀문진은 수면의 양과 질, 불면증, 주간졸림증 등 수면의 질과 개인이 경험하는 피로의 정도에 대해 평가하게 됩니다.</li>
				<li>작성된 문진표를 바탕으로 수면과 피로 상태에 대해 평가하고 그 결과를 알려드립니다.</li>
			</div>
			<c:url value="${firstUrl}" var="nextUrl"/>
			<div class="home-action">
				<div class="info-button"><a href="${nextUrl}"><img src="${pageContext.request.contextPath}/resources/images/page_bt_start.png"/></a></div>
			</div>
		</div>
	</div>

</body>
</html>