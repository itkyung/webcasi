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

	<div class="content">
		<div class="infoImg">
				<img src="${pageContext.request.contextPath}/resources/assets/images/info02_2.png"/>
		</div>
		<c:url value="${firstUrl}" var="nextUrl"/>
		<div class="home-action">
			<div class="info-button"><a href="${nextUrl}">
			<img src="${pageContext.request.contextPath}/resources/images/page_bt_next.png"/></a></div>
		</div>
	</div>

</body>
</html>