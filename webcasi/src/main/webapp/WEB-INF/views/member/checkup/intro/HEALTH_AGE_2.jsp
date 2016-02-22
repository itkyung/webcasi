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
        <div class="titlePage">
            <div class="img01"><img src="${pageContext.request.contextPath}/resources/assets/images/titlepage_3_txt.png"></div>
            <div class="text01">
                <span class="t1">건강 나이란 ?</span>
                <span class="t2">
                실제 나이에 비해 건강을 위협하는 질병의 후험 요일을 평가한 나이이며<br/>
                건강에 해로운 나쁜 생활습관을 계산하면 건강 나이가 젊어 질 수 있습니다.<br/><br/>

                <em>[나의 건강나이]</em> 설문은 문진표의 설문과 일부 종합검진 결과를 바탕으로 평가하게 됩니다.<br/>
                문진표의 응답이 일부 누락될 경우 정확한 평가가 이루어 지지 않습니다.
                </span>
            </div>
        </div>
            <c:url value="${firstUrl}" var="nextUrl"/>
            <div class="home-action">
                <div class="info-button"><a href="${nextUrl}"><img src="${pageContext.request.contextPath}/resources/images/bt_next_02.png"/></a></div>
            </div>
    </div>

	
</body>
</html>