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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/zipcode.js"></script>
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/protection.css"/>
	
</head>
<body>
	<div id="search-zipcode-form">
		<div class="zipcode-title">우편번호찾기
		</div>
		<div class="zipcode-red-desc">찾고자하는 주소의 동(읍/면/리/가)명을 입력하세요.</div>
		<div class="zipcode-desc">예)서울시 강남구 대치1동이라면 대치1만 입력해주세요.</div>
		<form id="searchForm">
			<div class="zipcode-search-form">
				지역명 : <input type="text" name="dongName" id="dongName" size="15"/> 동(읍/면/리/가)
				<span>
					<a href="javascript:_search();"><img src="${pageContext.request.contextPath}/resources/images/safeguard_bt4.png" class="text-middle"/></a>
				</span>
			</div>
		</form>
		<div class="zipcode-desc">검색후 우편번호를 클릭해주세요.</div>
	</div>
	<div id="zipcodeResult">
		<table id="resultTable" class="zipcode-table">
			<tr class="result-table-header">
				<td class="zipcol-short">우편번호</td>
				<td class="zipcol-long">주소</td>
				<td class="zipcol-short"></td>
			</tr>
			
		</table>
	
	</div>

</body>
</html>