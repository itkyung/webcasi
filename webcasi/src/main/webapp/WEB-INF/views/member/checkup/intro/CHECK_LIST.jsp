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
<script>

$(document).ready(function(){
	var _needRequest = "${needRequest}";
	
	if(_needRequest == "true"){
		$.fancybox.open({
			href : _requestPath + "/member/resultRequestForm",type : 'iframe', 
			openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 720,height : 800
		});
	}
	
});

</script>
</head>
<body>
<div class="main">
		<div class="content">
			<div class="info">
				<img src="${pageContext.request.contextPath}/resources/assets/images/info00_main.png"/>
				<span class="dot"></span>
				<li class="mgTop20">검사를 보다 정확하고 안전하게 진행하기 위해 건진 전 귀하의 몸 상태와 준비 정도를</li>
				<li>확인하는 과정입니다.</li>
				
			</div>
			<c:url value="${firstUrl}" var="nextUrl"/>
			<div class="home-action">
				<div class="info-button"><a href="${nextUrl}"><img src="${pageContext.request.contextPath}/resources/images/page_bt_start.png"/></a></div>
			</div>
		</div>
	</div>

</body>
</html>