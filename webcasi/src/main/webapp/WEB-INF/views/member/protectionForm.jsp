<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Web CASI</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		var _itemId = "${itemId}";
		var _questionId = "${questionId}";
		var _type = "${type}";
		var _patientAge = "${patientAge}";
	</script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/protection.js"></script>
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/protection2.css"/>
	
</head>
<body id="protection-body">
	<form id="protection-form">
	<input type="hidden" id="protectionId" value="${protection.id}"/>
	<input type="hidden" id="instanceId" value="${protection.instance.id}"/>
	
	<div id="protection-sms">
		<div class="protection-title-row">
			<div class="protection-title">가족 사랑 문자메세지는 수진자가 지정한 보호자에게 건진 진행에 대한<br>메세지가 발송되며, 건진 당일 이후에는 전화 번호가 저장되지 않습니다.</div>
		</div>
		<div class="protection-row">
			<div class="protection-medium-col text-align-middle">
				수진자명 
			</div>
			<div class="protection-medium-col text-align-middle border-left">
				${protection.owner.name}
			</div>
		</div>
		<div class="protection-row">
			<div class="protection-medium-col text-align-middle">
				문자서비스 동의 
			</div>
			<div class="protection-medium-col text-align-middle border-left">
				<c:choose>
					<c:when test="${protection.smsAgree eq true}">
						<input type="checkbox" id="smsAgree" name="smsAgree" checked/>
					</c:when>
					<c:otherwise>
						<input type="checkbox" id="smsAgree" name="smsAgree"/>
					</c:otherwise>
				</c:choose>
				동의합니다.
			</div>
		</div>
		<div class="protection-row">
			<div class="protection-medium-col text-align-middle">
				보호자 연락처 
			</div>
			<div class="protection-medium-col text-align-middle border-left">
				<input type="text" id="parentPhone" name="parentPhone" size=15 value="${protection.parentPhone}"/>&nbsp;&nbsp;(숫자만입력)
			</div>
		</div>
		<div class="protection-row protection-button">
			<a href="javascript:_submit(true,false);"><img src="${pageContext.request.contextPath}/resources/images/safeguard_bt3.png"/></a>
		</div>
	</div>
	<div class="protection-close">
		<a href="javascript:close();"><img src="${pageContext.request.contextPath}/resources/images/safeguard_bt5.png"/></a>
	</div>
	</form>
	
	<div id="confirmDiv">
		<div class="confirm-title">가족사랑 문자발송 신청번호가 맞으십니까?</div>
		<div id="confirm-phone"></div>
		<div>
			<input type="radio" id="yes" name="confirm-flag" onclick="confirmPhone(true);">예 
			<input type="radio" id="no" name="confirm-flag" onclick="confirmPhone(false);">아니오 
		</div>
	</div>
</body>
</html>
