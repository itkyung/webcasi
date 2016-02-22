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

	var needRequest = ${needRequest};
	

	$(document).ready(function(){
		if(needRequest){
			/* $.fancybox.open({
				href : _requestPath + "/member/protectionForm",type : 'iframe', 
				openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 700,height : 600
			});		 */
		}
	});
</script>


</head>
<body>


	<div class="main">
		<div class="content">
			<div class="startPage">
				<div class="sp_left">
					<img src="${pageContext.request.contextPath}/resources/assets/images/startPage_01.png"/>
					<span class="nameBox">${webcasi:currentUser().name}</span>
					<img class="mgLeft10" src="${pageContext.request.contextPath}/resources/assets/images/startPage_04.png"/>
				</div>
				<div class="sp_right">
					<img src="${pageContext.request.contextPath}/resources/assets/images/startPage_03.png"/>
				</div>
			</div>
			<c:url var="startCheckupUrl" value="/member/checkup/category/CHECK_LIST"/>
			
				<c:choose>
					<c:when test="${currentStatus eq 'CLOSED'}">
						<div class="main-restart">
							<div class="info-msg">귀하는 ${currentYear}년 스마트 문진 응답을 마치셨습니다.<br> 감사합니다.</div>
						</div>
					</c:when>
					<c:when test="${currentStatus eq 'COMPLETED'}">
						<div class="main-restart">
							<div class="info-msg">귀하는 ${currentYear}년 스마트 문진 응답을 마치셨습니다.<br> 감사합니다.</div>
						</div>
					</c:when>		
					<c:when test="${currentStatus eq 'READY' || currentStatus eq 'IN_PROGRESS'}">
						<c:choose>
							<c:when test="${canStart eq false}">
								<div class="main-restart">
									<div class="info-msg">건진예약 2주 전부터 스마트문진을 시작할 수 있습니다.</div>
								</div>
							</c:when>
							<c:otherwise>
								<div class="main-home-action">
									<div class="info-button"><a href="${startCheckupUrl}?needRequest=${needRequest}"><img src="${pageContext.request.contextPath}/resources/images/page_bt_start.png"/></a></div>
								</div>
								<div class="main-desc">
									<div class="main-desc-row">
										1. 문진 시작 전 "스마트 문진 쉽게 따라하기"를 클릭하시면 문진 작성방법에 대해 안내받으실 수 있습니다.
									</div>
									<div class="main-desc-row">
										2. 문진 응답은 자동으로 저장되며, 귀하의 건진 당일까지 로그인을 통해 수정 가능합니다.
									</div>
									<div class="main-desc-row">
										3. "문진 진행사항"을 클릭하시면 문진 진행 정도를 실시간으로 확인하실 수 있습니다.
									</div>
									<div class="main-desc-row">
										4. 수진자의 편의를 위해 이전 건진때 체크된 내용 중 일부는 그대로 보여집니다.<br>
										　변동사항이 있다면 다시 작성하여 주시기 바랍니다.
									</div>
									<br>
									<div class="main-desc-row">
										5. 마지막 문진 응답 후 20분이 지나면 자동으로 로그아웃 됩니다.
									</div>
								</div>
							</c:otherwise>
						</c:choose>
						
					</c:when>	
					<c:when test="${currentStatus eq 'FIRST_COMPLETED'}">
						<div class="main-restart">
							<div class="info-msg">귀하는 ${currentYear}년 스마트 문진 응답을 마치셨습니다.<br> 문진 응답은 자동으로 저장되며, 귀하의 건진 당일까지<br>로그인을 통해 수정 가능합니다.</div>
						</div>
						<div class="main-home-action">
							<div class="info-button"><a href="${startCheckupUrl}?needRequest=${needRequest}"><img src="${pageContext.request.contextPath}/resources/images/bt_restart2.png"/></a></div>
						</div>
					</c:when>			
				</c:choose>
				
<!-- 				
			<div class="main-desc">
				<div class="main-desc-row">
					1. 문진 시작 전 "스마트 문진 쉽게 따라하기"를 클릭하시면 문진 작성방법에 대해 안내받으실 수 있습니다.
				</div>
				<div class="main-desc-row">
					2. 문진 응답은 자동으로 저장되며, 귀하의 건진 당일까지 로그인을 통해 수정 가능합니다.
				</div>
				<div class="main-desc-row">
					3. "문진 진행사항"을 클릭하시면 문진 진행 정도를 실시간으로 확인하실 수 있습니다.
				</div>
				<div class="main-desc-row">
					4. 수진자의 편의를 위해 이전 건진때 체크된 내용 중 일부는 그대로 보여집니다.<br>
					　변동사항이 있다면 다시 작성하여 주시기 바랍니다.
				</div>
				<br>
				<div class="main-desc-row">
					5. 마지막 문진 응답 후 20분이 지나면 자동으로 로그아웃 됩니다.
				</div>
			</div>
 -->			
		</div>
	</div>


</body>
</html>