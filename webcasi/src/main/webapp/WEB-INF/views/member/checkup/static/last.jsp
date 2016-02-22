<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/css/common_static.css"/>	
<title>삼성병원 건강검진</title>
</head>
<body>
<div class="main">
	<div class="content">
		<img src="${pageContext.request.contextPath}/resources/assets/images/img_endPage.png"/>
		<div class="bottom_text2">
			<span>문진 응답은 자동으로 저장되며, 귀하의 검진 당일까지 언제 어디서나</span>
			<span>로그인을 통해 수정 가능합니다.</span>
		</div>
		<c:url value="/logout" var="logoutUrl"/>
		<div class="home-action">
			<div class="info-button"><a href="${logoutUrl}"><img src="${pageContext.request.contextPath}/resources/assets/images/bt_end_1.png"/></a></div>
		</div>	
	</div>
		
</div>
	
</body>
</html>