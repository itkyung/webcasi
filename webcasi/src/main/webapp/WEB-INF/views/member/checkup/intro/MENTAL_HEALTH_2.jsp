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
		<div class="guide02">
			<div class="guide02_top">
				<div class="left">
					<img src="${pageContext.request.contextPath}/resources/assets/images/img_guide02_mental.png">
				</div>
				<div class="right">
					<span class="line1">정신건강 <em>결과표의 예</em></span>
					<span class="line2">
						작성해주신 문진표를 바탕으로 불안, 우울, 수면, 직무스트레스,<br/>
						스트레스에 대한 피드백을 드립니다.
					</span>
				</div>
			</div>
			<div class="guide02_main">
				<div class="sub_title">정신건강 결과발표의 예</div>
				<div class="sub_main">
					<div class="sub_1">
						<span>우울/불안</span>
						<img src="${pageContext.request.contextPath}/resources/assets/images/img_guide02_mental_a.png">
					</div>
					<div class="sub_1">
						<span>수면/스트레스</span>
						<img src="${pageContext.request.contextPath}/resources/assets/images/img_guide02_mental_b.png">
					</div>
					<div class="sub_1">
						<span>직무스트레스</span>
						<img src="${pageContext.request.contextPath}/resources/assets/images/img_guide02_mental_c.png">
					</div>
				</div>
			</div>
			<c:url value="${firstUrl}" var="nextUrl"/>
			<div class="home-action">
				<div class="info-button"><a href="${nextUrl}"><img src="${pageContext.request.contextPath}/resources/images/bt_next_02.png"/></a></div>
			</div>
		</div>
	</div>

</body>
</html>