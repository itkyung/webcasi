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
		var _nurseViewFlag=false;
		var _nurseEditable = false;
		var _instanceId = "";
		var _patientAge = "${patientAge}";
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/custom/family2Depth.js"></script>
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
	
	<!--[if IE]>
   		<div style="margin-right:20px;">
	<![endif]-->
	
	<!--[if !IE]><!-->
   	<div>
	<!-- <![endif]-->
		<table class="family-table">
			<tr>
				<td class="family-long-col">질환</td>
				<td class="family-medium-col">부</td>
				<td class="family-medium-col">모</td>
				<td class="family-medium-col">형제자매</td>
				<td class="family-medium-col">자녀</td>
				<td class="family-short-col">친조부모</td>
				<td class="family-short-col">4촌이내<br>(친가)</td>
				<td class="family-short-col">외조부모</td>
				<td class="family-short-col">4촌이내<br>(외가)</td>
			</tr>
		</table>
	</div>
	<div id="family-contents" style="overflow-y:auto;overflow-x:hidden;height:365px;">
		<table class="family-table">
			<c:forEach var="item" items="${questionRow.childItems}" varStatus="itemStatus">
				<c:if test="${item.active eq true}">
				<tr>
				<c:choose>		
					<c:when test="${item.type eq 'CHECK'}">
						<td class="family-long-col">${item.title}</td>
					</c:when>
					<c:when test="${item.type eq 'CHECK_SUBJ'}">
						<c:set var="resultStr" value="${resultMap[item.id]}"/>
						<td class="family-long-col">${item.title}
							<input type=text questionId="${item.parentQuestion.id}" size=7 itemId="${item.id}"  itemType="${item.type}" 
									existChild="false" validator="${item.validator}" value="${resultStr}">
						</td>
					</c:when>
				</c:choose>
				<c:forEach var="subItem" items="${subItems[item.id]}" varStatus="subItemStatus">
					<c:set var="subColClass" value=""/>
					<c:choose>
						<c:when test="${subItemStatus.index >= 0 && subItemStatus.index <= 3}">
							<c:set var="subColClass" value="family-medium-col"/>
						</c:when>
						<c:otherwise>
							<c:set var="subColClass" value="family-short-col"/>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${subItem.active eq false}">
							<td class="${subColClass}"></td>
						</c:when>
						<c:when test="${subItem.active eq true && subItem.type eq 'CHECK'}">
							<td class="${subColClass}">
								<c:choose>
									<c:when test="${subItem.checked}">
										<input questionId="${subItem.parentQuestion.id}" id="${subItem.id}" type="checkbox" 
											value="${subItem.id}" existChild="false" itemType="CHECK" 
											noneFlag="false" checked>
									</c:when>
									<c:otherwise>
										<input questionId="${subItem.parentQuestion.id}" id="${subItem.id}" type="checkbox" 
											value="${subItem.id}" existChild="false" itemType="CHECK" 
											noneFlag="false">
									</c:otherwise>
								</c:choose>
								
							</td>
						</c:when>
						<c:when test="${subItem.active eq true && subItem.type eq 'CHECK_SUBJ'}">
							<td class="${subColClass}">
								<c:choose>
									<c:when test="${subItem.checked}">
										<input questionId="${subItem.parentQuestion.id}" id="${subItem.id}" type="checkbox" 
											value="${subItem.id}" existChild="false" itemType="CHECK" 
											noneFlag="false" checked>
									</c:when>
									<c:otherwise>
										<input questionId="${subItem.parentQuestion.id}" id="${subItem.id}" type="checkbox" 
											value="${subItem.id}" existChild="false" itemType="CHECK_SUBJ" 
											noneFlag="false">
									</c:otherwise>
								</c:choose>
								<br/>
								${subItem.preText}
								<br/>
								<input type=text questionId="${subItem.parentQuestion.id}" itemId="${subItem.id}"  itemType="${subItem.type}" size=1 
									existChild="false" validator="${subItem.validator}" value="${subItem.strValue}">
								${subItem.postText}
								
							</td>
						</c:when>
					</c:choose>
				</c:forEach>
				</tr>
				</c:if>
			</c:forEach>
		</table>
	</div>
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
