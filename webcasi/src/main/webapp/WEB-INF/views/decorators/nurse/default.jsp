<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.context.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>Web CASI-간호사</title>
	<script>
		var _requestPath = "${pageContext.request.contextPath}";
		var _nurseEditable = "${nurseEditable}";
		var syncFlag = false;
		
		function _updateProgress(progress){
			
		}
		
	</script>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/smoothness/jquery-ui-1.9.1.custom.min.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common.css"/>	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jquery.jscrollpane.css" media="all" />
		
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery-ui-1.9.1.custom.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/fancybox/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/common.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.mousewheel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jquery.jscrollpane.min.js"></script>	
	<script>
		var _isMobileDevice = "${_isMobile}";
		var _requestPath = "${pageContext.request.contextPath}";
		
		$(document).ready(function(){
			$("#history").change(function(){
				var id = $("#history option:selected").val();
				if( id != "-1"){
					document.location.href = _requestPath + "/nurse/view/${patientNo}?instanceId=" + id;
				}
			});	
		});
		
		function _syncQuestionGroup(questionGroupId,callback){
			if(_nurseEditable == true){
				$.ajax({
					dataType:  'json', 
					type : 'GET',
					async : false,
					cache : false,
					url : _requestPath + '/nurse/syncQuestionGroup/' + questionGroupId + "?instanceId=" + _instanceId,
					timeout : 5000,
					beforeSubmit : function(){
						
					},				
					success : function(result){
						var success = result.success;
						if(success){
							if(callback != undefined){
								callback();
							}
						}else{
							alert(result.msg);
						}
					},
					error : function(response, status, err){
						alert(err);
					}
				});
			}else{
				callback();
			}
		}
	</script>
	<decorator:head />
</head>

<body>
	<div id="mask"></div>
	<div id="main-container">
		<div id="main-header">
			<div class="width-1024">
				<a href="${pageContext.request.contextPath}/member/goHome"><div id="header-logo"></div></a>
				<div id="nurse-header-right">
					<div id="nurse-header-userinfo">
						<span class="nurse-title">이름</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.name}</span>
						
						<span class="nurse-title">수진번호</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.patNo}</span>
						<span class="nurse-title">나이</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.age}</span>
						<span class="nurse-title">성별</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.gender}</span>
						<span class="nurse-title">예약일자</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.reserveDate}</span>
						<span class="nurse-title">접수일자</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.acptDate}</span>
						<span class="nurse-title">주민번호</span>
						<span class="nurse-line"></span>
						<span class="nurse-title-value">${patientInfo.socialNumber}</span>
					</div>
					<div id="nurse-direct">
						
					</div>
				</div>
			</div>
		</div>
		<div id="main-body-wrapper">
			<div class="width-1024">
				<div id="main-left">
					<%@ include file="/WEB-INF/views/decorators/nurseLeftMenu.jspf" %>
				</div>	
				<div id="main-contents">
					<div class="current-path">
						<span>
							<!--  <img src="${pageContext.request.contextPath}/resources/images/header_admin.png" class="text-middle"/> -->
							과거검진이력(접수일) : 
						</span>
						<span>
							<select id="history" name="history">
								<option value="-1">--------</option>
								<c:forEach var="row" items="${history}">
									<option value="${row.id}">${row.acptDateStr}</option>
								</c:forEach>
							</select>
						</span>
					
					</div>
					<div id="main-checkup-body">
						<decorator:body />
					</div>
				</div>
			</div>
		</div>
		<div id="main-footer">
			<div class="width-1024">
				<%@ include file="/WEB-INF/views/decorators/footer.jspf" %>
			</div>
		</div>
	</div>
	<div id="second-depth-question-container" class="hidden">
		<iframe id="second-depth-frame" name="second-depth-frame" marginwidth="0" marginheight="0" frameborder="0" style="width:880px;height:550px;"></iframe>
	</div>
</body>
</html>