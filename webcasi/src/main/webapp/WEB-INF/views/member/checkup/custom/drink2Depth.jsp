<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>삼성병원 건강검진</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		var _itemId = "${itemId}";
		var _questionId = "${questionId}";
		var _type = "${type}";
		var _nurseViewFlag=${nurseViewFlag};
		var _nurseEditable = false;
		var _previewFlag = ${previewFlag};
		var _instanceId = "${instanceId}";
		var _patientAge = "${patientAge}";
		var drinkOrgArray = [];
		var complete3DepthFlag = false;
	</script>
	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jquery.jscrollpane.css" media="all" />
		
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.mousewheel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.jscrollpane.min.js"></script>
	
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	
	<c:choose>
		<c:when test="${previewFlag eq false || nurseEditable eq true}">
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/eventHandler.js"></script>
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/admin/previewEventHandler.js"></script>
		</c:otherwise>
	</c:choose>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/custom/drink2Depth.js"></script>
</head>
<body id="second-body">
<div class="second-depth-bar">
	<div class="second-depth-title"></div>
	<div class="second-depth-button">
		<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt4.png"/></a>
		<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt1.png"/></a>
	</div>
</div>
<div id="second-question-container">
	
<form>
	<c:forEach var="questionRow" items="${questions}" varStatus="status">
		<c:choose>		
			<c:when test="${questionRow.type eq 'RADIO' || questionRow.type eq 'RADIO_RADIO' || questionRow.type eq 'RADIO_SUBJ_HOUR_MINUTE'
				|| questionRow.type eq 'RADIO_HOR'  || questionRow.type eq 'RADIO_SUBJ_1'}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<c:set var="questionIndex" value="${status.index}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/radio.jsp" />
				<c:remove var="question" scope="request"/>			
				<c:remove var="questionIndex" scope="request"/>			
			</c:when>
			<c:when test="${(questionRow.type eq 'SUBJECTIVE') || (questionRow.type eq 'TEXT_AREA') || (questionRow.type eq 'SUBJECTIVE_YEAR') || 
					(questionRow.type eq 'SUBJECTIVE_HOUR_MINUTE') || (questionRow.type eq 'SUBJECTIVE_YEAR_MONTH_RANGE')}">
				<c:set var="question" value="${questionRow}" scope="request"/>
				<jsp:include page="/WEB-INF/views/member/checkup/template/subjective.jsp" />
				<c:remove var="question" scope="request"/>					
			</c:when>				
			<c:when test="${questionRow.type eq 'TAB'}">
				<!-- <div class="question-short-desc">
					리스트 되어있는 술 중에서 1년동안 드신 술을 체크하시고 확인버튼을 눌러주세요.
				</div> -->
				<div class="tab-wrapper">

					<c:set var="question" value="${questionRow}" scope="request"/>
				
					<c:set var="tabQuestion" value="true" scope="request"/>
					<jsp:include page="/WEB-INF/views/member/checkup/template/checkboxSubj.jsp" />
					<c:set var="resultItems" value="${resultMap[question.id]}"/>
					<c:forEach var="item" items="${question.childItems}">
						<c:set var="innerItem" value="${resultItems[item.id]}"/>
						<c:if test="${!empty innerItem}">
							<script>
								drinkOrgArray.push('${item.id}');
							</script>
						</c:if>
					</c:forEach>
					<c:remove var="question" scope="request"/>
					
					<c:remove var="tabQuestion" scope="request"/>
					<div id="drink-button">
						<a href="javascript:showDepth('${questionRow.id}');"><img src="${pageContext.request.contextPath}/resources/images/bt_ok.png"/></a>
						
					</div>
				</div>
			</c:when>
		</c:choose>
	</c:forEach>
	</form>
</div>
<div class="bottom-btn">
	<a href="javascript:completed(true);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt4.png"/></a>
	<a href="javascript:completed(false);"><img src="${pageContext.request.contextPath}/resources/images/2depth_bt1.png"/></a>
	
</div>
<div id="third-depth-question-container" class="hidden">
	<iframe id="third-depth-frame" name="third-depth-frame" marginwidth="0" marginheight="0" frameborder="0" style="width:750px;height:500px;"></iframe>
</div>
<div id="mask"></div>
</body>
</html>
