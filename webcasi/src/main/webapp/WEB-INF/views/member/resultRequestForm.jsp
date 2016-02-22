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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/resultRequest.js"></script>
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/protection.css"/>
	
</head>
<body id="protection-body">
	<div id="resultrequest-image" align="center">
		<img src="${pageContext.request.contextPath}/resources/images/page_smart.jpg"/>
	</div>

	<form id="protection-form">
	<input type="hidden" id="resultRequestId" value="${resultRequest.id}"/>
	<input type="hidden" id="instanceId" value="${resultRequest.instance.id}"/>
	<div id="protection-intro">
		<div class="protection-title">스마트 건진 결과지 수령 안내</div>
		<div class="protection-row">
			<div class="protection-medium-col">
				<div class="protection-small-col">
					건진일 : <fmt:formatDate pattern="yyyy-MM-dd" value="${resultRequest.instance.reserveDate}"/>
				</div>
				<div class="protection-small-col border-left">
					성명 : ${resultRequest.owner.name}
				</div>
			</div>
			<div class="protection-medium-col border-left">
				휴대폰 번호 : <input type="text" id="cellPhone" name="cellPhone" size=15 value="${resultRequest.cellPhone}"/>(숫자만입력)
			</div>
		</div>
		<div class="protection-row">
			<div class="protection-medium-col">
				회사명 : ${resultRequest.companyName}
			</div>
			<div class="protection-medium-col border-left">
				회사 전화번호 : <input type="text" id="companyPhone" name="companyPhone" size="15" value="${resultRequest.companyPhone}"/>(숫자만입력)
			</div>
		</div>
		<div class="protection-row">
			수령 방법 : 
			<c:choose>
				<c:when test="${empty resultRequest.askType}">
					<input type="radio" id="email" name="askType" value="EMAIL"/> 이메일 
					<input type="radio" id="walk" name="askType" value="WALK"/> 방문 
					<input type="radio" id="post" name="askType" value="POST"/> 우편(등기) 
				</c:when>
				<c:when test="${resultRequest.askType eq 'EMAIL'}">
					<input type="radio" id="email" name="askType" value="EMAIL" checked/> 이메일 
					<input type="radio" id="walk" name="askType" value="WALK"/> 방문 
					<input type="radio" id="post" name="askType" value="POST"/> 우편(등기) 
				</c:when>
				<c:when test="${resultRequest.askType eq 'WALK'}">
					<input type="radio" id="email" name="askType" value="EMAIL"/> 이메일 
					<input type="radio" id="walk" name="askType" value="WALK" checked/> 방문 
					<input type="radio" id="post" name="askType" value="POST"/> 우편(등기) 
				</c:when>
				<c:when test="${resultRequest.askType eq 'POST'}">
					<input type="radio" id="email" name="askType" value="EMAIL"/> 이메일 
					<input type="radio" id="walk" name="askType" value="WALK"/> 방문 
					<input type="radio" id="post" name="askType" value="POST" checked/> 우편(등기) 
				</c:when>
			</c:choose>
		</div>
		<div class="protection-row">
			이메일 수령 동의 : 
			<c:choose>
				<c:when test="${resultRequest.emailAgree eq true}">
					<input type="checkbox" id="emailAgree" name="emailAgree" checked/>
				</c:when>
				<c:otherwise>
					<input type="checkbox" id="emailAgree" name="emailAgree"/>
				</c:otherwise>
			</c:choose>
			동의합니다.
		</div>
		<div class="protection-row">
			이메일주소 : 
			<c:set var="prefix" scope="request">
				<c:out value="${emailPrefix}" default=""/>
			</c:set>
			<c:set var="postfix" scope="request">
				<c:out value="${emailPostfix}" default=""/>
			</c:set>
			<input type="text" id="prefix" name="prefix" value="${prefix}" size="10"/> <span>@</span>
			<c:set var="emailDInputClass" value="hidden"/>
			<c:if test="${useDirectEmail eq true}">
				<c:set var="emailDInputClass" value=""/>
			</c:if>
			<span id="emailPostfixInputSpan" class="${emailDInputClass}">
				<input type="text" id="emailPostfixInput" name="emailPostfixInput" value="${postfix}" size="15"/>
			</span>
			<select id="postfix" name="postfix">
				<c:forEach var="row" items="${emailDatas}">
					<c:choose>
						<c:when test="${useDirectEmail eq false && postfix eq row}">
							<option value="${row}" selected>${row}</option>
						</c:when>
						<c:when test="${useDirectEmail eq true && row eq '직접입력'}">
							<option value="${row}" selected>${row}</option>
						</c:when>
						<c:otherwise>
							<option value="${row}">${row}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</div>
		<div class="protection-row">
			<span>결과지 수령 주소 : </span>
			<input type="hidden" id="zipcode" name="zipcode" value="${resultRequest.zipCode}"/>
			<span>
			<a href="javascript:_searchZipcode();"><img src="${pageContext.request.contextPath}/resources/images/safeguard_bt1.png" class="text-middle"/></a>
			</span>
			<input type="text" id="zpAddress" name="zpAddress" size="25" value="${resultRequest.zpAddress}" readonly/>
			<input type="text" id="address" name="address" size="35" value="${resultRequest.address}"/>
		</div>
		<div class="protection-row protection-button">
			<a href="javascript:_submit(false,false);"><img src="${pageContext.request.contextPath}/resources/images/safeguard_bt2.png"/></a>
		</div>
	</div>
	
	<div style="text-align:center;">
		<a href="javascript:close();"><img src="${pageContext.request.contextPath}/resources/images/safeguard_bt5.png"/></a>
	</div>
	</form>
	
	
</body>
</html>
