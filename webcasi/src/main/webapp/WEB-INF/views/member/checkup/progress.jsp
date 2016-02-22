<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>

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
		
		
	</script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.ui.monthpicker.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.masonry.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/member/progress.js"></script>
</head>
<body id="second-body">
	<div id="progress-wrapper">
		<c:forEach var="entity" items="${progressInfo}" >
			
			<div class="progress-entity">
				<div class="progress-category">
					${entity.title}
				</div>
				<div class="progress-groups">
					<c:forEach var="group" items="${entity.questionGroups}" varStatus="status">
						<c:set var="cssPostfix" scope="request">
							<c:choose>
								<c:when test="${status.index eq 0 || status.index % 2 eq 0}">
									<c:out value="even"/>
								</c:when>
								<c:otherwise>
									<c:out value="odd"/>
								</c:otherwise>
							</c:choose>
						</c:set>
						<c:choose>
							<c:when test="${group.processed eq true}">
								<div class="progress-group processed ${cssPostfix}">
									<a href="javascript:goGroup('${group.id}');">${group.title}(완료)</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="progress-group ${cssPostfix}">
									${group.title}
								</div>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</div>
			</div>
		</c:forEach>
	</div>
</body>
</html>