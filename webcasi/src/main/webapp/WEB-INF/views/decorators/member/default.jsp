<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.springframework.context.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib uri="http://myhealth.kbsmc.co.kr/taglibs" prefix="webcasi" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<c:url value="/logout" var="logoutUrl"/>
<head> 
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"> 
	<title>Web CASI</title>
	
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
	
	<c:if test="${_isMobile eq true}">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/common_mobile.css"/>	
	</c:if>
	
	<c:choose>
		<c:when test="${_isMobile eq true}">
			<script>
				var widthConstants = 516;
			</script>
		</c:when>
		<c:otherwise>
			<script>
				//var widthConstants = 260;
				var widthConstants = 516;
			</script>
		</c:otherwise>
	</c:choose>
	
	<script>
		var syncFlag = false;
	
		var _isMobileDevice = "${_isMobile}";
		
		var _requestPath = "${pageContext.request.contextPath}";
		
		function _updateProgress(progress){
			var _width = widthConstants * Number(progress) / 100; 
			$("#global-progress-bar").css("width",_width);
			
			$("#global-progress").text(progress+"%");
		}
		
		$(document).ready(function(){
			_updateProgress("${_progress}");
			
			 document.oncontextmenu = function(){
			     return false;
			 };
			
			 document.ondragstart = function(event){
				if(event.srcElement.type != "text" && event.srcElement.type != "textarea"){
					return false;
				}
			 };
			
			 document.onselectstart = function(event){
				 if(event.srcElement.type != "text" && event.srcElement.type != "textarea"){
						return false;
				}
			 };
			
			 
			
		});
		
		function _viewProgress(){

			$.fancybox.open({
				href : _requestPath + "/member/checkup/viewProgress",type : 'iframe', 
				openEffect : 'elastic',closeEffed : 'elastic', autoSize : false,width : 850,height : 650
			});	
			
		}
		
		function _goDirectGroup(groupId){
			$.fancybox.close();
			document.location.href = _requestPath + "/member/checkup/questionGroup/" + groupId;
		}
		
		function _logout(){
			
			alert("현재까지 작성한 문진은 모두 저장되었습니다.");
			
			if(typeof _questionGroupId != "undefined"){
				_syncQuestionGroup(_questionGroupId,function(){
					var url = "${logoutUrl}";
					try{
						window.location = url;
					}catch(e){
						
					}
				});
			}else{
				var url = "${logoutUrl}";
				document.location.href = url;
			}
		}
		
		function _syncQuestionGroup(questionGroupId,callback){
			
			$.ajax({
				dataType:  'json', 
				type : 'GET',
				async : false,
				cache : false,
				url : _requestPath + '/member/checkup/syncQuestionGroup/' + questionGroupId,
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
					//alert(err);
				}
			});
		}
		
		function closeBox(){
			$.fancybox.close();
		}
	</script>
	
	<decorator:head />
</head>

<body>
	<div id="container">
		<div id="mask"></div>
		<div id="main-container">
			<div id="main-header">
				<div class="width-1024">
					<a href="${pageContext.request.contextPath}/member/goHome"><div id="header-logo"></div></a>
					<div id="header-progress">
						<div id="header-progress-bar" title="현재 ${_progress}% 문진 응답이 완료되었습니다.">
							<div id="global-progress-empty"></div>
							<div id="global-progress-start"></div>
							<div id="global-progress-bar"></div>
							<div id="global-progress-end"></div>
							<div id="global-progress">${_progress} %</div>
						</div>
						<div id="header-progress-btn">
							<a href="javascript:_viewProgress();"><img src="${pageContext.request.contextPath}/resources/images/page_top/bt_process.png" title="클릭하시면 문진 리스트 및 진행 정도를 확인하실 수 있습니다."/></a>
						</div>
					</div>
					<div id="header-userinfo">
						<div class="user-name">
							${webcasi:currentUser().name} 님
						</div>
						<div class="logout">
							<a href="javascript:_logout();"><img src="${pageContext.request.contextPath}/resources/images/page_top/bt_logout.png"/></a>
						</div>
					</div>
				</div>
			</div>
			<div id="main-body-wrapper">
				<div class="width-1024">
					<div id="main-left">
						<%@ include file="/WEB-INF/views/decorators/leftMenu.jspf" %>
					</div>		
					<div id="main-contents">
						<div class="current-path">
							<div class="left">* ${_path}</div>
							<div class="right">예약일 : ${_hopeDate}</div>	
						</div>
						<div id="main-checkup-body">
							<decorator:body />
						</div>
					</div>
				</div>
			</div>
			<c:if test="${_isMobile eq false}">
				<div id="main-footer">
					<div class="width-1024">
						<%@ include file="/WEB-INF/views/decorators/footer.jspf" %>
					</div>
				</div>
			</c:if>
		</div>
		<div id="second-depth-question-container" class="hidden">
			<iframe id="second-depth-frame" name="second-depth-frame" marginwidth="0" marginheight="0" frameborder="0" style="width:880px;height:550px;"></iframe>
		</div>
	</div>
</body>
<script>
	$(document).ready(function(){
		var height = $("#main-contents").height();
		if(height > 650){
			$("#main-body-wrapper .width-1024").height(height+10);
			$("#main-left").height(height);
			
		}
		<c:if test="${_isMobile eq false}">
			var browserHeight = $(window).height(); 
			var mainBodyWrapperHeight = $("#main-body-wrapper").height();
			if((browserHeight-45-50) > mainBodyWrapperHeight){
				$("#main-body-wrapper").height(browserHeight-45-50);
			}
			
		</c:if>
	});
</script>
</html>